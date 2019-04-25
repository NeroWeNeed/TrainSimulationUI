#include <string>
#include <fstream>
#include <iostream>

using namespace std;

class SuggestionFile {
public:
	SuggestionFile(string file)
	{
		outFile.open(file);
	}

	~SuggestionFile()
	{
		outFile.close();
	}

	void writeHeader()
	{
		outFile << "|Suggested Changes File|" << endl << endl;
	}

	//just using this struct as an example for mirrorEdge()
	typedef struct {
		string station1;
		string station2;
		int totalTraffic;
	} edge;

	//method tells user to add a mirror edge to relieve congestion
	//intakes: the edge with most traffic OR collisions
	//outputs: line to file
	void mirrorEdge(edge theEdge)
	{

		outFile << "Additional Edges:" << endl;
		outFile << "The track between " << theEdge.station1 << " and " << theEdge.station2 << " was found to have the highest traffic, with a total usage of " << theEdge.totalTraffic << " times." << endl;
		outFile << " - Adding an another edge between the two stations may help increase efficiency." << endl << endl;
	} 

	//uses an "average" idle time metric to determine if user should add more trains
	//intakes: train total idle time for all trains, or the train with highest amount of idle time
	//compares downtime parameter with the "average" metric and tells user wether or not to add trains
	void idleTime(int idleTime)
	{
		string totalTrainDownTime = "The total total train idle time is ";
		string singleTrainDownTime = "One of your trains has an idle time of ";

		//dis for you to change
		int brendanMetric = 75;

		outFile << "Additional Trains:" << endl;
		outFile << totalTrainDownTime << idleTime << " minutes, which is ";

		if (idleTime < brendanMetric)
		{
			outFile << "below the average." << endl;
			outFile << " - No new trains are required, trains are running efficiently." << endl << endl;
		}
		else if (idleTime > brendanMetric)
		{
			outFile << "above the average." << endl;
			outFile << " - Removing trains may decrease train idle time" << endl;
			outFile << " - Swapping the trains home hub may also help decrease idle time" << endl << endl;
		}
	}

	//method suggests changing the ROUTE with highest delay time
	//intakes: route with highest delay, time it is delayed by
	//just says the same thing every time
	void highestDelay(string routeName, int routeDelayTime)
	{
		outFile << "Routing Change:" << endl;
		outFile << "Route " << routeName << " was found to have the highest delay time. (" << routeDelayTime << " mins)" << endl;
		outFile << " - Adjusting the route for may decrease the delay time." << endl;
		outFile << " - Delaying the starting time may also help decrease delay times.";

	}

private:
	ofstream outFile;
};
