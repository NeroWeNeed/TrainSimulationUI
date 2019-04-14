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
}


station::~station()
{
}




void station::printInfo(bool printID)
{
	cout << PIPE_VERT;
	cout << setfill(' ') << left;

	cout << setw(11) << this->getName();
	
	if (printID) cout << " ID: " << setw(3) << this->getID();
	cout << endl << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_DLR; //Tab for connections
	printConnections();
	cout << endl << "   " << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ;
	printTrainsHere();

}
