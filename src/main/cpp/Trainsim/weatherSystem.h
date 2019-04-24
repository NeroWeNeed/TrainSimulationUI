#include "global.h"

//Author: Abbass
//adapted to a non-inline class format by Brendan

#pragma once
class weatherSystem
{
public:
	weatherSystem(int Type, float Severity);

	~weatherSystem();

	void weatherStatus();

	int isWeatherGood(int maxDelay);

	int RNG(int a, int b);

private:
	float severity;
	int type;
};

