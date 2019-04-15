#include "load.h"



load::load()
{

}


load::~load()
{

}


string load::getName() { return name; }

loadType load::getType() { return type; }

node* load::getSpawn() { return spawnPoint; }

node* load::getDest() { return destination; }


void load::setPickupTime(milTime when) { pickup = when; }

void load::setDropoffTime(milTime when) { dropoff = when; }