#include "global.h"
#include "load.h"
#include "node.h"
#include "route.h"
#include "station.h"
#include "train.h"
#include "track.h"
#include "hub.h"
#include "weatherSystem.h"
#include <string>
#include <sstream>
#include <fstream>

#define LOAD_PROCESS_TIME 60
#define CREW_PROCESS_TIME 60
#define REFUEL_TIME 60

ofstream maxxFile("stats.csv");
ofstream neroFile("output.txt");

using namespace std;

int currentNodeID = 0; 
int currentTrackID = 0; 
int currentTrainID = 0; 
int currentFreightID = 0; 
int currentPassID = 0; 
int simDay = 1; //don't change this again you twat
day simWeekday;
milTime realTime = { 00,00 };
string rule = "============================================================================\n";


//UTILITY METHODS

	//Utility for looping isDigit
	bool stringIsNumber(string in)
	{
		for (int j = 0; j < in.length(); j++)
		{
			if (isdigit(in[j]) == false)
			{
				return false;
			}
		}
		return true;
	}

	//Calculates minutes until B relative to A (may be negative)
	int minutesUntil(milTime A, milTime B) //Time UNTIL B relative to A
	{
		if (A == B) return 0;
		int minutes = B.min - A.min;
		minutes = minutes + (60 * (B.hour - A.hour));
		return minutes;
	}

	//Print time in a nice format
	void printTime(milTime when)
	{
		cout << right << setfill('0') << setw(2) << "TIME: " << when.hour << ":" << setw(2) << when.min << endl;
		cout << left << setfill(' ');
	}

	void printDay()
	{
		switch (simWeekday)
		{
		case MON:
			cout << "Monday, ";
			break;

		case TUE:
			cout << "Tuesday, ";
			break;

		case WED:
			cout << "wednesday, ";
			break;

		case THR:
			cout << "Thursday, ";
			break;

		case FRI:
			cout << "Friday, ";
			break;

		case SAT:
			cout << "Saturday, ";
			break;

		case SUN:
			cout << "Sunday, ";
			break;

		default:
			cout << "printDay encountered simWeekday value that is not valid monday-thru-sunday value" << endl;
			return;
		}

		cout << "day " << simDay << endl;
	}

	errorState err;

	void resetErr()
	{
		//Do not clear highError since these will end the program
		err.lowError = false;
		err.message.clear();
		err.code = 0;
	}

	void nextDay(day &in)
	{
		switch (in)
		{
		case MON:
			in = TUE;
			break;

		case TUE:
			in = WED;
			break;

		case WED:
			in = THR;
			break;

		case THR:
			in = FRI;
			break;

		case FRI:
			in = SAT;
			break;

		case SAT:
			in = SUN;
			break;

		case SUN:
			in = MON;
			break;

		default:
			cout << "nextDay encountered value that is not valid monday-thru-sunday value" << endl;
			break;
		}
	}

	void runClock(int amount)
	{
		realTime.min += amount;
		if (realTime.min >= 60)
		{
			realTime.hour = realTime.hour + (realTime.min / 60);
			realTime.min = realTime.min % 60;
		}
		if (realTime.hour == 24 && realTime.min == 00)
		{
			realTime.hour = 0;
			nextDay(simWeekday);
			simDay++;
		}
	}

//FILE PARSING INFO CONTAINERS

	//Info passed by java frontend
	struct simulationParameters
	{
		float hubFuel = 10000.00;		// :^) not used anymore but don't delete it because i don't feel like dealing with it

		int tickMinutes = 1;			//Probably leave this at 1

		int trainSpeed;					//Weight units per hour
		float trainFuelPerWeight;		//fuel cost per weight unit
		int passengerCap;				//Passenger train capacity
		int preemptiveCrewTime;			//When refuelling, if the crew is beyond this time (minutes), they will be swapped to avoid swapping later
		float cargoPrice;				//per-amount price for cargo transport. Revenue for us
		float fuelCost;					//Cost of 1.00% of a fuel tank

		int preemptiveLoadDispatchTime;	//Dispatch a train when a load has (this) much time left before pickup time (Do not set low or you'll always be late)

		day startDay;			//Monday, tuesday...
		milTime startTime;		//Hour,Min
		int duration;			//Days

		bool weatherToggle;		// :^)
		int weatherType;		// 0 is least bad, 3 is snow
		int weatherSeverity;	//0 to 100 i think
		int maxWeatherDelay;	//Natural maximum is 100 on worst settings
	};

	struct nodeDetails
	{
		bool isHub;
		string name;
		string type;
		string trainMax;
		string peopleOn1;
		string peopleOn2;
		string peopleOff1;
		string peopleOff2;
		string ticketPrice;
	};

	struct edgeDetails
	{
		string node1;
		string node2;
		string weight;
		string startTime;
		string endTime;
	};

	struct trainDetails
	{
		string name;
		string homeHub;
		string type;
	};

	struct freightDetails
	{
		string station1;
		string station2;
		string when;
		string startTime;
		string capacity;
	};

	struct routeDetails
	{
		string station;
		string time;
	};

	struct passengerDetails
	{
		string when;
		vector<routeDetails> stations;
	};

	struct edgeMaintenanceDetails
	{
		string day;

		string node1;
		string node2;
		string daysDown;
	}; 

	struct trainMaintenanceDetails
	{
		string day;

		string name;
		string daysDown;
	}; 


	struct maintenanceRecTrain 
	{
		train* affected;
		int day;			//Which numeric day this will occur
	};

	struct maintenanceRecTrack 
	{
		track* affected;
		int day;			//Which numeric day this will occur
	};


//SIMULATION INFORMATION CONTAINERS

	//Contains pointers to all tracks
	vector<track*> tracks;

	//Contains node pointers to all stations and hubs. Can be static casted to 
	//utilize station/hub specific methods
	vector <node*> nodes;

	//Contains pointers to all trains
	vector<train*> trains;

	//Contains pointers to all loads
	vector <load*> loads;
	vector <load*> completeLoads; //This program :^)

	//Contains a permanent record of the node vector in the form of
	//records able to be used in pathfinding. 
	//node IDs match to index of that item
	vector<route::record> masterRecords;

	//contains maintenance records for trains
	vector<maintenanceRecTrain> trainMaintenance;

	//contains maintenance records for trains
	vector<maintenanceRecTrack> trackMaintenance;


