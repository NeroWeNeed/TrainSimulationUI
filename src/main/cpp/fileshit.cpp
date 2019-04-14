//Brendan Sucks Program v69.666
//Everything I did in this program is wrongn't

//THINGS NEED TO TALK ABOUT:
/*	-Keeping track to total number of files processed
	-What does brendan want null string values to be
		-Char null values set to 'X'
	-What to do if line after Header isn't a control statement
	-Ending program if total number of hub isn't "XXXX" long
*/

#include "pch.h"
#include <iostream>
#include <fstream>
#include <string>
#include <vector>

using namespace std;

//stations & hubs
typedef struct {
	bool isHub;
	string name;
	char type;
	int trainMax;
	int peopleOn1;
	int peopleOn2;
	int peopleOff1;
	int peopleOff2;
	string ticketPrice;
} hub;

typedef struct {
	string node1;
	string node2;
	int weight;
	string startTime;
	string endTime;
} edge;

typedef struct {
	string name;
	string homeHub;
	char type;
} train;

//checks to see if the file was found/opened
void isFileOpen(ifstream &file)
{
	cout << "did the file open?  ";

	if (file.is_open())
	{
		cout << "gj idiot, the file opened" << endl;
	}
	else
	{
		cout << "couldn't find the file, idiot" << endl;
	}
}

//returns an int the tells program what instruction was found
int structReader(char lineStart, int lineNumber)
{

	if (lineStart == 'H' && lineNumber == 0)
	{
		//everything okay, header first line
		return 0;
	}
	else if (lineStart == 'H' && lineNumber != 0)
	{
		//exit program, header not first line
		return 3;
	}
	else if (lineStart == 'C' && lineNumber != 0)
	{
		//everthing okay, control struct in right spot
		return 1;
	}
	else if (lineStart == 'C' && lineNumber == 0)
	{
		//exit program, file started with control struct
		return 3;
	}
	else if (lineStart == 'T')
	{
		//everything okay, number of lines = total lines in file
		return 2;
	}
	/*else if (lineStart == 'T' && lineNumber != lineTotal)
	{
		//exit program, total lines reported and given don't match
		return 4;
	}*/

}

//prints out all nodes: hubs & stations
void printHubs(vector<hub> const &input)
{
	cout << "Node List Size: " << input.size() << endl;
	for (int i = 0; i < input.size(); i++)
	{
		if (input.at(i).isHub == true)
			cout << "Name: " << input.at(i).name << endl;
		else
		{
			cout << "Name: " << input.at(i).name;
			cout << "  Type: " << input.at(i).type;
			cout << "  Max Trains: " << input.at(i).trainMax;
			cout << "  People On: " << input.at(i).peopleOn1 << "/" << input.at(i).peopleOn2;
			cout << "  People off: " << input.at(i).peopleOff1 << "/" << input.at(i).peopleOff2;
			cout << "  Ticket Price: " << input.at(i).ticketPrice << endl;
		}
	}

	cout << endl;
}

void printEdges(vector<edge> const &input)
{
	cout << "Edge List Size: " << input.size() << endl;
	for (int i = 0; i < input.size(); i++)
	{
		cout << "Node1: " << input.at(i).node1;
		cout << "  Node2: " << input.at(i).node2;
		cout << "  Distance: " << input.at(i).weight;
		cout << "  Start/End Time: " << input.at(i).startTime << "/" << input.at(i).endTime << endl;
	}

	cout << endl;
}

void printTrains(vector<train> const &input)
{
	cout << "Train List Size: " << input.size() << endl;
	for (int i = 0; i < input.size(); i++)
	{
		cout << "Name: " << input.at(i).name;
		cout << "  Home Hub: " << input.at(i).homeHub;
		cout << "  Type: " << input.at(i).type << endl;
	}

	cout << endl;
}

