#pragma once
#include "global.h"
#include "node.h"

class node; //Forward declaration

class load
{
private:
	int uniqueID;
	string name;

	loadType type;
	int amount;
	node* spawnPoint;
	node* destination;

	milTime begin;
	milTime pickup;
	milTime dropoff;

	struct loadStats
	{

	};

	loadStats stats;

public:
	load();
	~load();

	string getName();
	node* getSpawn();
	node* getDest();

	void setPickupTime(milTime when);
	void setDropoffTime(milTime when);

	loadType getType();
	int getAmount();

};


