// ExportCSV.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include "pch.h"

#include <iostream>
#include<fstream>
#include<iostream>
#include<string>
#include<sstream>


using namespace std;

class Export
{
private:
	fstream fileToImport;
	fstream fileToExport;
	//The comma will separate each value
	const char comma = ',';
	string line;
	string word;
public:
	void exportingFiles()
	{
		string filename;
		string fileNameExport;

		//We want to open both files
		getline(cin, filename);
		getline(cin, fileNameExport);
		fileToImport.open(filename.c_str());

		if (fileNameExport.substr(fileNameExport.find_last_of(".") + 1) == "csv")
		{
			fileToExport.open(fileNameExport.c_str());

			while (!fileToImport.eof())
			{

				fileToExport << "Day, Day Number" << endl;

				//Reading in our day info
				while (getline(fileToImport, line))
				{
					stringstream stringStream(line);
					if ((line.substr() == "Train Name"))
					{
						word = "";
						break;
					}
					bool first = true;
					while (stringStream >> word)
					{
						if (!first)
						{
							fileToExport << comma;
						}
						fileToExport << word;
						first = false;
					}
					fileToExport << endl;
				}

				fileToExport << endl << "Train Name, Train Type, Total of Distance Traveled, Collisions, Weight" << endl;

				//Reading in our Train info
				while (getline(fileToImport, line))
				{

					stringstream stringStream(line);
					if ((line.substr() == "Station Name"))
					{
						word = "";
						break;
					}
					bool first = true;
					while (stringStream >> word)
					{
						if (!first)
						{
							fileToExport << comma;
						}
						fileToExport << word;
						first = false;
					}
					fileToExport << endl;
				}


				fileToExport << endl << "Station Name, Trains Stopped, Items Dropped Off, Items Picked Up, Passed Through" << endl;

				//Reading in our Station info
				while (getline(fileToImport, line))
				{
					stringstream stringStream(line);
					if ((line.substr() == "Track Name"))
					{
						word = "";
						break;
					}
					bool first = true;
					while (stringStream >> word)
					{
						if (!first)
						{
							fileToExport << comma;
						}
						fileToExport << word;
						first = false;
					}
					fileToExport << endl;
				}


				fileToExport << endl << "Track Name, Total Train Usage" << endl;

				//Reading in our hub info
				while (getline(fileToImport, line))
				{
					stringstream stringStream(line);
					if ((line.substr() == "Hub Name"))
					{
						word = "";
						break;
					}
					bool first = true;
					while (stringStream >> word)
					{
						if (!first)
						{
							fileToExport << comma;
						}
						fileToExport << word;
						first = false;
					}
					fileToExport << endl;
				}

				fileToExport << endl << "Hub Name, Amount of trains passed" << endl;

				while (getline(fileToImport, line))
				{
					stringstream stringStream(line);
					if ((line.substr() == "Day"))
					{
						word = "";
						break;
					}
					bool first = true;
					while (stringStream >> word)
					{
						if (!first)
						{
							fileToExport << comma;
						}
						fileToExport << word;
						first = false;
					}
					fileToExport << endl;
				}
				fileToExport << endl;
			}

			fileToImport.close();
			fileToExport.close();
			cout << "File is exported.  Take a look" << endl;
		}
		else
		{
			//Error message to provide correct format

			cout << "Cannot run export.  File type provided is not valid.  Can only output to .csv file type" << endl;

		}
	}
};

int main()
{
	Export exportThis;

	exportThis.exportingFiles();

	system("pause");
}