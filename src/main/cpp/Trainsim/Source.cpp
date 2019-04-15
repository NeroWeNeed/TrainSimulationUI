#include "global.h"
#include "load.h"
#include "node.h"
#include "route.h"
#include "station.h"
#include "train.h"
#include "track.h"
#include "hub.h"
#include <sstream>

using namespace std;

int currentNodeID = 0; int currentTrackID = 0; int currentTrainID = 0;
milTime realTime = { 00,00 };



//Contains pointers to all tracks
vector<track*> tracks;

//Contains node pointers to all stations and hubs. Can be static casted to 
//utilize station/hub specific methods
vector <node*> nodes;

//Contains pointers to all trains
vector<train*> trains;

//Contains pointers to all active loads
vector <load*> loads;

//Contains a permanent record of the node vector in the form of
//records able to be used in pathfinding. 
//node IDs match to index of that item
vector<route::record> masterRecords;

string rule = "============================================================================\n";
errorState err;

void resetErr()
{
	//Do not clear highError since these will end the program
	err.lowError = false;
	err.message.clear();
	err.code = 0;
}

struct simulationParameters
{
	float hubFuel;
	int tickMinutes;
	milTime time;
	int trainSpeed; //Weight units per hour
	int passengerCap; //P train capacity
};

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

void buildNodes(string input, simulationParameters param)
{
	stringstream in(input);
	string current;

	in >> current;
	while (in.eof() == false)
	{
		resetErr();

		if (current.find("HUB") != current.npos)
		{
			nodes.push_back(new hub(current, currentNodeID, param.hubFuel));
			currentNodeID++;
			in >> current;
		}

		else if (current.find("STATION") != current.npos)
		{
			string stName = current;
			string options[7];
			loadType stType;
			int currentOption = 0; //Used to recover from error where an option may take the next station's name

			in >> options[0];
			currentOption++;
			if (options[0].length() == 1)
			{
				if (options[0][0] == 'F' || options[0][0] == 'f')
					stType = FREIGHT;
				else if (options[0][0] == 'P' || options[0][0] == 'p')
					stType = PASSENGER;
				else
				{
					err.lowError = true;
					cout << "main::BuildNodes encountered " << current << " with invalid node type " << options[0] << endl;
				}
				
			}
			else
			{
				err.lowError = true;
				cout << "main::BuildNodes current station indicator is too long" << endl;
			}
			
			if (!err.lowError)
			{
				for (int i = 1; i < 6; i++)
				{
					in >> options[i];
					currentOption++;
					if (!stringIsNumber(options[i]))
					{
						err.lowError = true;
						cout << "main::BuildNodes found non-integer value for station " << stName << " parameters" << endl;
						break;
					}
				}
			}

			if (!err.lowError)
			{
				in >> options[6];
				currentOption++;
				for (int i = 0; i < options[6].length(); i++)
				{
					if (isdigit(options[6][i]) == false && options[6][i] != '.')
					{
						err.lowError = true;
						cout << "main::BuildNodes found non-numeric value for ticket price" << stName << " parameters" << endl;
						break;
					}
				}
			}

			if (!err.lowError)
			{
				nodes.push_back(new station(stName, currentNodeID, STATION, stType, stoi(options[1]), stoi(options[2]), stoi(options[3]), stoi(options[4]), stoi(options[5]), stof(options[6])));
				currentNodeID++;
				in >> current;
			}			
			else
			{
				cout << "main::BuildNodes discarded invalid entry for " << stName << endl;
				current = options[currentOption - 1]; //If we had an error, the next station name may have been read into an option.
													  //This will rescue it if that is the case
			}

		}

		else
		{
			//Cycle the current substring until it is a name
			cout << "Main::BuildNodes discarded invalid name " << current << endl;
			in >> current;
		}

	}
	resetErr();
}