//INITIALIZATION METHODS

	node* findNodeByName(string name)
	{
		node* output = NULL;
		vector<node*>::iterator nameIter = nodes.begin();
		for (int n = 0; nameIter < nodes.end(); nameIter++, n++)
		{
			if (name == nodes[n]->getName())
			{
				output = nodes[n];
				break;
			}
		}
		return output;
	}

	milTime stringToTime(string time)
	{
		stringstream parser(time);
		int hour = 0, min = 0;
		parser >> hour;
		parser >> min;
		return milTime{ hour,min };
	}

	//Creates every hub and station object
	void buildNodes(vector<nodeDetails> input, simulationParameters param)
	{
		for (int i = 0; i < input.size(); i++)
		{
			if (input[i].isHub == true)
			{
				nodes.push_back(new hub(input[i].name, currentNodeID, param.hubFuel));
			}
			else
			{
				int randOnLow = stoi(input[i].peopleOn1);
				int randOnHi  = stoi(input[i].peopleOn2);
				int randOffLo = stoi(input[i].peopleOff1);
				int randOffHi = stoi(input[i].peopleOff2);
				float ticket  = stof(input[i].ticketPrice);

				loadType type = FREIGHT;
				if (input[i].type == "p" || input[i].type == "P") type = PASSENGER;

				nodes.push_back(new station(input[i].name, currentNodeID, STATION, type, 0, randOnHi, randOnLow, randOffHi, randOffLo, ticket));
			}
			currentNodeID++;
		}
	}

	//Creates every track and hubtrack object
	void buildTracks(vector<edgeDetails> input)
	{
		for (int i = 0; i < input.size(); i++)
		{
			node* connectionA = findNodeByName(input[i].node1);
			node* connectionB = findNodeByName(input[i].node2);

			if (connectionA == NULL)
			{
				cout << "buildTracks could not find node " << input[i].node1 << endl;
				err.highError = true;
				return;
			}

			if (connectionB == NULL)
			{
				cout << "buildTracks could not find node " << input[i].node2 << endl;
				err.highError = true;
				return;
			}

			int startHour = 0, startMinute = 0, endHour = 0, endMinute = 0;

			milTime start = stringToTime(input[i].startTime);
			milTime close = stringToTime(input[i].endTime);

			int weight = stoi(input[i].weight);

			nodeType type = TRACK;
			if (connectionA->getNodeType() == HUB || connectionB->getNodeType() == HUB) type = HUBTRACK;

			tracks.push_back(new track("Track" + to_string(currentTrackID), currentTrackID, type, connectionA, connectionB, weight, start, close));
			currentTrackID++;
		}
	}

	//Connects every track to its assigned nodes
	void connectNodesToTracks()
	{
		for (int i = 0; i < tracks.size(); i++)
		{
			err.highError = tracks.at(i)->getConnection(0)->addConnection(tracks.at(i));
			err.highError = tracks.at(i)->getConnection(1)->addConnection(tracks.at(i));
		}
	}

	//Create all train objects
	void buildTrains(vector<trainDetails> input, simulationParameters param)
	{
		for (int i = 0; i < input.size(); i++)
		{
			node* home = findNodeByName(input[i].homeHub);
			loadType type = FREIGHT;

			if (home == NULL)
			{
				cout << "buildTrains could not find node " << input[i].homeHub << endl;
				err.highError = true;
				return;
			}

			if (input[i].type == "p" || input[i].type == "P") type = PASSENGER;

			trains.push_back(new train(input[i].name, currentTrainID, type, param.trainSpeed, home, param.passengerCap, param.trainFuelPerWeight, param.cargoPrice));
			currentTrainID++;
		}
	}

	//Creates the records needed for pathfinding based on the railway
	void buildMasterRecord()
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			route::record newRec{ nodes[i], NULL, INT_MAX };
			masterRecords.push_back(newRec);
		}
	}

	//Causes each station to generate an ordered vector of which hub is closest
	int calculateHubDistances()
	{
		int numHubs = 0;
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes[i]->getNodeType() != STATION)
			{
				numHubs++;
				continue; //skip non-stations
			}
		
			else
			{
				for (int j = 0; j < nodes.size(); j++)
				{
					if (nodes[j]->getNodeType() != HUB) continue; //This is unbelievably expensive but whatever its a one-off setup method
					else
					{
						route hubDistanceFinder;
						vector<route::record> hubPath = hubDistanceFinder.AstarPath(nodes[i], nodes[j], masterRecords);

						int distance = hubPath[0].distance; //First (logically final) step of a path contains the total distance
						static_cast<station*>(nodes[i])->addHubDistance(nodes[j], distance);
					}
				}
			}
		}
		return numHubs;
	}

	//Figures out the largest distance between a station and a hub that exists, converts it to a padded value of minutes
	//This value will be used to determine when trains must return home for the night
	int calculateWorstCaseHomeTime(int numHubs, simulationParameters param)
	{
		int worst = 0;

		for (int n = 0; n < nodes.size(); n++)
		{
			if (nodes[n]->getNodeType() == STATION)
			{
				station* st = static_cast<station*>(nodes[n]);
				
				if (worst < st->getFarthestHubDistance()) worst = st->getFarthestHubDistance();
			}
		}

		int worstTravelTimeMinutes = worst / param.trainSpeed;
		worstTravelTimeMinutes += LOAD_PROCESS_TIME + 15; //Pad time
		return worstTravelTimeMinutes;
	}

	//Creates a record for handling maintenance
	void buildMaintenanceRecords(vector<edgeMaintenanceDetails> inTracks, vector<trainMaintenanceDetails> inTrains)
	{
		for (int i = 0; i < inTrains.size(); i++)
		{
			int day = stoi(inTrains[i].day);
			int down = stoi(inTrains[i].daysDown);
			train* affected = NULL;

			//Find what train this is
			for (int j = 0; j < trains.size(); j++)
			{
				if (trains[j]->getName() == inTrains[i].name)
				{
					affected = trains[j];
					break;
				}
			}

			if (affected == NULL)
			{
				cout << "buildMaintenanceRecords could not find the train matching name: " << inTrains[i].name << endl;
				err.highError = true;
				return;
			}

			//Create the new record
			maintenanceRecTrain newRec{ affected, day };
			trainMaintenance.push_back(newRec);

			//For as many days as this maintenance will last, write that many records
			while (down > 1)
			{
				(newRec.day)++;
				trainMaintenance.push_back(newRec);
				down--;
			}
		}


		for (int i = 0; i < inTracks.size(); i++)
		{
			int day = stoi(inTracks[i].day);
			int down = stoi(inTracks[i].daysDown);
			track* affected = NULL;

			//Figure out which track this is
			for (int j = 0; j < tracks.size(); j++)
			{
				if (((tracks[j]->getConnection(0)->getName() == inTracks[i].node1) || (tracks[j]->getConnection(0)->getName() == inTracks[i].node2)) &&
					((tracks[j]->getConnection(1)->getName() == inTracks[i].node1) || (tracks[j]->getConnection(1)->getName() == inTracks[i].node2)))
				{
					affected = tracks[j];
					break;
				}
			}

			if (affected == NULL)
			{
				cout << "buildMaintenanceRecords could not find the track matching node connections: " << 
														inTracks[i].node1 << " to " << inTracks[i].node2 << endl;
				return;
			}

			//Create the new record
			maintenanceRecTrack newRec{ affected, day };
			trackMaintenance.push_back(newRec);

			//For as many days as this maintenance will last, write that many records
			while (down > 1)
			{
				(newRec.day)++;
				trackMaintenance.push_back(newRec);
				down--;
			}
		}
	}

	//Creates the loads (repeatable routes) to be assigned in the simulation
	void buildLoads(vector<freightDetails> inFreight, vector<passengerDetails> inPass, day simStartDay, simulationParameters param)
	{
		//Freight loop
		for (int i = 0; i < inFreight.size(); i++)
		{
			node* spawn = findNodeByName(inFreight[i].station1);
			node* dest  = findNodeByName(inFreight[i].station2);

			vector<bool> whichDays;
			if (inFreight[i].when == "F" || inFreight[i].when == "f")
			{
				for (int w = 0; w < param.duration; w++)
				{
					whichDays.push_back(true);
				}
			}
			else
			{
				for (int w = 0; w < 7; w++)
				{
					if (simStartDay == SUN || simStartDay == SAT) whichDays.push_back(false);
					else whichDays.push_back(true);
					nextDay(simStartDay);
				}
			}

			milTime start = stringToTime(inFreight[i].startTime);

			int amount = stoi(inFreight[i].capacity);

			if (spawn == NULL)
			{
				cout << "buildLoads could not find station named " << inFreight[i].station1 << endl;
				err.highError = true;
				return;
			}

			if (dest == NULL)
			{
				cout << "buildLoads could not find station named " << inFreight[i].station2 << endl;
				err.highError = true;
				return;
			}

			int whatDay = 1;
			for (int w = 0; w < param.duration; w++)
			{
				if (whichDays[w % 7] == true)
				{
					string name = "Freight#" + to_string(currentFreightID) + "_day" + to_string(whatDay);
					loads.push_back(new load(name, currentFreightID, amount, spawn, dest, start, whatDay));
				}
				whatDay++;
			}
			currentFreightID++;
		}
		
		//Passenger loop
		for (int i = 0; i < inPass.size(); i++)
		{
			vector<bool> whichDays;
			cout << inPass[i].when << endl;
			if (inPass[i].when == "F" || inPass[i].when == "f")
			{
				for (int w = 0; w < param.duration; w++)
				{
					whichDays.push_back(true);
				}
			}
			else
			{
				for (int w = 0; w < 7; w++)
				{
					if (simStartDay == SUN || simStartDay == SAT) whichDays.push_back(false);
					else whichDays.push_back(true);
					nextDay(simStartDay);
				}
			}

			vector<node*> stationList;
			vector<milTime> timesList;

			for (int j = inPass[i].stations.size() - 1; j >= 0 ; j--)
			{
				node* currentStation = findNodeByName(inPass[i].stations[j].station);
				milTime currentTime = stringToTime(inPass[i].stations[j].time);

				if (currentStation == NULL)
				{
					cout << "buildLoads could not find station named " << inPass[i].stations[j].station << endl;
					err.highError = true;
					return;
				}

				stationList.push_back(currentStation);
				timesList.push_back(currentTime);
			}
			int whatDay = 1;
			for (int w = 0; w < param.duration; w++)
			{
				if (whichDays[w % 7] == true)
				{
					string name = "Passenger#" + to_string(currentPassID) + "_day" + to_string(whatDay);
					loads.push_back(new load(name, currentPassID, stationList, timesList, whatDay));
				}
				whatDay++;
			}
			currentPassID++;
		}
	}

	//Insertion-sorts all loads so that the earliest load is at the top of the container
	void sortLoads() //Uses insertion sort
	{
		load* swapper = NULL;
		int i = 1;

		while (i < loads.size())
		{
			int j = i;

			while ((j > 0) && (loads[j - 1]->getBegin() < loads[j]->getBegin()))
			{
				swapper = loads[j - 1];
				loads[j - 1] = loads[j];
				loads[j] = swapper;
				j--;
			}
			i++;
		}

		i = 1;

		while (i < loads.size())
		{
			int j = i;

			while ((j > 0) && (loads[j - 1]->getDay() < loads[j]->getDay()))
			{
				swapper = loads[j - 1];
				loads[j - 1] = loads[j];
				loads[j] = swapper;
				j--;
			}
			i++;
		}

	}


