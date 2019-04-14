#include "train.h"



train::train(string _name, int ID, loadType _type, int speed, node* homeHub)
{
	name = _name;
	uniqueID = ID;
	Ltype = _type;
	travelSpeed = speed;
	home = homeHub;

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

float train::getFuel() { return fuel; }

load* train::getLoad() { return loadCarried; }


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
		progress = 12.01;
		int bars = (int)progress;
		float fraction = (float)progress - bars;
		bars = bars / 10;

		char shade = 'x';
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
			cout << currentPath[i].thisNode->getName();

			if (i + 1 < currentPath.size()) cout << ", ";
		}
		cout << endl;
	}
}


void train::setState(trainState newState)
{
	state = newState;
}

void train::setRoute(vector<route::record> newRoute) { currentPath = newRoute; }
