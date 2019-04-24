#include "hub.h"



hub::hub(string _name, int ID, float _fuel) : node(_name, ID, HUB)
{
	fuel = _fuel;

	hubStats first;
	hub_stats.push_back(first);
}


hub::~hub()
{
}

void hub::setOwnership(train* newTrain) { trainsOwned.push_back(newTrain); }


float hub::getFuelRemaining() {	return fuel; }

void hub::printInfo()
{
	cout << PIPE_VERT;
	cout << setfill(' ') << left;

	cout << setw(11) << this->getName() << " ID: " << setw(3) << this->getID();

	cout << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_DLR; //Tab for connections
	printConnections();
	cout << endl << "   " << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ;
	printTrainsHere();
}

void hub::recordDispatch()
{
	hub_stats[hub_stats.size() - 1].crewGiven++;
}

void hub::giveFuel(float requested, train* receiver)
{
	if (requested > fuel)
	{
		cout << "hub::giveFuel was asked to give more fuel than the hub " << name << " has by " << receiver->getName() << endl;
		return;
	}
	else
	{
		//fuel = fuel - requested;
		hub_stats[hub_stats.size() - 1].fuelGiven += requested;
		receiver->refuel(requested);

	}
}

//ONLY CALL AFTER SIM IS DONE
hub::hubStats hub::getHubStats()
{
	hubStats result = hub_stats[hub_stats.size() - 1];
	hub_stats.pop_back();
	return result;
}

void hub::newDayHub()
{
	hubStats newday;
	hub_stats.push_back(newday);
}