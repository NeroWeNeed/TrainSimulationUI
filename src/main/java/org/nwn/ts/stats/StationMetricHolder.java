package org.nwn.ts.stats;

public interface StationMetricHolder extends MetricHolder {
    int getTrainsStopped();
    int getItemsDroppedOff();
    int getItemsPickedUp();
    int getVisits();
}
