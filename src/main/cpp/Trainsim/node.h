#pragma once
#include "global.h"
#include "train.h"

class train; //forward declaration

class node
{
protected:
	int uniqueID;
	string name;

	nodeType nType;
	vector<train*> trainsHere;
	vector<node*> connections;


public:
	node(string _name, int ID, nodeType _type);
	~node();

	void arrival(train* trainHere);
	void departure(train* trainGone);

	string getName();

	nodeType getNodeType();

	int getID();

	void printTrainsHere();

	void printConnections();

	bool addConnection(node* newCon);

	int getNumConnections();

	node* getConnection(int in);

	int getNumTrainsHere();

	train* getTrain(int in);
};

