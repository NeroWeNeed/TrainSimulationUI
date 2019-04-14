#pragma once
#include "global.h"
#include "track.h"
#include "train.h"

class station : public node
{
private:
	loadType type;
	list<load*> loadsHere;

	int maxTrains;
	int boardingRandMin;
	int boardingRandMax;
	int leavingRandMin;
	int leavingRandMax;
	float ticketPrice;

public:
	station(string _name, int ID, nodeType _type, loadType _loadType,
		int max, int bMax, int bMin, int Lmax, int Lmin, float price);
	~station();

	void printInfo(bool printID = false);
};

