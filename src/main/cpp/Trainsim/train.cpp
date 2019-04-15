#include "train.h"



train::train(string _name, int ID, loadType _type, int speed, node* homeHub, int cap)
{
	name = _name;
	uniqueID = ID;
	Ltype = _type;
	travelSpeed = speed;
	travelSpeedPerMinute = (float)speed / 60;
	home = homeHub;
	capacity = cap;

	state = IDLE;
	location = home;
	direction = NULL;
	progress = 0.0;
	fuel = 100.0;
	crewtime = 0;
	loadCarried = NULL;
	rerouting = false;
	avoidance = false;
}


train::~train()
{
}

//TESTING UTILITY DO NOT USE
void train::teleport(node* dest)
{
	if (dest != NULL) 
	{
		location->departure(this);
		location = dest;
		location->arrival(this);
		cout << "DEBUG: " << name << " teleported to " << location->getName() << endl;
	}
}

//TESTING UTILITY DO NOT USE (teleports to next destination)
void train::teleport()
{

}


void train::changeLocation()
{
	if (direction == NULL)
	{
		cout << name << " attempted to change to a NULL direction" << endl;
	}
	else
	{
		cout << "DEBUG: Old loc: " << location->getName() << "  Old dir: " << direction->getName() << endl;

		if (location->getNodeType() == HUB || location->getNodeType() == STATION)
		{
			location->departure(this);	//Exit station
			location = direction;		//enter track
			location->arrival(this);	//inform track
			direction = currentPath[currentPath.size() - 1].thisNode; //Get new direction from path
			cout << "DEBUG: New loc: " << location->getName() << "  new dir: " << direction->getName() << endl;
		}
		else
		{
			location->departure(this);	//Exit track
			location = direction;		//enter station
			location->arrival(this);	//Inform station
			if (currentPath.size() > 1) //If we aren't at the end of the line
			{
				node* nextDirection = getNextTrackHeading(); //Find next direction
				direction = nextDirection;
				cout << "DEBUG: New loc: " << location->getName() << "  new dir: " << direction->getName() << endl;
			}
			else //If we are done with the route
			{
				direction = NULL;
			}
			currentPath.pop_back();
		}
	}
}


loadType train::getType() { return Ltype; }

trainState train::getState() { return state; }

string train::getName() { return name; }

string train::getStateName()
{
	switch (state)
	{
	case IDLE:
		return "Idle";

	case SEEK:
		return "Seeking load";

	case HAUL:
		return "Hauling load";

	case WAIT:
		return "Waiting";

	case FUEL:
		return "Refueling";

	case HOME:
		return "Returning to home hub";

	case MAIN:
		return "Down for maintenance";

	default:
		cout << "Train::getStateName defaulted. This should never happen" << endl;
		return "X";
	}
}

node* train::getHeading()
{
	if (direction != NULL)
	{
		return direction;
	}
	else
	{
		cout << "train::getHeading called when train had no destination" << endl;
		return NULL;
	}
}

node* train::getLocation()
{
	if (location != NULL)
	{
		return location;
	}
	else
	{
		cout << "train::getLocation called, train has null location." << endl;
		return NULL;
	}
}

bool train::hasLoad() { return (loadCarried != NULL); }

bool train::hasPath() { return (currentPath.empty() == false); }

float train::getFuel() { return fuel; }

load* train::getLoadCarried() { return loadCarried; }

load* train::getLoadSought() { return loadSought; }


void train::printInfo()
{
	cout << PIPE_VERT;
	cout << setfill(' ') << left;

	string loadName = "None";
	if (loadCarried != NULL)
	{
		loadName = loadCarried->getName();
	}



	string typeString = "(Error)";
	if (Ltype == PASSENGER) typeString = "(Passenger)";
	if (Ltype == FREIGHT) typeString =   "  (Freight)";

	cout << name << ", ID: " << uniqueID << " " << typeString << " Home: " << home->getName() << "  State: " << getStateName();

	cout << endl << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_DLR;
	cout << "Location: " << location->getName();

	cout << endl << "   " << PIPE_UDR << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ;
	cout << "Load: " << loadName;

	if (direction != NULL)
	{
		int bars = (int)progress;
		float fraction = (float)progress - bars;
		bars = bars / 10;

		char shade = 'x';
		if (fraction == 0.00f) shade = '.';
		if (fraction > 0.00f) shade = SHADE1;
		if (fraction > 0.33f) shade = SHADE2;
		if (fraction > 0.66f) shade = SHADE3;

		cout << endl << "   " << PIPE_UDR << PIPE_HORZ;
		cout << "Heading: " << setw(10) << direction->getName() << " ";

		for (int i = 0; i < bars; i++) cout << SHADE4;
		cout << shade;
		for (int i = 9 - bars; i > 0; i--) cout << ".";
		cout << endl << endl;

	}
}

void train::printRoute()
{
	if (currentPath.empty()) cout << "train::printRoute called on train " << name << " which has no route" << endl;
	else
	{
		for (int i = 0; i < currentPath.size(); i++)
		{
			cout << currentPath[i].thisNode->getName() << "(W" << currentPath[i].weightBefore << "  TD" << currentPath[i].distance << ")";

			if (i + 1 < currentPath.size()) cout << ", ";
		}
		cout << endl;
	}
}


void train::deleteRoute()
{
	currentPath.clear();
}

void train::setState(trainState newState)
{
	state = newState;
}

void train::setExitState(trainState newExitState)
{
	exitState = newExitState;
}

