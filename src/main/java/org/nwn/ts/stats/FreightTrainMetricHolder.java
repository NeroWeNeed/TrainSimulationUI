package org.nwn.ts.stats;

public interface FreightTrainMetricHolder extends TrainMetricHolder {
    int getTotalRefuels();
    double getTotalWeightCarried();
    double getHeaviest();
    double getFuelUsage();
}