void buildTracks(string input)
{
	stringstream in(input);
	string current;

	in >> current;
	while (in.eof() == false)
	{
		resetErr();
		bool firstExists = false;
		int firstIter = 0;
		bool secondExists = false;
		int secondIter = 0;
		int weight = 0;
		int start = 0;
		int close = 0;

		//This if/else block is our failsafe for missing arguments in the tracks. 
		//It ensures that if one track is missing an argument, the next correct track will not be lost
		if (current.find("HUB") == current.npos && current.find("STATION") == current.npos)
		{
			//tracks may connect hubs or stations. Any thing else as one of the connection
			//parameters will be cycled out
			cout << "main::buildTracks discarded invalid connection name " << current << endl;
			in >> current;
			continue;
		}
		else
		{
			if (current.find("HUB") == current.npos && current.find("STATION") == current.npos)
			{
				//If the first connection name is valid, check the second and creep forward 1
				cout << "main::buildTracks discarded invalid connection name " << current << endl;
				in >> current;
				continue;

			}
		}

		for (firstIter = 0; firstIter < nodes.size(); firstIter++)
		{
			if (current == nodes[firstIter]->getName())
			{
				firstExists = true;
				break;
			}
		}

		if (!firstExists)
		{
			err.lowError = true;
			cout << "main::buildTracks could not find node " << current << " in node set" << endl;
		}

		if (!err.lowError)
		{
			in >> current;
			for (secondIter = 0; secondIter < nodes.size(); secondIter++)
			{
				if (current == nodes[secondIter]->getName())
				{
					secondExists = true;
					break;
				}
			}
			if (!secondExists)
			{
				err.lowError = true;
				cout << "main::buildTracks could not find node " << current << " in node set" << endl;
			}
		}

		if (!err.lowError)
		{
			in >> current;
			if (!stringIsNumber(current))
			{
				err.lowError = true;
				cout << "main::buildTracks encountered invalid weight value: " << current << endl;
			}
		}

		if (!err.lowError) weight = stoi(current);

		if (!err.lowError)
		{
			in >> current;
			if (!stringIsNumber(current))
			{
				err.lowError = true;
				cout << "main::buildTracks encountered invalid start time: " << current << endl;
			}
		}

		if (!err.lowError) start = stoi(current);

		if (!err.lowError)
		{
			in >> current;
			if (!stringIsNumber(current))
			{
				err.lowError = true;
				cout << "main::buildTracks encountered invalid close time: " << current << endl;
			}
		}

		if (!err.lowError) close = stoi(current);


		if (!err.lowError)
		{
			nodeType trackType = TRACK;
			if (nodes[firstIter]->getNodeType() == HUB || nodes[secondIter]->getNodeType() == HUB) trackType = HUBTRACK;
			tracks.push_back(new track("Track " + to_string(currentTrackID), currentTrackID, trackType, nodes[firstIter], nodes[secondIter], weight, start, close));
			in >> current;
		}
		else
		{
			cout << "main::buildTracks discarded track ID " << currentTrackID << endl;
		}

		currentTrackID++;
	}
	resetErr();
}

void connectNodesToTracks()
{
	for (int i = 0; i < tracks.size(); i++)
	{
		err.highError = tracks.at(i)->getConnection(0)->addConnection(tracks.at(i));
		err.highError = tracks.at(i)->getConnection(1)->addConnection(tracks.at(i));
	}
}