//TRAIN PROCESSING METHODS AND SIMULATION METHODS
	//variable timeAllowance represents how many minutes (maximum = ticklength)
	//the train has to work with. An example of where this is useful is when a train
	//enters a station with time to spare on the current tick, and may spend the remaining
	//time on running down the station processing timer
	//Allowance is spent 1 minute at a time.

	//Triggers route generation in a train to an assigned destination
	void assignRoute(train* loco, node* dest)
	{
		node* src = loco->getLocation();
		route pathfind;
		vector<route::record> path = pathfind.AstarPath(src, dest, masterRecords);
		loco->deleteRoute();
		loco->setPath(path);
	}

	void IDLEtrainProc(train* theTrain, int* timeAllowance)
	{
		if (theTrain->getState() != IDLE)
		{
			cout << "main::trainProcessing sent a train into IDLE proc that is not IDLE" << endl;
			err.highError = true;
			return;
		}
		//TODO: do statistics stuff here
	}

	void SEEKtrainProc(train* theTrain, int* timeAllowance, simulationParameters param, int weather)
	{
		if (theTrain->getState() != SEEK)
		{
			cout << "main::trainProcessing sent a train into SEEK proc that is not SEEK" << endl;
			err.highError = true;
			return;
		}
		if (theTrain->hasLoad())
		{
			cout << "main::SEEKtrainProc encountered train " << theTrain->getName() << " at " << theTrain->getLocation()->getName() << " that already has a load." << endl;
			err.highError = true;
			return;
		}
		if (theTrain->getCrewTime() >= param.preemptiveCrewTime && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB))
		{
			//If crew time is getting high, stop to swap
			theTrain->setState(WAIT);
			theTrain->setWait(CREW_PROCESS_TIME, weather);
			theTrain->setExitState(SEEK);
			theTrain->setSwapFlag();
			hub* home = static_cast<hub*>(theTrain->getHome());
			home->recordDispatch();
			return;
		}
		if (theTrain->getFuel() < 10.0f && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB) )
		{
			theTrain->setState(FUEL); //If train is low on fuel, set state to refuel, and also i dont know how to handle this on a track.
			theTrain->setExitState(SEEK);
			return;
		}

		if (theTrain->hasPath() == false) //If train needs a path, give it one
		{
			assignRoute(theTrain, theTrain->getLoadSought()->getSpawn());
		}

		//If we got here, we should be en route
		theTrain->move(timeAllowance);

		if (theTrain->getLoadSought()->getSpawn() == theTrain->getLocation()) //If we found the load
		{
			theTrain->getLocation()->statsTrainStop();

			station* st = static_cast<station*>(theTrain->getLocation());

			st->recordPickup();

			if (realTime >= theTrain->getLoadSought()->getBegin())
			{
				theTrain->setWait(LOAD_PROCESS_TIME); //hour long processing time
			}
			else //If we got here early
			{
				int extraWait = minutesUntil(realTime, theTrain->getLoadSought()->getBegin());
				theTrain->setWait(LOAD_PROCESS_TIME + extraWait, weather);
			}
			theTrain->setState(WAIT);
			theTrain->setExitState(HAUL);
			theTrain->pickupLoad(realTime);

			if (theTrain->getType() == PASSENGER)
			{
				int minOn = 0, maxOn = 0, minOff = 0, maxOff = 0;
				st->getBoardingInfoRef(&minOn, &maxOn, &minOff, &maxOff);
				float price = st->getTicketPrice();

				theTrain->transferLoad(realTime, minOn, maxOn, 0, 0, price);
			}


			if (*timeAllowance > 0) theTrain->wait(timeAllowance); //If we have any time left over, put it into waiting
			return;
		}

	}

	void HAULtrainProc(train* theTrain, int* timeAllowance, simulationParameters param, int weather)
	{
		if (theTrain->getState() != HAUL)
		{
			cout << "main::trainProcessing sent a train into HAUL proc that is not HAUL" << endl;
			err.highError = true;
			return;
		}
		if (theTrain->hasLoad() == false)
		{
			cout << "main::HAULtrainProc encountered train " << theTrain->getName() << " at " << theTrain->getLocation()->getName() << " that has no load." << endl;
			err.highError = true;
			return;
		}
		if (theTrain->getCrewTime() >= param.preemptiveCrewTime && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB))
		{
			//If crew time is getting high, stop to swap
			theTrain->setState(WAIT);
			theTrain->setWait(CREW_PROCESS_TIME, weather);
			theTrain->setExitState(HAUL);
			theTrain->setSwapFlag();
			hub* home = static_cast<hub*>(theTrain->getHome());
			home->recordDispatch();
			return;
		}
		if (theTrain->getFuel() < 10.0f && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB))
		{
			//If train is low on fuel, set state to refuel, and also i dont know how to handle this on a track.
			theTrain->setState(FUEL); 
			theTrain->setExitState(HAUL);
			return;
		}

		if (theTrain->hasPath() == false) //If train needs a path, give it one
		{
			assignRoute(theTrain, theTrain->getLoadCarried()->getCurrentStop());
		}

		//If we got here, we should be en route
		theTrain->getLoadCarried()->transit(*timeAllowance);
		theTrain->move(timeAllowance);

		if (theTrain->getLoadCarried()->getCurrentStop() == theTrain->getLocation()) //If we found the (current?) destination
		{
			bool loadComplete = theTrain->getLoadCarried()->cycleStops();
			theTrain->getLocation()->statsTrainStop();
			station* st = static_cast<station*>(theTrain->getLocation());

			st->recordDropoff();

			if (loadComplete)
			{
				theTrain->setExitState(HOME);

				completeLoads.push_back(theTrain->getLoadCarried()); //Do not let the load be lost

				theTrain->dropoffLoad(realTime);

				if (*timeAllowance > 0) theTrain->wait(timeAllowance); //If we have any time left over, put it into waiting
			}
			else
			{
				int minOn = 0, maxOn = 0, minOff = 0, maxOff = 0;
				st->getBoardingInfoRef(&minOn, &maxOn, &minOff, &maxOff);
				float price = st->getTicketPrice();


				theTrain->transferLoad(realTime, minOn, maxOn, minOff, maxOff, price);
				st->recordPickup();

				theTrain->deleteRoute();
				assignRoute(theTrain, theTrain->getLoadCarried()->getCurrentStop());
				theTrain->setExitState(HAUL);
			}
			theTrain->setState(WAIT);
			theTrain->setWait(LOAD_PROCESS_TIME, weather); //hour long processing time

			return;
		}
	}

	void WAITtrainProc(train* theTrain, int* timeAllowance)
	{
		if (theTrain->getState() != WAIT)
		{
			cout << "main::trainProcessing sent a train into WAIT proc that is not WAIT" << endl;
			err.highError = true;
			return;
		}

		theTrain->wait(timeAllowance); //State should exit within this method.
	}

	void FUELtrainProc(train* theTrain, int* timeAllowance, simulationParameters param)
	{
		if (theTrain->getState() != FUEL)
		{
			cout << "main::trainProcessing sent a train into FUEL proc that is not FUEL" << endl;
			err.highError = true;
			return;
		}

		if (theTrain->getCrewTime() >= param.preemptiveCrewTime && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB))
		{
			//If crew time is getting high, stop to swap
			theTrain->setState(WAIT);
			theTrain->setWait(CREW_PROCESS_TIME);
			theTrain->setExitState(FUEL);
			theTrain->setSwapFlag();
			hub* home = static_cast<hub*>(theTrain->getHome());
			home->recordDispatch();
			return;
		}

		if (theTrain->getDestination()->getNodeType() != HUB) //If the train's path isn't to a hub, give it a new path
		{
			theTrain->deleteRoute();

			//Find the closest hub that has enough fuel for us
			node* bestHub = NULL;
			station* location = static_cast<station*>(theTrain->getLocation());

			int numHubs = 0;
			for (int i = 0; i < nodes.size(); i++)
			{
				if (nodes[i]->getNodeType() == HUB) numHubs++;
			}

			float fuelDesired = 100.0; //Set a baseline for desired fuel

			while (fuelDesired > 0.00)
			{
				for (int i = 0; i < numHubs; i++)
				{
					hub* inspectHub = static_cast<hub*>(location->getNthClosestHub(i));
					if (inspectHub->getFuelRemaining() >= fuelDesired) 
					{
						bestHub = static_cast<node*>(inspectHub);
						break;
					}
				}
				if (bestHub != NULL) break;
				fuelDesired = fuelDesired - 10.0;
			}
			if (bestHub != NULL)
			{
				assignRoute(theTrain, bestHub);
			}
			else
			{
				cout << "WARNING: ALL HUBS ARE AT OR BELOW 10% FUEL." << endl;
			}
		}
	
		//If we got here, we're moving
		theTrain->move(timeAllowance);

		//If at a hub, refuel and possibly recrew
		if (theTrain->getLocation()->getNodeType() == HUB)
		{
			hub* locHub = static_cast<hub*>(theTrain->getLocation());
			float fuelNeeded = 100.0 - theTrain->getFuel();
			locHub->giveFuel(fuelNeeded, theTrain);

			theTrain->setState(WAIT);
			theTrain->setWait(REFUEL_TIME);

			if (theTrain->getCrewTime() >= param.preemptiveCrewTime)
			{
				theTrain->newCrew();
			}

			theTrain->wait(timeAllowance);
		}
	}

	void HOMEtrainProc(train* theTrain, int* timeAllowance, simulationParameters param)
	{
		if (theTrain->getState() != HOME)
		{
			cout << "main::trainProcessing sent a train into HOME proc that is not HOME" << endl;
			err.highError = true;
			return;
		}

		if (theTrain->getLoadCarried() != NULL)
		{
			cout << "main::trainProcessing sent a train into HOME proc that is carrying a load" << endl;
			err.highError = true;
			return;
		}

		if (theTrain->getLoadSought() != NULL)
		{
			cout << "main::trainProcessing sent a train into HOME proc that is assigned a load" << endl;
			err.highError = true;
			return;
		}

		if (theTrain->getCrewTime() >= param.preemptiveCrewTime && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB))
		{
			//If crew time is getting high, stop to swap
			theTrain->setState(WAIT);
			theTrain->setWait(CREW_PROCESS_TIME);
			theTrain->setExitState(HOME);
			theTrain->setSwapFlag();
			hub* home = static_cast<hub*>(theTrain->getHome());
			home->recordDispatch();
			return;
		}

		if (theTrain->getFuel() < 10.0)
		{
			theTrain->setState(FUEL);
			theTrain->setExitState(HOME);
		}

		if (theTrain->getDestination() != theTrain->getHome())
		{
			theTrain->deleteRoute();
			assignRoute(theTrain, theTrain->getHome());
		}

		//If we got here then we have to move
		theTrain->move(timeAllowance);

		//If we got home, go idle
		if (theTrain->getLocation() == theTrain->getHome())
		{
			theTrain->deleteRoute();
			theTrain->setState(IDLE);
			return;
		}
	}

	void MAINTINENCEtrainProc(train* theTrain, int* timeAllowance)
	{
		if (theTrain->getState() != MAIN)
		{
			cout << "main::trainProcessing sent a train into MAINT proc that is not MAINT" << endl;
			err.highError = true;
			return;
		}

		if (theTrain->getLoadCarried() != NULL)
		{
			cout << "main::trainProcessing sent a train into MAINT proc that is carrying a load" << endl;
			err.highError = true;
			return;
		}

		if (theTrain->getLoadSought() != NULL)
		{
			cout << "main::trainProcessing sent a train into MAINT proc that is assigned a load" << endl;
			err.highError = true;
			return;
		}

		theTrain->addDowntime(*timeAllowance);
		//Do nothing. This state is entered and exited by start/end of day processing
	}

	bool collisionDetection()
	{
		bool collision = false;
		for (int i = 0; i < tracks.size(); i++)
		{
			collision = tracks[i]->collisionDetect();
			if (collision) return true;
		}
		return false;
	}

	void assignLoads(simulationParameters param, milTime time, int numHubs, int dispatchTime)
	{
		bool passengerTrainsAvailable = true;
		bool freightTrainsAvailable = true;

		vector<load*> storage; //For when jobs of one type have no trains available, store unfillable jobs and search deeper into job list

		while ((loads.empty() == false) && ((passengerTrainsAvailable) || (freightTrainsAvailable)))
		{
			int soonest = loads.size() - 1;
			milTime loadTime = loads[soonest]->getBegin();

			if (((loads[soonest]->getType() == FREIGHT) && (freightTrainsAvailable == false)) || //or
				((loads[soonest]->getType() == PASSENGER) && (passengerTrainsAvailable == false)))
			{
				storage.push_back(loads[soonest]); //Store loads that can't be assigned 
				loads.pop_back();
				continue;
			}

			if ( ((minutesUntil(loadTime, realTime) >= -(dispatchTime)) && (loads[soonest]->getDay() == simDay)) ||
				((1440 + minutesUntil(loadTime, realTime) <= dispatchTime) && (loads[soonest]->getDay() == simDay + 1)) )//If a load is ready, assign it
			{
				
				train* candidateTrain = NULL;

				station* loadSpawn = static_cast<station*>(loads[soonest]->getSpawn());
				for (int i = 0; i < numHubs; i++) //For every hub on the map, starting with the nearest to the pickup point
				{
					node* candidateHub = loadSpawn->getNthClosestHub(i);

					for (int t = 0; t < candidateHub->getNumTrainsHere(); t++) //For every train at the current hub
					{
						train* current = candidateHub->getTrain(t);

						if (current->getType() != loads[soonest]->getType()) continue; //Skip trains of incorrect type
						if (current->getState() == IDLE) //If the hub has a free train, assign the load
						{
							candidateTrain = current; //FOUND TRAIN
							break;
						}
					}
					if (candidateTrain != NULL) break; //if FOUND TRAIN, break
				}

				if (candidateTrain == NULL) //If we still don't have a train
				{
					for (int i = 0; i < trains.size(); i++) //Check all trains for a HOME train
					{
						if ((trains[i]->getState() == HOME) && 
							(trains[i]->getType() == loads[soonest]->getType()) && 
							(trains[i]->getLocation()->getNodeType() == STATION))
						{
							candidateTrain = trains[i];
						}
					}
				}
			
				if ((candidateTrain == NULL) && (loads[soonest]->getType() == PASSENGER)) passengerTrainsAvailable = false; //If there are no trains available, kill the loop
				else if ((candidateTrain == NULL) && (loads[soonest]->getType() == FREIGHT)) freightTrainsAvailable = false; //If there are no trains available, kill the loop
				else
				{
					candidateTrain->seekLoad(loads[soonest]);
					candidateTrain->setState(SEEK);
					assignRoute(candidateTrain, loads[soonest]->getSpawn());
					loads.pop_back();
				}
			}
			else break; //If the earliest load isn't ready yet, no other loads are ready so quit
		}

		while (storage.empty() == false)
		{
			int top = storage.size() - 1;
			loads.push_back(storage[top]);
			storage.pop_back();
		}
	}

	//Called once per tick to handle trains
	void trainProcessing(train* theTrain, int* allowance, simulationParameters param, int weatherDelay)
	{
		trainState state = theTrain->getState();
		switch (state)
		{
		case IDLE:
			IDLEtrainProc(theTrain, allowance);
			break;

		case SEEK:
			SEEKtrainProc(theTrain, allowance, param, weatherDelay);
			break;

		case HAUL:
			HAULtrainProc(theTrain, allowance, param, weatherDelay);
			break;

		case WAIT:
			WAITtrainProc(theTrain, allowance);
			break;

		case FUEL:
			FUELtrainProc(theTrain, allowance, param);
			break;

		case HOME:
			HOMEtrainProc(theTrain, allowance, param);
			break;

		case MAIN:
			MAINTINENCEtrainProc(theTrain, allowance);
			break;
		}
	}

	void processAllTrains(simulationParameters param, weatherSystem weather, int dayEndMinutes)
	{
		int weatherDelay = 0;

		if (param.weatherToggle)
		{
			weatherDelay = weather.isWeatherGood(param.maxWeatherDelay);
		}

		if (minutesUntil(realTime, { 23,59 }) <= dayEndMinutes)
		{
			for (int i = 0; i < trains.size(); i++)
			{
				if (trains[i]->getState() != HOME && trains[i]->getState() != IDLE && trains[i]->getState() != MAIN && trains[i]->getState() != WAIT)
				{
					trains[i]->setExitState(trains[i]->getState());
					trains[i]->setState(HOME);
				}
			}
			int x = 0; //This line is here for me to breakpoint to. ignore it
		}

		//Passenger trains first
		for (int i = 0; i < trains.size(); i++)
		{
			if (trains[i]->getType() == PASSENGER)
			{
				int allowance = param.tickMinutes;
				if ((trains[i]->getState() != IDLE) && (trains[i]->getState() != MAIN)) trains[i]->addCrewTime(param.tickMinutes);
				trainProcessing(trains[i], &allowance, param, weatherDelay);
				trains[i]->lockCrewTime(); //If crew time is locked, process that
			}
		}

		for (int i = 0; i < trains.size(); i++)
		{
			if (trains[i]->getType() == FREIGHT)
			{
				int allowance = param.tickMinutes;
				if ((trains[i]->getState() != IDLE) && (trains[i]->getState() != MAIN)) trains[i]->addCrewTime(param.tickMinutes);
				trainProcessing(trains[i], &allowance, param, weatherDelay);
				trains[i]->lockCrewTime(); //If crew time is locked, process that
			}
		}
	}

	void perTickProcessing()
	{
		//Look at track opening/closing times and mark them as open or closed accordingly
		for (int i = 0; i < tracks.size(); i++)
		{
			if (tracks[i]->isTimed() && (tracks[i]->isDown() == false))  //Let maintenance take precedence
			{
				if (tracks[i]->getOpenTime() < realTime) tracks[i]->setClosed();
				else tracks[i]->setOpen();
				if (tracks[i]->getCloseTime() >= realTime) tracks[i]->setClosed();
			}
		}
	}

	void endOfDayProcessing()
	{
		for (int i = 0; i < tracks.size(); i++)
		{
			tracks[i]->setOpen(); //Set all tracks to open
			tracks[i]->setUp(); //Set all tracks to open
		}

		for (int i = 0; i < trains.size(); i++)
		{
			if (trains[i]->getState() == MAIN) trains[i]->setState(IDLE);
			float refuel = 100.0 - trains[i]->getFuel();
			trains[i]->refuel(refuel);
			trains[i]->newCrew();
		}
	}

	void startOfDayProcessing(bool statsRefresh)
	{
		//Set track maintenance
		for (int i = 0; i < trackMaintenance.size(); i++)
		{
			if (simDay == trackMaintenance[i].day)
			{
				trackMaintenance[i].affected->setClosed();
			}
		}

		for (int i = 0; i < trains.size(); i++)
		{
			if (trains[i]->getLoadCarried() != NULL || trains[i]->getLoadSought() != NULL)
			{
				//if we held a load overnight, get ready to deliver it in the morning
				trains[i]->setState(trains[i]->getExitState());
			}
		}

		//set train maintenance
		for (int i = 0; i < trainMaintenance.size(); i++)
		{
			if (simDay == trainMaintenance[i].day)
			{
				trainMaintenance[i].affected->setState(MAIN);
			}
		}

		if (statsRefresh == true)
		{
			int x;
			//Create new stats pages for every object that self-tracks stats
			for (int i = 0; i < nodes.size(); i++)
			{
				nodes[i]->newDayNode();

				if (nodes[i]->getNodeType() == HUB)
				{
					static_cast<hub*>(nodes[i])->newDayHub();
				}

				if (nodes[i]->getNodeType() == STATION)
				{
					static_cast<station*>(nodes[i])->newDayStation();
				}
			}

			for (int i = 0; i < tracks.size(); i++)
			{
				tracks[i]->newDayNode();
			}

			for (int i = 0; i < trains.size(); i++)
			{
				trains[i]->newDayTrain();
			}
		}
	}


