#pragma once
#include "global.h"
#include "node.h"



class track : public node
{
private:
	int weight;
	milTime openTime;
	milTime closeTime;
	bool timed;
	bool open; //True if track is open
	bool down; //True if track is under maintenance

public:
	track(string _name, int ID, nodeType _type, node* A,
		node* B, int _weight, milTime openTime, milTime closeTime);
	~track();

	int getWeight();
	milTime getOpenTime();
	milTime getCloseTime();
	void printInfo();

	bool collisionDetect();

	void setOpen();
	void setClosed();
	void setDown();
	void setUp();
	bool isOpen();
	bool isTimed();
	bool isDown();

	int getOtherSideID(int thisSideID);
};

