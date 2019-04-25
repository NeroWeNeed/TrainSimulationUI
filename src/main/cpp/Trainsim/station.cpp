#include "station.h"


station::station(string _name, int ID, nodeType _type, loadType _loadType, int max, int bMax, int bMin, int Lmax, int Lmin, float price) : node(_name, ID, STATION)
{
	type = _loadType;

	maxTrains = max;
	boardingRandMax = bMax;
	boardingRandMin = bMin;
	leavingRandMax = Lmax;
	leavingRandMin = Lmin;
	ticketPrice = price;

	stationStats first;
	station_stats.push_back(first);
}


station::~station()
{
}




void station::printInfo(bool printID)
{
	cout << PIPE_VERT;
	cout << setfill(' ') << left;

	cout << setw(11) << this->getName();
	
	if (printID) cout << " ID: " << setw(3) << this->getID() << " Closest hub: " << hubDistances[0].hubDist->getName();
	cout << endl << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_DLR; //Tab for connections
	printConnections();
	cout << endl << "   " << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ;
	printTrainsHere();

}


void station::addHubDistance(node* theHub, int dist)
{
	//Perform an ordered insertion
	if (hubDistances.size() == 0) hubDistances.push_back(hubDistance{ theHub, dist });
	else
	{
		vector<hubDistance>::iterator it = hubDistances.begin();
		for (int i = 0; i < hubDistances.size(); i++, it++)
		{
			//cout << "DEBUG: " << i << endl;
			if (dist <= hubDistances[i].distance)
			{
				hubDistances.emplace(it, hubDistance{ theHub, dist }); //Maintain order of smallest to largest
				break;
			}
			else if (i + 1 >= hubDistances.size())
			{
				hubDistances.push_back(hubDistance{ theHub, dist });
				break;
			}
		}
	}
}

node* station::getNthClosestHub(int N)
{
	if (N >= hubDistances.size())
	{
		cout << "station::getNthClosestHub was called with N greater than number of hubs." << endl;
		return NULL;
	}
	else
	{
		return hubDistances[N].hubDist;
	}
}

int station::getFarthestHubDistance()
{
	int max = 0;
	for (int i = 0; i < hubDistances.size(); i++)
	{
		if (max < hubDistances[i].distance) max = hubDistances[i].distance;
	}

	return max;
}

void station::getBoardingInfoRef(int* minOn, int* minOff, int* maxOn, int* maxOff)
{
	*minOn = boardingRandMin;
	*maxOn = boardingRandMax;
	*minOff = leavingRandMin;
	*maxOff = leavingRandMax;
}

void station::recordPickup()
{
	station_stats[station_stats.size() - 1].pickups++;
}

void station::recordDropoff()
{
	station_stats[station_stats.size() - 1].dropoffs++;
}

station::stationStats station::getStationStats()
{
	stationStats result = station_stats[station_stats.size() - 1];
	station_stats.pop_back();
	return result;
}

void station::newDayStation()
{
	stationStats newstat;
	station_stats.push_back(newstat);
}

float station::getTicketPrice() { return ticketPrice; }