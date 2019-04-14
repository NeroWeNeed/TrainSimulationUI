#include "hub.h"



hub::hub(string _name, int ID, float _fuel) : node(_name, ID, HUB)
{
	fuel = _fuel;
}


hub::~hub()
{
}

void hub::setOwnership(train* newTrain)
{
	trainsOwned.push_back(newTrain);
}


void hub::printInfo()
{
	cout << PIPE_VERT;
	cout << setfill(' ') << left;

	cout << setw(11) << this->getName() << " ID: " << setw(3) << this->getID();

	cout << endl << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_DLR; //Tab for connections
	printConnections();
	cout << endl << "   " << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ;
	printTrainsHere();
}
