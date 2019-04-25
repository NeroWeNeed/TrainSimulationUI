#pragma once
#include "global.h"
#include "track.h"
#include "train.h"

class station : public node
{
public:
	struct stationStats
	{
		int dropoffs = 0;
		int pickups = 0;
	};

private:
	loadType type;
	list<load*> loadsHere; //Unused, whoops
	vector<stationStats> station_stats;

	struct hubDistance //Every station will hold records of which hubs are what distance away
	{
		node* hubDist;
		int distance;
	};

	int maxTrains;
	int boardingRandMin;
	int boardingRandMax;
	int leavingRandMin;
	int leavingRandMax;
	float ticketPrice;
	vector<hubDistance> hubDistances;

public:
	station(string _name, int ID, nodeType _type, loadType _loadType,
		int max, int bMax, int bMin, int Lmax, int Lmin, float price);
	~station();

	void printInfo(bool printID = false);
	void addHubDistance(node* theHub, int dist);
	void getBoardingInfoRef(int* minOn, int* minOff, int* maxOn, int* maxOff);

	node* getNthClosestHub(int N);
	int getFarthestHubDistance();

	void recordPickup();
	void recordDropoff();

	stationStats getStationStats();

	void newDayStation();

	float getTicketPrice();
};

