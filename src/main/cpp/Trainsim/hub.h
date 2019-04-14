#pragma once
#include "global.h"
#include "node.h"
#include "track.h"
#include "train.h"

class hub : public node
{
private:
	list<train*> trainsOwned;
	float fuel;


public:
	hub(string _name, int ID, float _fuel);

	~hub();

	void setOwnership(train* newTrain);

	float giveFuel(float requested);

	void printInfo();

};

