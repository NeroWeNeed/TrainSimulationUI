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
	
	trainState state;
	loadType Ltype;
	int travelSpeed;
	node* home;

	node* direction;
	node* location;
	float progress;

	float fuel;
	int crewtime;

	vector<route::record> currentPath;
	load* loadCarried;

	bool rerouting;
	bool avoidance;

	struct trainStats
	{

	};

public:
	train(string _name, int ID, loadType _type, int speed, node* homeHub);
	~train();

	void depart();
	void arrive();

	void teleport(node* dest); //NOT TO BE USED
	void teleport(); //NOT TO BE USED

	trainState getState();
	loadType getType();
	string getName();
	string getStateName();
	node* getHeading();
	node* getLocation();
	bool hasLoad();
	float getFuel();
	load* getLoad();

	void printInfo();
	void printRoute();

	void setRoute(vector<route::record> newRoute);
	void setLoad(load* newLoad);
	void setState(trainState newState);

};