void train::setRoute(vector<route::record> newRoute) 
{ 
	if (newRoute.size() <= 1)
	{
		cout << "train::setRoute encountered a route with one or less entries given to " << name << " at " << location->getName() << endl;
	}
	else if (location != newRoute[newRoute.size() - 1].thisNode)
	{
		cout << "train::setRoute passed a route to " << name << " that does not begin at its location " << location->getName() << endl;
	}
	else
	{
		currentPath = newRoute;
		currentPath.pop_back();
		direction = getNextTrackHeading();
		destination = currentPath[0].thisNode; //Set the destination
		cout << "DEBUG: " << location->getName() << " to direction " << direction->getName() << endl;

	}

}

void train::seekLoad(load* newLoad)
{
	if (loadCarried != NULL)
	{
		if (loadCarried == newLoad)
		{
			cout << name << " was assigned the load that it already has" << endl;
		}
		else cout << name << " was assigned a new load despite already carrying one" << endl;
	}
	else
	{
		loadSought = newLoad;
		destination = loadSought->getSpawn();
	}
}

void train::pickupLoad(milTime when)
{
	if (loadSought == NULL)
	{
		cout << name << " attempted to pick up a load despite not having a sought load" << endl;
	}
	else if (loadCarried != NULL)
	{
		cout << name << " attempted to pick up a load despite already carrying one" << endl;
	}
	else if (Ltype == loadSought->getType())
	{
		loadCarried = loadSought;
		loadCarried->setPickupTime(when);
		loadSought = NULL;
	}
	else
	{
		cout << name << " attempted to pick up a load of incorrect load type" << endl;
	}
}


void train::dropoffLoad(milTime when)
{
	if (loadCarried == NULL)
	{
		cout << name << " attempted to drop off a load despite not having one" << endl;
	}
	else if (Ltype == loadSought->getType())
	{
		loadCarried->setDropoffTime(when);
		loadCarried = NULL;
	}
	else
	{
		cout << name << " attempted to pick up a load of incorrect load type" << endl;
	}
}



void train::move(int* minutes)
{
	if (hasPath() == false)
	{
		cout << name << " was called to move but does not have a path right now." << endl;
		return;
	}
	//If we're on a hub, depart when safe
	if (location->getNodeType() == HUB || location->getNodeType() == STATION)
	{
		//Check for potential collisions, then embark if safe
		//Note: direction is already defined here
		bool safe = true;

		if (direction->getNodeType() == HUBTRACK);	//We're gauranteed to be safe if we are entering a hub con
		else										//Otherwise check for track activity
		{
			for (int i = 0; i < direction->getNumTrainsHere(); i++)
			{
				train* inspecting = direction->getTrain(i);
				if (inspecting->getHeading() == location) //If any train on the next track is coming towards us
				{
					cout << name << " avoided a collision with " << inspecting->getName() << " on " << direction->getName() << endl;
					safe = false;
				}
			}
		}
		if (safe)
		{
			//disembark location to direction (track). set new direction (node)
			changeLocation();
			progress = 0.00;
		}
		else
		{
			return; //keep waiting
		}

	}
	
	//If we're on a track, keep moving
	if (location->getNodeType() == TRACK || location->getNodeType() == HUBTRACK)
	{
		int currentTrackWeight = currentPath[currentPath.size() - 1].weightBefore;
		float progressPerMinute = (float)travelSpeed / currentTrackWeight;
		cout << "DEBUG: CTW: " << currentTrackWeight << "   PPM: " << progressPerMinute << endl;

		while (*minutes > 0 && progress < 99.99)
		{
			progress = progress + progressPerMinute;
			cout << "DEBUG: " << progress << "%" << endl;
			*minutes--;
		}
	}

	if (progress >= 99.99)	//If we have arrived at the next station/hub
							//Due to the nature of the progress meter, we can only enter this branch after leaving a track
	{
		changeLocation();
		progress = 0.00;
		//currentPath.pop_back();

		cout << "DEBUG: " << location->getName() << " vs " << destination->getName() << endl;
		if (location == destination) //end of line processing
		{
			if (state == SEEK || state == HAUL) state = WAIT;
			else if (state == FUEL); //Do nothing
			else if (state == HOME) state = IDLE;

			deleteRoute();
		}
		else //Prepare for next movement
		{
			node* target = currentPath[currentPath.size() - 1].thisNode;

			node* nextDir = getNextTrackHeading();


			if (nextDir == NULL)
			{
				cout << "train::Move could not find next track to assign to " << name << ". Infodump: " << endl;
				printInfo();
				cout << endl;
			}
			else
			{
				direction = nextDir;
			}
		}
	}
}

node* train::getNextTrackHeading()
{
	node* result = NULL;

	if (location->getNodeType() == TRACK || location->getNodeType() == HUBTRACK)
	{
		cout << "train::getNextTrackHeading cannot get a new track heading if train is not at a node." << endl;
	}
	else
	{
		for (int i = 0; i < location->getNumConnections(); i++) //For all of our new location's tracks
		{
			node* inspectTrack = location->getConnection(i);
			for (int j = 0; j < 2; j++) //Check all of those tracks' connections
			{
				node* inspectNode = inspectTrack->getConnection(j);
				if (inspectNode == currentPath[currentPath.size() - 1].thisNode) //When we find the one that connects to our next path entry
				{
					result = inspectTrack; //Set is as our next destination;
				}
			}
		}
	}

	return result;
}


void train::setWait(int minutes)
{
	waitTime = minutes;
}

void train::wait(int* minutes)
{
	while (*minutes > 0 && waitProgress < waitTime)
	{
		waitProgress++;
		*minutes--;
	}

	if (waitProgress >= waitTime)
	{
		state = exitState;
		waitTime = 0;
		waitProgress = 0;
	}
}