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

		bool operator<(const record& b);
	};

private: //maybe
	int uniqueID;
	string name;
	bool repeating;

	



	vector<record> path;

public:

	route();
	~route();

	vector<record> AstarPath(node * src, node * dst, vector<record> graph);
};

