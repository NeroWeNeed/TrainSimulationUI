#include "route.h"
#include "track.h"
route::route()
{
}


route::~route()
{
}


bool route::record::operator<(const record & b)
{
	return (b.distance > this->distance);
}


vector<route::record> route::AstarPath(node* src, node* dst, vector<record> graph)
{
	vector<record> traceback;

	if (dst == src) return traceback; //If source is dest, we don't need to make a path

	vector<record*> unexplored;
	//vector<record*> explored;

	int dstID = dst->getID();

	for (int i = 0; i < graph.size(); i++)
	{
		if (graph[i].thisNode == src) graph[i].distance = 0;	//Initialize starting node's distance to 0
		if (graph[i].thisNode == src) graph[i].weightBefore = 0;	//Initialize starting node's prevWeight to 0
		unexplored.push_back(&(graph[i]));
	}

	bool foundTrueDestination = false; //Set to trigger return process. Only set if dest was found through a good track

	while (unexplored.empty() == false)
	{

		vector<record*>::iterator iter;
		vector<record*>::iterator min = unexplored.begin();
		record* current = unexplored.at(0);

		int iterInt = 0;
		for (iter = unexplored.begin(); iter < unexplored.end(); iter++, iterInt++) //Get lowest cost unexplored node
		{
			if (unexplored[iterInt]->distance < current->distance)
			{
				current = unexplored[iterInt];
				min = iter; //Record which one is your minimum
			}
		}
		unexplored.erase(min); //Remove the node we found from the unexplored set

		for (int i = 0; i < current->thisNode->getNumConnections(); i++) //For each track exiting our current node
		{
			int badConnections = 0;
			track* currentConnection = static_cast<track*>(current->thisNode->getConnection(i)); //Find the corresponding track object
			int connectionDestinationID = currentConnection->getOtherSideID(current->thisNode->getID()); //Determine this track's endpoint's ID

			if ((currentConnection->isDown() == true) || (currentConnection->isOpen() == false))
			{
				badConnections++;
				continue; //Skip closed/disabled tracks
			}

			int connectionWeight = currentConnection->getWeight(); //Determine weight of this track
			int currentDist = current->distance + connectionWeight; //Determine cost of going to this node

			if (currentConnection->isOpen() == false)
			{
				cout << "    pathfinder: INVALID TRACK" << endl;
				currentDist = INT_MAX; //If this track is closed, set its cost to be infinite
			}
			else if (connectionDestinationID == dstID) //IF WE HAVE FOUND THE DESTINATION THROUGH LEGITIMATE MEANS
			{
				foundTrueDestination = true;
			}

			if (currentDist < graph[connectionDestinationID].distance) //if we have found a new shortest path, record it
			{
				graph[connectionDestinationID].distance = currentDist; //record distance
				graph[connectionDestinationID].weightBefore = currentConnection->getWeight(); //record distance
				graph[connectionDestinationID].prev = current;   //record precursor
			}
		}
		if (foundTrueDestination == true) break;
	}

	//Re-trace and deep copy the shortest route
	bool failureDetectionOne = false;
	bool failureDetectionTwo = false;
	if (foundTrueDestination == true && dstID != INT_MAX) //Why am i checking this second condition
	{
		record* current = &(graph[dstID]);
		
		traceback.push_back(record(*current));

		while (current->prev != NULL)
		{
			current = &(graph[current->prev->thisNode->getID()]);

			if (traceback.size() > 2)
			{
				if ((!failureDetectionOne) && (!failureDetectionTwo) && (current->thisNode->getID() == traceback[traceback.size() - 2].thisNode->getID()))
				{
					failureDetectionOne = true;
				}

				else if ((!failureDetectionTwo) && (current->thisNode->getID() == traceback[traceback.size() - 2].thisNode->getID()))
				{
					failureDetectionTwo = true;
				}
			}

			traceback.push_back(record(*current));

			if (failureDetectionOne && failureDetectionTwo) break;
		}
	}
	if (failureDetectionOne && failureDetectionTwo)
	{
		cout << "route::AstarPath failed to create a path from " << src->getName() << " to " << dst->getName() << endl;
		traceback.clear();
	}

	//Set all of the distances back to INT_MAX for the next pathfinder call
	for (int i = 0; i < graph.size(); i++)
	{
		graph[i].distance = INT_MAX;
		graph[i].weightBefore = INT_MAX;
	}
	return traceback;
}

