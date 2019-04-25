#pragma once
#include "global.h"
#include <queue>

class node; //Forward declaration
class track; //Forward declaration

class route
{
public:
	struct record //Records comprise one link of a route
	{
		node* thisNode;
		record* prev;
		int distance;
		int weightBefore; //Because train doesn't know the track class, this is used to communicate edge weight to trains
						  //Contains the weight of the edge leading up to this record's node.

		bool operator<(const record& b);
	};

private:
	//int uniqueID;
	//string name;
	//bool repeating;

	//vector<record> path;

public:

	route();
	~route();

	vector<record> AstarPath(node * src, node * dst, vector<record> graph);
};

