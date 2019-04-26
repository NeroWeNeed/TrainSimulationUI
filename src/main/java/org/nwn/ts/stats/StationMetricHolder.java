package org.nwn.ts.stats;

public interface StationMetricHolder extends MetricHolder {
    int getTrainsStopped();
    int getVisits();
    int getItemsDroppedOff();
    int getItemsPickedUp();

}
