#include "weatherSystem.h"
#include <ctime>
#include <random>

//Author: Abbass
//adapted to a non-inline class format by Brendan

weatherSystem::weatherSystem(int Type, float Severity)
{
	type = Type;
	severity = Severity;
}

weatherSystem::~weatherSystem()
{
}


void weatherSystem::weatherStatus()
{
	std::cout << "Today there is ";
	switch (type)
	{
	case 1:
		std::cout << "Windy Weather";
		break;
	case 2:
		std::cout << "Rainy Weather";
		break;
	case 3:
		std::cout << "Snowy Weather";
		break;
	default:
		std::cout << "Fair Weather";
		return;
		break;

	}
	std::cout << std::endl;
	std::cout << "Severity: ";
	for (int i = 0; i < severity / 2; ++i)
	{
		std::cout << "|";
	}
	for (int i = severity / 2; i < 50; ++i)
	{
		std::cout << ".";
	}

	std::cout << std::endl;
}

int weatherSystem::isWeatherGood(int maxDelay)
{
	int lowerBound;
	int upperBound;
	switch (type)
	{
	case 1:
		lowerBound = 15;
		upperBound = 65;
		break;
	case 2:
		lowerBound = 20;
		upperBound = 90;
		break;
	case 3:
		lowerBound = 25;
		upperBound = 100;
		break;
	default:
		lowerBound = 0;
		upperBound = 15;
		break;
	}

	float percentage = (severity / 100)*((lowerBound + 5) + (upperBound - lowerBound - 10));

	lowerBound = (percentage - 5);
	upperBound = (percentage + 5);
	float x = (((float)RNG(0, (upperBound)) / 100));
	return maxDelay * x;
}

int weatherSystem::RNG(int a, int b)
{
	std::mt19937 generator;
	generator.seed((unsigned int)std::time(0));
	std::uniform_int_distribution<uint32_t> rng(a, b);
	return rng(generator);
}