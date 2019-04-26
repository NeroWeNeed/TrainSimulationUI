package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.StationData;

public class StationMetricHolderImpl implements StationMetricHolder {
    private String name;
    private int trainsStopped;
    private int visits;
    private int itemsDroppedOff;
    private int itemsPickedUp;

    public StationMetricHolderImpl(String name, int trainsStopped, int visits, int itemsDroppedOff, int itemsPickedUp) {
        this.name = name;
        this.trainsStopped = trainsStopped;
        this.visits = visits;
        this.itemsDroppedOff = itemsDroppedOff;
        this.itemsPickedUp = itemsPickedUp;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getTrainsStopped() {
        return trainsStopped;
    }

    @Override
    public int getVisits() {
        return visits;
    }

    @Override
    public int getItemsDroppedOff() {
        return itemsDroppedOff;
    }

    @Override
    public int getItemsPickedUp() {
        return itemsPickedUp;
    }
}
