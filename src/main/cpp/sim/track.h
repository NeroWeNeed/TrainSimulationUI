#pragma once
#include "global.h"
#include "node.h"



class track : public node
{
private:
	int weight;
	int openTime;
	int closeTime;
	bool timed;
	bool good; //True if track may be used

public:
	track(string _name, int ID, nodeType _type, node* A,
		node* B, int _weight, int openTime, int closeTime);
	~track();

	int getWeight();
	int getOpenTime();
	int getCloseTime();
	void printInfo();

	bool collisionDetect();

	void setGood();
	void setBad();
	bool isGood();

	int getOtherSideID(int thisSideID);
};

