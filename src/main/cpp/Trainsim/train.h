#pragma once
#include "global.h"
#include "node.h"
#include "route.h"
#include "load.h"

class load; //Forward declaration
class route; //Forward declaration

class train
{
private:
	int uniqueID;
	string name;
	
	trainState state;			//What is its goal right now
	trainState exitState;		//What state will we be in after this state (Used when interrupted)

	loadType Ltype;				//Freight or passenger?
	int travelSpeed;			//trackweights per hour
	float travelSpeedPerMinute;	//former divided by 60

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

	bool rerouting;		//Unused?
	bool avoidance;		//Unused?

	struct trainStats
	{
		//TODO: stats system
	};

public:
	train(string _name, int ID, loadType _type, int speed, node* homeHub, int cap);
	~train();

	void changeLocation();

	void teleport(node* dest); //NOT TO BE USED
	void teleport(); //NOT TO BE USED

	trainState getState();
	loadType getType();
	string getName();
	string getStateName();
	node* getHeading();
	node* getLocation();
	node* getDestination();
	bool hasLoad();
	bool hasPath();
	float getFuel();
	load* getLoadCarried();
	load* getLoadSought();

	void printInfo();
	void printRoute();

	void setRoute(vector<route::record> newRoute);
	void deleteRoute();
	void seekLoad(load* newLoad);
	void pickupLoad(milTime when);
	void dropoffLoad(milTime when);
	void increaseLoad(load* source); //For adding passengers to train
	void decreaseLoad(int amount); //For releasing passengers from train
	void setState(trainState newState);
	void setExitState(trainState newExitState);
	void move(int* minutes);
	void setWait(int minutes);
	void wait(int* minutes);

private:
	node* getNextTrackHeading();

};

