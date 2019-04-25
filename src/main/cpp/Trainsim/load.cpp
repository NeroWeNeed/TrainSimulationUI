#include "load.h"



load::load(string _name, int ID, vector<node*> _routeStops, vector<milTime> times, int _day)
{
	complete = false;
	name = _name;
	uniqueID = ID;
	type = PASSENGER;
	amount = 0;
	routeStops = _routeStops;
	stopTimes = times;
	dayWhen = _day;
	
	multipleStops = false;

	if (routeStops.size() == 0) cout << "load constructor encountered load with no route stops." << endl;
	if (stopTimes.size() == 0) cout << "load constructor encountered load with no expected stop times." << endl;
	if (routeStops.size() != stopTimes.size()) cout << "load constructor encountered load with number of station stops != number of expected stop times." << endl;
	else if (routeStops.size() > 1)
	{
		spawnPoint = routeStops[routeStops.size() - 1]; //Spawn point is last in vector
		routeStops.pop_back();

		begin = stopTimes[stopTimes.size() - 1]; //First arrival time is next last in vector
		stopTimes.pop_back();

		currentStop = routeStops[routeStops.size() - 1];
		routeStops.pop_back();

		if (routeStops.size() > 0) multipleStops = true;

		stats.intendedStart = begin;
		stats.intendedEnd = stopTimes[0];
	}
	else
	{
		cout << "load constructor encountered load without enough route stops" << endl;
	}
}

load::load(string _name, int ID, int amt, node* spawn, node* destination, milTime start, int _day)
{
	complete = false;
	name = _name;
	uniqueID = ID;
	type = FREIGHT;
	amount = amt;
	multipleStops = false;
	spawnPoint = spawn;
	currentStop = destination;
	begin = start;
	dayWhen = _day;

	stats.intendedStart = begin;

}


load::~load()
{

}


string load::getName() { return name; }

loadType load::getType() { return type; }

node* load::getSpawn() { return spawnPoint; }

node* load::getCurrentStop() { return currentStop; }

milTime load::getBegin() { return begin; }

bool load::multiStop() { return multipleStops; }

int load::getAmount() { return amount; }

int load::getDay() { return dayWhen; }

void load::addPassengers(int amt)
{
	if (type != PASSENGER)
	{
		cout << "load::addPassengers was called for non-passenger load " << name << endl;
	}
	else
	{
		amount = amount + amt;
	}
}

void load::subPassengers(int amt)
{
	if (type != PASSENGER)
	{
		cout << "load::subPassengers was called for non-passenger load " << name << endl;
	}
	else
	{
		amount = amount - amt;
		if (amount < 0) amount = 0;
	}
}

void load::setPickupTime(milTime when) 
{
	pickupTimes.push_back(when);
	if (pickupTimes.size() == 1)
	{
		stats.actualStart = when;
	}
}

void load::setDropoffTime(milTime when) { dropofftimes.push_back(when); stats.actualEnd = when; }

//Returns true if attempting to cycle while out of stops
bool load::cycleStops()
{
	if (routeStops.size() > 0)
	{
		currentStop = routeStops[routeStops.size() - 1];
		routeStops.pop_back();
	}
	else if (routeStops.size() == 0)
	{
		complete = true;
	}
	return complete;
}


void load::printInfo()
{
	char typeIndicator = 'P';
	if (type == FREIGHT) typeIndicator = 'F';
	cout << typeIndicator << " " << setw(15) << name << " found at " << setw(11) << spawnPoint->getName() << " starting on day " << dayWhen << " at ";
	cout << right << setfill('0') << setw(2) << begin.hour << ":" << setw(2) << begin.min << endl;

	cout << left << setfill(' ');
}

milTime load::getPickupTime()
{
	return stats.actualStart;
}

milTime load::getDropoffTime()
{
	return stats.actualEnd;
}

void load::transit(int minutes) { stats.transitTime += minutes; }

void load::setLateness(int wait)
{
	stats.minutesLate = wait;
}

load::loadStats load::getLoadStats()
{
	return stats;
}