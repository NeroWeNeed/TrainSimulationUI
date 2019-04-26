#include <iostream>
#include <string>
#include <fstream>
#include "SuggestionFile.h"

using namespace std;

int main()
{
	string theFile = "suggestions.txt";

	SuggestionFile temp(theFile);

	SuggestionFile::edge brendanEdge;

	brendanEdge.station1 = "STATION1";
	brendanEdge.station2 = "STATION2";
	brendanEdge.totalTraffic = 445;

	temp.writeHeader();
	temp.mirrorEdge(brendanEdge);
	temp.idleTime(78);
	temp.highestDelay("ROUTE1", 65);
}