int main()
{
	string theFile = "control.txt";

	ifstream controlFile;
	controlFile.open(theFile);
	
	isFileOpen(controlFile);

	vector<hub> nodes;
	vector<edge> edges;
	vector<train> trains;

	// starting stuff
	int lineIterator = 0;	//what line the program is at
	int instruction = -1;	//tells line status
	int lineTotal = 0;		//total lines, FOUND IN TAIL LINE
	int fileNumber = 0;		//file number form header line
	int controlLineTotal = 0;	//lines used for each control structure
	int fuckThis = 0;

	char lineStart = '?';	//tells program which instruction

	string temp = "";		//just used for holding shit
	string fileDate = "";	//read variable name idiot
	
	controlFile >> temp;

	lineStart = temp.at(0);

	instruction = structReader(lineStart, lineIterator);
	
	if (instruction == 0)
	{
		temp.erase(0, 1);

		fileNumber = stoi(temp);
		
		controlFile >> fileDate;
	}
	else
	{
		//file didn't start with header file, exit program
		cout << "File error: File didn't start with header line" << endl;
		return 0;
	}

	lineIterator++;

	while (!controlFile.eof())
	{
		controlFile >> temp;

		cout << "temp: " << temp << endl;

		instruction = structReader(temp.at(0), lineIterator);

		cout << "instruction: " << instruction << endl;

		if (instruction == 1)
		{
			fuckThis++;

			//do normal checking for control structure
			controlFile >> temp;

			//shitty names for nodes, sorry...
			hub Anode;
			edge Bnode;
			train Cnode;

			if (temp == "HUB")
			{
				lineIterator++;
				controlFile >> temp;

				if (temp.length() != 4)
				{
					//bad, throw error
					cout << "File error: number of control structure lines FORMAT was invalid" << endl;
					return 0;
				}

				controlLineTotal = stoi(temp);
				cout << "control line total: " << controlLineTotal << endl;

				for (int i = 0; i < controlLineTotal; i++)
				{ 
					Anode.isHub = true;

					controlFile >> Anode.name;

					nodes.push_back(Anode);

					lineIterator++;
				}
				
			}
			else if (temp == "STATION")
			{
				controlFile >> temp;
				lineIterator++;

				if (temp.length() != 4)
				{
					//bad, throw error
					cout << "File error: number of control structure lines FORMAT was invalid" << endl;
					return 0;
				}

				controlLineTotal = stoi(temp);
				cout << "control line total: " << controlLineTotal << endl;

				for (int i = 0; i < controlLineTotal; i++)
				{
					Anode.isHub = false;

					controlFile >> Anode.name;					
					controlFile >> Anode.type;
					controlFile >> Anode.trainMax;
					controlFile >> Anode.peopleOn1;
					controlFile >> Anode.peopleOn2;
					controlFile >> Anode.peopleOff1;
					controlFile >> Anode.peopleOff2;
					controlFile >> Anode.ticketPrice;

					nodes.push_back(Anode);

					lineIterator++;
				}


			}
			else if (temp == "EDGE")
			{
				controlFile >> temp;
				lineIterator++;

				if (temp.length() != 4)
				{
					//bad, throw error
					cout << "File error: number of control structure lines FORMAT was invalid" << endl;
					return 0;
				}

				controlLineTotal = stoi(temp);
				cout << "control line total: " << controlLineTotal << endl;
				
				for (int i = 0; i < controlLineTotal; i++)
				{
					controlFile >> Bnode.node1;
					controlFile >> Bnode.node2;
					controlFile >> Bnode.weight;
					controlFile >> Bnode.startTime;
					controlFile >> Bnode.endTime;

					edges.push_back(Bnode);

					lineIterator++;
				}
			}
			else if (temp == "LOCOMOTIVE")
			{
				controlFile >> temp;
				lineIterator++;

				if (temp.length() != 4)
				{
					//bad, throw error
					cout << "File error: number of control structure lines FORMAT was invalid" << endl;
					return 0;
				}

				controlLineTotal = stoi(temp);
				cout << "control line total: " << controlLineTotal << endl;

				for (int i = 0; i < controlLineTotal; i++)
				{
					controlFile >> Cnode.name;
					controlFile >> Cnode.homeHub;
					controlFile >> Cnode.type;

					trains.push_back(Cnode);

					lineIterator++;
				}
			}
			else
			{
				//invalid format
				cout << "File error: Invalid file format" << endl;
			}
		}
		else if (instruction == 2)
		{
			lineIterator++;

			controlFile >> temp;

			lineTotal = stoi(temp);

			if (lineTotal == (lineIterator - 2))
			{
				cout << "GOOD JOB ASSHOLE, YOUR PROGRAM WORKED" << endl;
			}
			else
			{
				cout << "YOU FUCKED SOMETHING UP ASSHAT" << endl;
			}

			controlFile >> temp;

			lineTotal = stoi(temp);


			if (lineTotal == (lineIterator - fuckThis - 2))
			{
				cout << "GOOD JOB ASSHOLE, YOUR PROGRAM WORKED" << endl;
			}
			else
			{
				cout << "YOU FUCKED SOMETHING UP ASSHAT" << endl;
			}
		}
	}

	controlFile.close();

	cout << "starting print lists" << endl << endl;
	printHubs(nodes);
	printEdges(edges);
	printTrains(trains);
	cout << "line total: " << lineIterator;

	cout << endl;
}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu

// Tips for Getting Started: 
//   1. Use the Solution Explorer window to add/manage files
//   2. Use the Team Explorer window to connect to source control
//   3. Use the Output window to see build output and other messages
//   4. Use the Error List window to view errors
//   5. Go to Project > Add New Item to create new code files, or Project > Add Existing Item to add existing code files to the project
//   6. In the future, to open this project again, go to File > Open > Project and select the .sln file