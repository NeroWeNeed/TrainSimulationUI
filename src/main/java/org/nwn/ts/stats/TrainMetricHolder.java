package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.TrainType;

public interface TrainMetricHolder extends MetricHolder {

    int getTotalDistanceTraveled();

    int getTotalCollisionsAvoided();

    double getProfit();

    int getTimesFueled();

    double getFuelUsed();

    TrainType getTrainType();


}
