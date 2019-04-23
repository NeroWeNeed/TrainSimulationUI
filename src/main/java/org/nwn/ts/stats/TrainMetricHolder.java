package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.TrainType;

public interface TrainMetricHolder extends MetricHolder {
    double getTotalDistanceTraveled();

    int getTotalCollisions();

    int getTotalWeight();

    TrainType getTrainType();



}