void buildTrains(string input, simulationParameters param)
{
	stringstream in(input);
	string current;

	in >> current;

	while (in.eof() == false)
	{
		resetErr();
		string name = "XXXX";
		node* hub = NULL;
		loadType type = UNDEF;

		//Name can be anything, no error to check for
		name = current;

		in >> current;
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.at(i)->getName() == current)
			{
				if (nodes.at(i)->getNodeType() != HUB)
				{
					err.lowError = true;
					cout << "main::buildTrains starting position defined for " << name << " is not a hub" << endl;
					break;
				}
				else
				{
					hub = nodes.at(i);
					break;
				}
			}
		}

		if (hub == NULL)
		{
			err.lowError = true;
			cout << "main::buildTrains could not find starting location: " << current << endl;
		}

		if (!err.lowError)
		{
			in >> current;
			if (current.length() == 1)
			{
				if (current[0] == 'F' || current[0] == 'f')
					type = FREIGHT;
				else if (current[0] == 'P' || current[0] == 'p')
					type = PASSENGER;
				else
				{
					err.lowError = true;
					cout << "main::buildTrains encountered invalid type definition: " << current << endl;
				}
			}
			else
			{
				err.lowError = true;
				cout << "main::buildTrains type definition too long: " << current << endl;
			}
		}

		if (!err.lowError)
		{
			trains.push_back(new train(name, currentTrainID, type, param.trainSpeed, hub, param.passengerCap));
			int lastTrain = trains.size() - 1;

			//Need to get the last train into a standalone pointer
			//because otherwise vector breaks everything?
			train* latestTrain = trains.at(trains.size() - 1);
			hub->arrival(latestTrain); //Tell the hub that a new train exists in it

			currentTrainID++;
		}
		in >> current;

		if (err.lowError)
		{
			err.highError = true;
			cout << "Errors occurred during train parse, aborting simulation." << endl;
			return;
		}
	}
}

void buildMasterRecord()
{
	for (int i = 0; i < nodes.size(); i++)
	{
		route::record newRec{ nodes[i], NULL, INT_MAX };
		masterRecords.push_back(newRec);
	}
}

void assignRoute(train* loco, node* dest)
{
	node* src = loco->getLocation();
	route pathfind;
	vector<route::record> path = pathfind.AstarPath(src, dest, masterRecords);

	loco->setRoute(path);
}


//TRAIN PROCESSING METHODS
//variable timeAllowance represents how many minutes (maximum = ticklength)
//the train has to work with. An example of where this is useful is when a train
//enters a station with time to spare on the current tick, and may spend the remaining
//time on running down the station processing timer
//Allowance is spent 1 minute at a time.

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

void SEEKtrainProc(train* theTrain, int* timeAllowance)
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
	if (theTrain->getFuel() < 10.0f && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB) )
	{
		theTrain->setState(FUEL); //If train is low on fuel, set state to refuel, and also i dont know how to handle this on a track.
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
		theTrain->setState(WAIT);
		theTrain->setWait(60); //hour long processing time
		theTrain->setExitState(SEEK);
		theTrain->pickupLoad(realTime);

		if (*timeAllowance > 0) theTrain->wait(timeAllowance); //If we have any time left over, put it into waiting
		return;
	}

}

