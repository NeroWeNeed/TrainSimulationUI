#include "track.h"

track::track(string _name, int ID, nodeType _type, node* A,
	node* B, int _weight, int open, int close) : node(_name, ID, _type)
{
	addConnection(A);
	addConnection(B);
	weight = _weight;
	openTime = open;
	closeTime = close;
	if (open >= close) timed = false;
	else timed = true;
	good = true;
}


track::~track()
{

}


int track::getWeight() { return weight; }

int track::getOpenTime() { return closeTime; }

int track::getCloseTime() { return closeTime; }

void track::printInfo()
{
	cout << setfill(' ');
	string connections = "**ERROR**";
	if (this->getNumConnections() == 2)
	{
		connections = getConnection(0)->getName() + " & " + getConnection(1)->getName();
	}
	

	cout << "|";
	cout << left << setw(9) << this->getName() << "ID: " << setw(4) << this->getID()  << " cost: " << setw(3) << weight;

	if (timed)
	{
		cout << " Times:" << setfill('0') << setw(4) << openTime << " to " << setw(4) << closeTime;
	}
	cout << setfill(' ');

	cout << endl << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_DLR; //Tab for connections
	printConnections();
	cout << endl << "   " << PIPE_TR << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ << PIPE_HORZ;
	printTrainsHere();


}

bool track::collisionDetect()
{
	if (trainsHere.size() > 1)
	{
		node* dirComp = trainsHere[0]->getHeading();

		for (int i = 1; i < trainsHere.size(); i++)
		{
			if (dirComp != trainsHere[i]->getHeading())
			{
				cout << "track::collisionDetect HAS DETECTED A COLLISION EVENT ON TRACK " << getName() << endl;
				cout << "TRAINS INVOLVED: ";
				printTrainsHere();
				cout << endl;
				return true;
			}
		}
	}
	return false;
}

void track::setGood() { good = true; }
void track::setBad() { good = false; }
bool track::isGood() { return good; }

int track::getOtherSideID(int thisSideID)
{
	if (connections[0]->getID() != thisSideID) return connections[0]->getID();
	else return connections[1]->getID();
}