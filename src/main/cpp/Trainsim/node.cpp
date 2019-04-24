#include "node.h"

node::node(string _name, int ID, nodeType _type)
{
	name = _name;
	uniqueID = ID;
	nType = _type;

	nodeStats first;
	stats.push_back(first);
}


node::~node()
{

}

void node::arrival(train* newtrain)
{
	if (newtrain != NULL)
	{
		trainsHere.push_back(newtrain);
		stats[stats.size() - 1].trainsThru++;
	}
	else
	{
		cout << "node::arrival was passed a null train" << endl;
	}
}

void node::departure(train* trainGone)
{
	vector<train*>::iterator iter;
	int i = 0;
	bool match = false;
	if (trainGone != NULL)
	{
		for (iter = trainsHere.begin(); iter < trainsHere.end(); i++, iter++)
		{
			if (trainGone == trainsHere[i])
			{
				trainsHere.erase(iter);
				match = true;
				break;
			}
		}
		if (match == false)
		{
			cout << "node::departure was passed a train that was not present" << endl;
		}
	}
	else
	{
		cout << "node::departure was passed a null train" << endl;
	}
}


string node::getName() { return name; }

nodeType node::getNodeType() { return nType; }

int node::getID() { return uniqueID; }

void node::printTrainsHere()
{
	cout << "Trains: ";
	for (int i = 0; i < trainsHere.size(); i++)
	{
		cout << trainsHere[i]->getName();
		if (i + 1 < trainsHere.size()) cout << ", ";
	}
}

void node::printConnections()
{
	cout << "Connections: ";
	for (int i = 0; i < getNumConnections(); i++)
	{
		cout << connections.at(i)->getName();
		if (i + 1 < getNumConnections()) cout << ", ";
	}
}


bool node::addConnection(node* newCon)
{
	if (newCon == NULL)
	{
		cout << "Node::AddConnection Tried to add null connection" << endl;
		return true;
		//ERROR HANDLING
	}
	else
	{
		bool dupe = false;

		for (int i = 0; i < connections.size(); i++)
		{
			if (newCon == connections.at(i)) dupe = true;
		}

		if (dupe)
		{
			cout << "Node::AddConnection tried to add a duplicate connection" << endl;
			return true;
			//ERROR HANDLING
		}
		else
		{
			if (newCon->getNodeType() == TRACK || newCon->getNodeType() == HUBTRACK)
			{
				if (nType == STATION)
				{
					connections.push_back(newCon); //Success
				}
				else if (nType == HUB)
				{
					if (connections.size() > 0)
					{
						cout << "Node::addConnection tried to add too many connections to a hub" << endl;
						return true;
						//ERROR HANDLING
					}
					else
					{
						connections.push_back(newCon); //Success
					}

				}
				else
				{
					cout << "Node::addConnection tried to connect one track to another" << endl;
					return true;
					//ERROR HANDLING
				}

			}

			else if (newCon->getNodeType() == STATION || newCon->getNodeType() == HUB)
			{
				if (nType == TRACK || nType == HUBTRACK)
				{
					if (connections.size() > 1)
					{
						cout << "Node::addConnection tried to add too many connections to a track" << endl;
						return true;
						//ERROR HANDLING
					}
					else
					{
						connections.push_back(newCon); //Success
					}
				}
				else
				{
					cout << "Node::addConnection tried to connect one station/hub to another" << endl;
					return true;
					//ERROR HANDLING
				}
			}
			else
			{
				cout << "Node::AddConnection was passed a node* with uninitialized nodeType" << endl;
				return true;
				//ERROR HANDLING
			}
		}
	}
	return false;
}


int node::getNumConnections() { return connections.size(); }

node* node::getConnection(int in)
{
	if (in > connections.size())
	{
		//ERROR HANDLING
		cout << "Node::getConnection tried to access out-of-bounds location" << endl;
		return NULL;
	}
	else
	{
		return connections[in];
	}
}

int node::getNumTrainsHere() { return trainsHere.size(); }

train* node::getTrain(int in)
{
	if (in > trainsHere.size())
	{
		//ERROR HANDLING
		cout << "Node::getTrain tried to access out-of-bounds location" << endl;
		return NULL;
	}
	else
	{
		return trainsHere.at(in);
	}

}

void node::statsTrainStop() { stats[stats.size() - 1].trainStops++; }

node::nodeStats node::getNodeStats()
{
	nodeStats result = stats[stats.size() - 1];
	stats.pop_back();
	return result;
}

void node::newDayNode()
{
	nodeStats newstat;
	stats.push_back(newstat);
}