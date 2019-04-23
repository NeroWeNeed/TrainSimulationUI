package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.StationData;

public class StationMetricHolderImpl implements StationMetricHolder {
    private String name;
    private int trainsStopped;
    private int itemsDroppedOff;
    private int itemsPickedUp;
    private int visits;

    public StationMetricHolderImpl(String name, int itemsDroppedOff, int itemsPickedUp, int visits, int trainsStopped) {
        this.trainsStopped = trainsStopped;
        this.itemsDroppedOff = itemsDroppedOff;
        this.itemsPickedUp = itemsPickedUp;
        this.visits = visits;
        this.name = name;
    }

    public StationMetricHolderImpl(StationData data, int itemsPickedUp, int itemsDroppedOff, int trainsStopped, int visits) {
        this(data.getName(), itemsDroppedOff, itemsPickedUp, visits, trainsStopped);
    }

    @Override
    public int getTrainsStopped() {
        return trainsStopped;
    }

    @Override
    public int getItemsDroppedOff() {
        return itemsDroppedOff;
    }

    @Override
    public int getItemsPickedUp() {
        return itemsPickedUp;
    }

    @Override
    public int getVisits() {
        return visits;
    }

    @Override
    public String getName() {
        return name;
    }
}