//PRINTING METHODS

	void printAllStationsHubs()
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.at(i)->getNodeType() == STATION)
			{
				station* stInfo = (station*)nodes[i]; //use a station pointer to access the station class's printInfo
				stInfo->printInfo(true);
				cout << endl << endl;
			}

			if (nodes.at(i)->getNodeType() == HUB)
			{
				hub* hubInfo = (hub*)nodes[i]; //use a hub pointer to access the hub class's printInfo
				hubInfo->printInfo();
				cout << endl << endl;
			}
		}
	}

	void printAllTracks()
	{
		for (int i = 0; i < tracks.size(); i++)
		{
			tracks.at(i)->printInfo();
			cout << endl << endl;
		}
	}

	void printAllTrains()
	{
		for (int i = 0; i < trains.size(); i++)
		{
			trains.at(i)->printInfo();
			cout << endl << endl;
		}

	}

	void printAllLoads()
	{
		for (int i = 0; i < loads.size(); i++)
		{
			loads[i]->printInfo();
		}
	}

	void printAllCompleteLoads()
	{
		for (int i = 0; i < completeLoads.size(); i++)
		{
			completeLoads[i]->printInfo();
		}
	}

int main()
{
	int numberOfHubs = 0;
	simulationParameters parameters;
	parameters.hubFuel = 12000.0;
	parameters.trainSpeed = 175;
	parameters.tickMinutes = 1;
	parameters.preemptiveCrewTime = 480;  // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	parameters.trainFuelPerWeight = 0.05;
	parameters.startDay = MON;
	parameters.duration = 20;
	parameters.passengerCap = 200;
	parameters.preemptiveLoadDispatchTime = 60;
	parameters.weatherToggle = true;
	parameters.weatherSeverity = 50;
	parameters.weatherType = 3;
	parameters.maxWeatherDelay = 90;
	parameters.cargoPrice = 5;
	parameters.fuelCost = 1.5;
	int dayEndTimer = 0;

	weatherSystem weather(parameters.weatherType, parameters.weatherSeverity);



	vector<nodeDetails> initNodes;
	initNodes.push_back({  true,      "HUB1", "F", "0", "0", "0", "0", "0", "0"});
	initNodes.push_back({ false,  "STATION1", "F", "0", "0", "0", "0", "0", "0" });
	initNodes.push_back({ false,  "STATION2", "F", "0", "0", "0", "0", "0", "0" });
	initNodes.push_back({ false,  "STATION3", "P", "0", "30", "110", "40", "70", "10.00" });
	initNodes.push_back({ false,  "STATION4", "F", "0", "0", "0", "0", "0", "0" });
	initNodes.push_back({ false,  "STATION5", "P", "0", "10", "70", "50", "120", "20.00" });
	initNodes.push_back({ false,  "STATION6", "F", "0", "0", "0", "0", "0", "0" });
	initNodes.push_back({ false,  "STATION7", "F", "0", "0", "0", "0", "0", "0" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initNodes.push_back({ false,  "STATION8", "F", "0", "0", "0", "0", "0", "0" });
	initNodes.push_back({ false,  "STATION9", "P", "0", "50", "150", "50", "100", "5.00" });
	initNodes.push_back({ false, "STATION10", "P", "0", "40", "90", "70", "160", "15.00" });
	initNodes.push_back({ false, "STATION11", "P", "0", "40", "100", "70", "100", "12.00" });
	initNodes.push_back({ false, "STATION12", "F", "0", "0", "0", "0", "0", "0" });
	initNodes.push_back({  true,      "HUB2", "F", "0", "0", "0", "0", "0", "0" });

	vector<edgeDetails> initTrack;
	initTrack.push_back({  "STATION1",  "STATION2",  "7", "00 00", "00 00" });
	initTrack.push_back({  "STATION2",  "STATION3", "15", "00 00", "00 00" });
	initTrack.push_back({  "STATION3",  "STATION4", "21", "00 00", "00 00" });
	initTrack.push_back({  "STATION4",  "STATION5", "12", "00 00", "00 00" });
	initTrack.push_back({  "STATION5",  "STATION6", "13", "00 00", "00 00" });
	initTrack.push_back({  "STATION5",  "STATION7",  "6", "00 00", "00 00" });
	initTrack.push_back({  "STATION7",  "STATION8",  "7", "00 00", "00 00" });
	initTrack.push_back({  "STATION8",  "STATION5",  "2", "00 00", "00 00" });
	initTrack.push_back({  "STATION8",      "HUB1", "12", "00 00", "00 00" });
	initTrack.push_back({  "STATION8", "STATION10", "15", "00 00", "00 00" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initTrack.push_back({ "STATION10",  "STATION9",  "8", "00 00", "00 00" });
	initTrack.push_back({ "STATION10", "STATION11",  "7", "00 00", "00 00" });
	initTrack.push_back({ "STATION11", "STATION12", "18", "00 00", "00 00" });
	initTrack.push_back({  "STATION1", "STATION11",  "9", "00 00", "00 00" });
	initTrack.push_back({      "HUB2",  "STATION1", "12", "00 00", "00 00" });
	initTrack.push_back({  "STATION3",  "STATION4",  "8", "00 00", "00 00" });

	vector<trainDetails> initTrain;
	initTrain.push_back({ "LOCOMOTIVE1", "HUB1", "P" });
	initTrain.push_back({ "LOCOMOTIVE2", "HUB1", "F" });
	initTrain.push_back({ "LOCOMOTIVE3", "HUB2", "F" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initTrain.push_back({ "LOCOMOTIVE4", "HUB2", "F" });

	vector<freightDetails> initFreights;
	initFreights.push_back({  "STATION7", "STATION12", "F", "04 00", "100" });
	initFreights.push_back({  "STATION7",  "STATION4", "F", "04 01",  "50" });
	initFreights.push_back({  "STATION1",  "STATION2", "F", "04 50",  "50" });
	initFreights.push_back({  "STATION6",  "STATION1", "F", "04 55",  "50" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initFreights.push_back({  "STATION4",  "STATION7", "F", "05 10", "100" });
	initFreights.push_back({  "STATION4",  "STATION1", "F", "15 10",  "50" });
	initFreights.push_back({  "STATION2",  "STATION4", "F", "15 10",  "50" });

	vector<routeDetails> initPRoutesOne;
	initPRoutesOne.push_back({  "STATION5", "02 00" });
	initPRoutesOne.push_back({  "STATION3", "03 00" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initPRoutesOne.push_back({ "STATION11", "04 00" });
	initPRoutesOne.push_back({  "STATION3", "05 00" });

	vector<routeDetails> initPRoutesTwo;
	initPRoutesTwo.push_back({ "STATION10", "02 00" });
	initPRoutesTwo.push_back({ "STATION11", "02 50" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initPRoutesTwo.push_back({ "STATION10", "03 40" });
	initPRoutesTwo.push_back({ "STATION11", "04 30" });
	
	vector<passengerDetails> initPassengers;
	initPassengers.push_back({ "W", initPRoutesOne }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initPassengers.push_back({ "F", initPRoutesTwo });

	vector<edgeMaintenanceDetails> initEMaint;
	initEMaint.push_back({ "1", "STATION5", "STATION4", "3" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS
	initEMaint.push_back({ "1", "STATION3", "STATION4", "3" });

	vector<trainMaintenanceDetails> initTMaint;
	initTMaint.push_back({ "1", "LOCOMOTIVE2", "3" }); // DELETE THESE AND REPLACE THEM WITH BRIDGE INPUTS


	///////////////////////////////////////////////////////////////////////////
	cout << rule << "Creating Hub and Station nodes..." << endl;
	buildNodes(initNodes, parameters);

	cout << rule << "Creating Track nodes..." << endl;
	buildTracks(initTrack);

	cout << rule << "Linking nodes..." << endl;
	connectNodesToTracks();

	cout << rule << "Preparing Trains..." << endl;
	buildTrains(initTrain, parameters);

	cout << rule << "Preparing repeatable routes..." << endl;
	buildLoads(initFreights, initPassengers, parameters.startDay, parameters);

	cout << rule << "Sorting routes..." << endl;
	sortLoads();

	cout << rule << "Preparing pathfinding graph..." << endl;
	buildMasterRecord();

	cout << rule << "Pre-calculating hub-station minimum distances..." << endl;
	numberOfHubs = calculateHubDistances();

	cout << rule << "Pre-calculating hub-station minimum distances..." << endl;
	dayEndTimer = calculateWorstCaseHomeTime(numberOfHubs, parameters);

	cout << rule << "Setting maintenance times..." << endl;
	buildMaintenanceRecords(initEMaint, initTMaint);
	cout << rule << endl;

	//Print railway info
	cout << rule << endl;
	printAllStationsHubs();
	cout << rule << endl;
	printAllTracks();
	cout << rule << endl;
	printAllTrains();
	cout << rule << endl;
	printAllLoads();
 	cout << rule << endl;

	//THE SIMULATION LOOP////////////////////////////////////////////////////
	bool COLLISION = false;
	int previousDay = 1;

	endOfDayProcessing();
	startOfDayProcessing(false);
	while (simDay <= parameters.duration)
	{
		if (simDay != previousDay)
		{
			startOfDayProcessing(true);
		}

		
		perTickProcessing();
		assignLoads(parameters, realTime, numberOfHubs, parameters.preemptiveLoadDispatchTime);
		processAllTrains(parameters, weather, dayEndTimer);


		COLLISION = collisionDetection();
		if (COLLISION)
		{
			cout << "COLLISION ERROR" << endl;
			break;
		}

		previousDay = simDay;
		runClock(parameters.tickMinutes);
		
		if (minutesUntil(realTime, { 23,59 }) < dayEndTimer + 30)
		{
			if (minutesUntil(realTime, { 23,59 }) == dayEndTimer)
			{
				cout << "DAY END CALLED. ALL TRAINS RETURN TO HUB. " << endl;
			}
			//printDay();
			//printTime(realTime);
			//printAllTrains();
			//printAllLoads();			//Uncomment these for a more detailed and wildly fucking slow simulation. Remember, system() is the devil
			//system("pause");
			//system("cls");
		}

		if (simDay != previousDay)
		{
			endOfDayProcessing();
			//printAllTrains();
		}
	}
	/////////////////////////////////////////////////////////////////////////
	//STATS PROCESSING
	cout << "SIMULATION COMPLETE" << endl;
	cout << rule << endl;
	cout << endl << "Processing statistics gathered..." << endl;

	for (int i = 0; i < completeLoads.size(); i++)
	{
		milTime realStart = completeLoads[i]->getPickupTime();
		milTime plannedStart = completeLoads[i]->getBegin();

		int lateness = minutesUntil(plannedStart, realStart);
		if (lateness <= 0) lateness = 0;

		completeLoads[i]->setLateness(lateness);		
	}

	//STATS PRINTING
	for (int i = 0; i < parameters.duration; i++)
	{
		maxxFile << "Day," << parameters.duration - i << endl;
		neroFile << "DAY" << parameters.duration - i << endl;


		string TRAIN_P = " "; //for output file, since this loop was originally designed around the stats file and the two must now coincide
		string TRAIN_F = " ";
		for (int t = 0; t < trains.size(); t++)
		{
			maxxFile << trains[t]->getName() << ",";
			if (trains[t]->getType() == FREIGHT) maxxFile << "Freight,";
			else maxxFile << "Passenger,";

			train::trainStats stat = trains[t]->getTrainStats();

			maxxFile << stat.distance << "," << stat.collisionsAvoided << "," << stat.totalCarried << endl;

			float profit = 0.0;
			profit = stat.loadRevenue - (stat.fuelUsed * parameters.fuelCost);

			if (trains[t]->getType() == PASSENGER)
			{
				TRAIN_P += "\nTRAIN_P";
				TRAIN_P += " " + trains[t]->getName() + " " + to_string(stat.distance) + " " + to_string(stat.collisionsAvoided) + " " + to_string(profit);
				TRAIN_P += " " + to_string(stat.timesFuelled) + " " + to_string(stat.fuelUsed) + " " + to_string(stat.totalPassengers) + " " + to_string(stat.acceptedPassengers);
			}
			else
			{
				TRAIN_F += "\nTRAIN_F";
				TRAIN_F += " " + trains[t]->getName() + " " + to_string(stat.distance) + " " + to_string(stat.collisionsAvoided) + " " + to_string(profit);
				TRAIN_F += " " + to_string(stat.timesFuelled) + " " + to_string(stat.fuelUsed) + " " + to_string(stat.totalCarried) + " " + to_string(stat.maxCarried);
			}
		}


		neroFile << TRAIN_P;
		neroFile << TRAIN_F << endl;

		for (int s = 0; s < nodes.size(); s++)
		{
			if (nodes[s]->getNodeType() == STATION)
			{
				maxxFile << nodes[s]->getName() << ",";

				node::nodeStats n_stat = nodes[s]->getNodeStats();
				station::stationStats s_stat = static_cast<station*>(nodes[s])->getStationStats();

				maxxFile << n_stat.trainStops << "," << s_stat.dropoffs << "," << s_stat.pickups << "," << n_stat.trainsThru << endl;

				neroFile << "STATION";
				neroFile << " " << nodes[s]->getName() << " " << s_stat.dropoffs << " " << s_stat.pickups << " " << n_stat.trainStops << " " << n_stat.trainsThru << endl;
			}
		}

		for (int r = 0; r < tracks.size(); r++)
		{
			node::nodeStats t_stat = tracks[r]->getNodeStats();
			maxxFile << tracks[r]->getName() << "," << t_stat.trainsThru << endl;

			neroFile << "TRACK";
			neroFile << " " << tracks[r]->getName() << " " << t_stat.trainsThru << endl;
		}


		for (int h = 0; h < nodes.size(); h++)
		{
			if (nodes[h]->getNodeType() == HUB)
			{
				node::nodeStats n_stats = nodes[h]->getNodeStats();
				hub::hubStats h_stats = static_cast<hub*>(nodes[h])->getHubStats();

				maxxFile << nodes[h]->getName() << "," << n_stats.trainsThru << endl;
				neroFile << "HUB";
				neroFile << " " << nodes[h]->getName() << " " << h_stats.fuelGiven << " " << h_stats.crewGiven << endl;
			}
		}


		maxxFile << endl;
		neroFile << endl << endl;
	}

	for (int o = 0; o < loads.size(); o++)
	{
		load::loadStats l_stat = loads[o]->getLoadStats();
		neroFile << "ROUTE";
		neroFile << " " << loads[o]->getName();
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.intendedStart.hour << ":" << setw(2) << l_stat.intendedStart.min;
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.intendedEnd.hour << ":" << setw(2) << l_stat.intendedEnd.min;
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.actualStart.hour << ":" << setw(2) << l_stat.actualStart.min;
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.actualEnd.hour << ":" << setw(2) << l_stat.actualEnd.min;
		neroFile << setfill(' ') << left;
		neroFile << " " << l_stat.minutesLate << " " << l_stat.transitTime;
		neroFile << endl;
	}

	for (int o = 0; o < completeLoads.size(); o++)
	{
		load::loadStats l_stat = completeLoads[o]->getLoadStats();
		neroFile << "ROUTE";
		neroFile << " " << completeLoads[o]->getName();
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.intendedStart.hour << ":" << setw(2) << l_stat.intendedStart.min;
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.intendedEnd.hour << ":" << setw(2) << l_stat.intendedEnd.min;
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.actualStart.hour << ":" << setw(2) << l_stat.actualStart.min;
		neroFile << " " << setfill('0') << right << setw(2) << l_stat.actualEnd.hour << ":" << setw(2) << l_stat.actualEnd.min;
		neroFile << setfill(' ') << left;
		neroFile << " " << l_stat.minutesLate << " " << l_stat.transitTime;
		neroFile << endl;
	}


	maxxFile.flush();
	neroFile.flush();
	cout.flush();
	cout << endl << endl;
	system("pause");
	return 0;
}