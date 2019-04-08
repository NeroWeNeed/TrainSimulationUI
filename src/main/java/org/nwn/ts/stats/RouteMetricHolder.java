package org.nwn.ts.stats;

public interface RouteMetricHolder extends MetricHolder {
    long getScheduledStartTime();
    long getScheduledEndTime();
    long getStartTime();
    long getEndTime();
    long getTimeWaitingPickup();
    long getTimeWaitingDropoff();
}
