#pragma once
#include "global.h"
#include "node.h"
#include "track.h"
#include "train.h"

class hub : public node
{
public:
	struct hubStats
	{
		float fuelGiven = 0;
		int crewGiven = 0;
	};

private:
	list<train*> trainsOwned;
	float fuel;
	vector<hubStats> hub_stats;

public:


	hub(string _name, int ID, float _fuel);

	~hub();

	void setOwnership(train* newTrain);

	void giveFuel(float requested, train* receiver);

	float getFuelRemaining();

	void recordDispatch();

	void printInfo();

	hubStats getHubStats();

	void newDayHub();
};

