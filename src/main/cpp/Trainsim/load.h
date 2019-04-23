#pragma once
#include "global.h"
#include "node.h"
#include <string>

class node; //Forward declaration

class load
{
public:
	struct loadStats
	{
		milTime intendedStart;
		milTime intendedEnd;
		milTime actualStart;
		milTime actualEnd;
		int minutesLate = 0;
		int transitTime = 0;
	};
private:
	int uniqueID;
	string name;
	loadStats stats;

	bool complete;
	loadType type;
	int amount;

	node* spawnPoint;
	node* currentStop;
	vector<node*> routeStops; //Used for passenger routes. Load item keeps track of iteration
	bool multipleStops;

	int dayWhen; //numeric day on which the route takes place
	milTime begin; //Expected pickup time at spawnpoint
	vector<milTime> stopTimes; //Expected pickup times for routeStops
	vector<milTime> pickupTimes; //Actual pickup times
	vector<milTime> dropofftimes; //Actual dropoff times

public:
	load(string _name, int ID, int amt, node* spawn, node* destination, milTime start, int _day); //Freight version
	load(string _name, int ID, vector<node*> routeStop, vector<milTime> times, int _day); //Passenger version
	~load();

	string getName();
	node* getSpawn();
	node* getCurrentStop();
	milTime getBegin();

	void setPickupTime(milTime when);
	void setDropoffTime(milTime when);
	bool cycleStops();
	bool multiStop();

	loadType getType();
	int getAmount();
	int getDay();
	void addPassengers(int amt);
	void subPassengers(int amt);

	void printInfo();
	void setLateness(int wait);
	void transit(int minutes);
	milTime getPickupTime();
	milTime getDropoffTime();


};


