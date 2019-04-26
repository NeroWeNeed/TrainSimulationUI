#pragma once
#include <string>
#include "global.h"

using namespace std;


struct simulationTrainParameters {
     int fuelCapacity;
     int fuelCost;
     int speed;
     int capacity;
};

//Info passed by java frontend
struct simulationParameters
{


     float hubFuel = 10000.00;		// :^) not used anymore but don't delete it because i don't feel like dealing with it

     int tickMinutes = 1;			//Probably leave this at 1

     int trainSpeed;					//Weight units per hour
     float trainFuelPerWeight;		//fuel cost per weight unit
     int passengerCap;				//Passenger train capacity
     int preemptiveCrewTime;			//When refuelling, if the crew is beyond this time (minutes), they will be swapped to avoid swapping later
     float cargoPrice;				//per-amount price for cargo transport. Revenue for us
     float fuelCost;					//Cost of 1.00% of a fuel tank

     int preemptiveLoadDispatchTime;	//Dispatch a train when a load has (this) much time left before pickup time (Do not set low or you'll always be late)

     day startDay;			//Monday, tuesday...
     milTime startTime;		//Hour,Min
     int duration;			//Days

     bool weatherToggle;		// :^)
     int weatherType;		// 0 is least bad, 3 is snow
     int weatherSeverity;	//0 to 100 i think
     int maxWeatherDelay;	//Natural maximum is 100 on worst settings
     int crewsPerHub;
     simulationTrainParameters freightTrainParameters;
     simulationTrainParameters passengerTrainParameters;
};






struct nodeDetails
{
     bool isHub;
     string name;
     loadType type;
     int trainMax;
     int peopleOn1;
     int peopleOn2;
     int peopleOff1;
     int peopleOff2;
     double ticketPrice;
     bool disabled;
};

struct edgeDetails
{
     string node1;
     string node2;
     int weight;
     int startTime;
     int endTime;
     bool disabled;
};

struct trainDetails
{
     string name;
     string homeHub;
     loadType type;
     bool disabled;
};

struct freightDetails
{
     string station1;
     string station2;
     day when;
     int startTime;
     int capacity;
};

struct routeDetails
{
     string station;
     int time;
};

struct dailyPassengerRouteDetails
{
     int day;
     vector<routeDetails> stations;
};
struct dailyFreightRouteDetails
{
     int day;
     vector<freightDetails> stations;
};

struct edgeMaintenanceDetails
{
     int day;

     string node1;
     string node2;
     int daysDown;
};

struct trainMaintenanceDetails
{
     int day;

     string name;
     int daysDown;
};




struct simulationDetails {
     vector<nodeDetails> nodes = vector<nodeDetails>();
     vector<edgeDetails> edges = vector<edgeDetails>();
     vector<trainDetails> trains = vector<trainDetails>();
     vector<edgeMaintenanceDetails> edgeMaintenance = vector<edgeMaintenanceDetails>();
     vector<trainMaintenanceDetails> trainMaintenance = vector<trainMaintenanceDetails>();
     vector<freightDetails> repeatingFreightRoutes = vector<freightDetails>();
     vector<dailyPassengerRouteDetails> repeatingPassengerRoutes = vector<dailyPassengerRouteDetails>(); // day = -1 or something
     vector<dailyPassengerRouteDetails> dailyPassengerRoutes = vector<dailyPassengerRouteDetails>();
     vector<dailyFreightRouteDetails> dailyFreightRoutes = vector<dailyFreightRouteDetails>();
     simulationParameters configuration = simulationParameters();

     //Used for making routes
     dailyPassengerRouteDetails currentDailyPassengerRoute = dailyPassengerRouteDetails{ 1 };
     dailyFreightRouteDetails currentDailyFreightRoute = dailyFreightRouteDetails{ 1 };
     dailyPassengerRouteDetails currentRepeatableRoute = dailyPassengerRouteDetails{ -1 };

};