void HAULtrainProc(train* theTrain, int* timeAllowance)
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
	if (theTrain->getFuel() < 10.0f && (theTrain->getLocation()->getNodeType() == STATION || theTrain->getLocation()->getNodeType() == HUB))
	{
		theTrain->setState(FUEL); //If train is low on fuel, set state to refuel, and also i dont know how to handle this on a track.
		return;
	}

	if (theTrain->hasPath() == false) //If train needs a path, give it one
	{
		assignRoute(theTrain, theTrain->getLoadCarried()->getDest());
	}

	//If we got here, we should be en route
	theTrain->move(timeAllowance);

	if (theTrain->getLoadCarried()->getDest() == theTrain->getLocation()) //If we found the destination
	{
		theTrain->setState(WAIT);
		theTrain->setWait(60); //hour long processing time
		theTrain->setExitState(HOME);
		theTrain->dropoffLoad(realTime);

		if (*timeAllowance > 0) theTrain->wait(timeAllowance); //If we have any time left over, put it into waiting
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

void FUELtrainProc(train* theTrain, int* timeAllowance)
{
	if (theTrain->getState() != FUEL)
	{
		cout << "main::trainProcessing sent a train into FUEL proc that is not FUEL" << endl;
		err.highError = true;
		return;
	}

}

//Called once per tick to handle trains
void trainProcessing(train* theTrain)
{
	trainState state = theTrain->getState();


}








int main()
{
	simulationParameters parameters;
	parameters.hubFuel = 1.1;
	parameters.trainSpeed = 175;

	string Snodes = "HUB1\nSTATION1 F 0 0 0 0 0 0\nSTATION2 F 0 0 0 0 0 0\nSTATION3 P 0 0 0 0 0 0\nSTATION4 F 0 0 0 0 0 0\nSTATION5 P 0 0 0 0 0 0\nSTATION6 F 0 0 0 0 0 0\nSTATION7 F 0 0 0 0 0 0\nSTATION8 F 0 0 0 0 0 0\nSTATION9 P 0 0 0 0 0 0\nSTATION10 P 0 0 0 0 0 0\nSTATION11 P 0 0 0 0 0 0\nSTATION12 F 0 0 0 0 0 0\nHUB2\n";
	string Stracks = "STATION1 STATION2 7 0000 0000\nSTATION2 STATION3 15 0000 0000\nSTATION3 STATION4 21 0000 0000\nSTATION4 STATION5 12 0000 0000\nSTATION5 STATION6 13 0000 0000\nSTATION5 STATION7 6 0000 0000\nSTATION7 STATION8 7 0000 0000\nSTATION8 STATION5 2 0000 0000\nSTATION8 HUB1 12 0000 0000\nSTATION8 STATION10 15 0000 0000\nSTATION10 STATION9 8 0000 0000\nSTATION10 STATION11 7 0000 0000\nSTATION11 STATION12 18 0000 0000\nSTATION1 STATION11 9 0000 0000\nHUB2 STATION1 12 0000 0000";
	string Strains = "LOCOMOTIVE1 HUB1 P\nLOCOMOTIVE2 HUB2 F\nLOCOMOTIVE3 HUB1 F\n";

	cout << rule << "Creating Hub and Station nodes..." << endl;
	buildNodes(Snodes, parameters);
	cout << rule << "Creating Track nodes..." << endl;
	buildTracks(Stracks);
	cout << rule << "Linking nodes..." << endl;
	connectNodesToTracks();
	cout << rule << "Preparing Trains..." << endl;
	buildTrains(Strains, parameters);
	cout << rule << "Preparing pathfinding graph..." << endl;
	buildMasterRecord();
	cout << rule << endl;


	cout << rule << endl;
	//Print all hub and station info
	for (int i = 0; i < nodes.size(); i++)
	{
		if (nodes.at(i)->getNodeType() == STATION)
		{
			station* stInfo = (station*) nodes[i]; //use a station pointer to access the station class's printInfo
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
	cout << rule << endl;

	//Print all track info
	for (int i = 0; i < tracks.size(); i++)
	{
		tracks.at(i)->printInfo();
		cout << endl << endl;
	}
	cout << rule << endl;

	//Print all train info
	for (int i = 0; i < trains.size(); i++)
	{
		trains.at(i)->printInfo();
		cout << endl << endl;
	}
	cout << rule << endl;



	assignRoute(trains[0], nodes[1]);
	trains[0]->printRoute();
	cout << endl;
	trains[0]->setState(SEEK);


	assignRoute(trains[1], nodes[8]);
	trains[1]->printRoute();
	cout << endl;
	trains[1]->setState(SEEK);


	while (trains[0]->hasPath())
	{
		int freeMinute = 1;
		cout << rule << "FREIGHT TRAIN" << endl;
		trains[0]->move(&freeMinute);
		trains[0]->printInfo();
		cout << endl;

		cout << rule << "PASS TRAIN" << endl;
		freeMinute = 1;
		trains[1]->move(&freeMinute);
		trains[1]->printInfo();
		cout << endl;

	}


	while (1)
	{
		break;
		assignRoute(trains[0], nodes[1]);
		trains[0]->printRoute();
		trains[0]->deleteRoute();
	}


	char continueSim = '0';
	while (continueSim == '0')
	{
		break;
	}


	cout << endl << endl;
	system("pause");
}