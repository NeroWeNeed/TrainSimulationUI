#pragma once
#include <iostream>
#include <list>
#include <vector>
#include <iomanip>
#include <string>

using namespace std;

enum loadType { PASSENGER, FREIGHT, UNDEF };

enum nodeType {TRACK, STATION, HUB, HUBTRACK};

enum trainState {IDLE, SEEK, HAUL, WAIT, FUEL, HOME, MAIN};

enum day {MON, TUE, WED, THR, FRI, SAT, SUN, ALL, WKD};

struct errorState
{
	bool lowError = false;	//For errors we can correct
	bool highError = false; //For errors we can't/won't
	string message;			//For the log
	signed int code = 0;	//For when context alone cannot be used for correction
};

struct milTime
{
	int hour;
	int min;

	void operator++()
	{
		if (min < 60) min++;
		else
		{
			min = 0;
			hour++;
		}
	}
};

//define some shorthand ways of getting high ASCII characters
const char PIPE_HORZ = (char)196;
const char PIPE_VERT = (char)179;
const char PIPE_TR   = (char)192;
const char PIPE_DLR  = (char)194;
const char PIPE_UDR  = (char)195;

const char D_VERTPIPE = (char)186;
const char SHADE1     = (char)176; // 25%
const char SHADE2     = (char)177; // 50%
const char SHADE3     = (char)178; // 75%
const char SHADE4     = (char)219; // 100%


