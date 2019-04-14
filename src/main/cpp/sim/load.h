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

	struct loadStats
	{

	};

	loadStats stats;

public:
	load();
	~load();

	string getName();
	node* getSpawn();

};


