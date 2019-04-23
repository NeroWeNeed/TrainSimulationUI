package org.nwn.ts.stats;

import javafx.beans.property.DoubleProperty;
import org.nwn.ts.simulation.data.TrainData;
import org.nwn.ts.simulation.data.TrainType;

public class TrainMetricHolderImpl implements TrainMetricHolder {

    private double totalDistanceTraveled;
    private int totalCollisions;
    private TrainType trainType;
    private String name;
    private int totalWeight;


    public TrainMetricHolderImpl(String name, TrainType trainType, int totalCollisions, double totalDistanceTraveled, int totalWeight) {
        this.totalDistanceTraveled = totalDistanceTraveled;
        this.totalCollisions = totalCollisions;
        this.trainType = trainType;
        this.name = name;
        this.totalWeight = totalWeight;
    }
    public TrainMetricHolderImpl(TrainData data, int totalCollisions, double totalDistanceTraveled, int totalWeight) {
        this(data.getName(),data.getType(),totalCollisions,totalDistanceTraveled, totalWeight);
    }

    @Override
    public double getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    @Override
    public int getTotalCollisions() {
        return totalCollisions;
    }
    @Override
    public TrainType getTrainType() {
        return trainType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getTotalWeight() {
        return totalWeight;
    }
}
