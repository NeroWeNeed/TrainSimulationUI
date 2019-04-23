#pragma once
#include "global.h"
#include "node.h"
#include "route.h"
#include "load.h"

class load; //Forward declaration
class route; //Forward declaration

class train
{
public:
	struct trainStats
	{
		int distance = 0;
		int collisionsAvoided = 0;
		int timesFuelled = 0;

		int upTime = 0;		//Seeking, hauling, homing (moving at all)
		int downTime = 0;	//maintenance, idle time
		int waitTime = 0;	//waiting, fuelling

		int maxCarried = 0;
		int totalCarried = 0;
		float fuelUsed = 0.0;

		int totalPassengers = 0;
		int acceptedPassengers = 0;
	};
private:
	int uniqueID;
	string name;
	vector<trainStats> stats;
	
	trainState state;			//What is its goal right now
	trainState exitState;		//What state will we be in after this state (Used when interrupted)

	loadType Ltype;				//Freight or passenger?
	int travelSpeed;			//trackweights per hour
	float travelSpeedPerMinute;	//former divided by 60
	float fuelPerWeight;		//Fuel cost per weight unit traversed

	node* home;			//Where the train idles
	node* location;		//Where the train is now
	node* direction;	//Where the train is about to be
	float progress;		//How close it is to being there. Percentage.
	node* destination;	//Where the train wants to end up after several directions

	int waitTime;		//Total time to wait
	int waitProgress;	//Time waited

	float fuel;			//Fuel remaining. Percentage.
	int crewtime;		//Time spent with the same crew. Has a hard limit that will induce a WAIT state

	vector<route::record> currentPath;	//Container of the route being taken
	load* loadCarried;					//Points to load being hauled
	load* loadSought;					//Points to load that it will haul (mutually exclusive)
	int capacity;						//Passenger capacity

	bool swapping;						//True if train is WAIT because of a crewswap

public:
	train(string _name, int ID, loadType _type, int speed, node* homeHub, int cap, float fuelUse);
	~train();

	void changeLocation();

	void teleport(node* dest); //NOT TO BE USED
	void teleport(node* dest, node* heading); //NOT TO BE USED - for forcing collisions during testing

	trainState getExitState();
	trainState getState();
	loadType getType();
	string getName();
	string getStateName();
	node* getHeading();
	node* getLocation();
	node* getDestination();
	node* getHome();
	bool hasLoad();
	bool hasPath();
	float getFuel();
	load* getLoadCarried();
	load* getLoadSought();
	int getCrewTime();

	void printInfo();
	void printRoute();

	void setPath(vector<route::record> newRoute);
	void deleteRoute();
	void seekLoad(load* newLoad);
	void pickupLoad(milTime when);
	void dropoffLoad(milTime when);
	void transferLoad(milTime when, int minOn, int maxOn, int minOff, int maxOff); //For handling passenger stations beyond the first one
	void setState(trainState newState);
	void setExitState(trainState newExitState);
	void move(int* minutes);
	void setWait(int minutes);
	void wait(int* minutes);
	void refuel(float amount);
	void addCrewTime(int minutes);
	void lockCrewTime();
	void newCrew();
	void setSwapFlag();
	void addDowntime(int amount);
	trainStats getTrainStats();
	void newDayTrain();

private:
	node* getNextTrackHeading();

};

