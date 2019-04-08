package org.nwn.ts.stats;

public interface TrainMetricHolder extends MetricHolder {
    double getTotalDistanceTraveled();

    double getTotalPossibleCollisions();

    TrainType getTrainType();
    String getTrainName();

}
