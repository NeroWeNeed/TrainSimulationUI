package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.TrainType;

public class FreightTrainMetricHolderImpl implements FreightTrainMetricHolder {
    private String name;
    private int totalDistanceTraveled;
    private int totalCollisionsAvoided;
    private double profit;
    private int timesFueled;
    private double fuelUsed;
    private int weightCarried;
    private int maxWeightCarried;

    public FreightTrainMetricHolderImpl(String name, int totalDistanceTraveled, int totalCollisionsAvoided, double profit, int timesFueled, double fuelUsed, int weightCarried, int maxWeightCarried) {
        this.name = name;
        this.totalDistanceTraveled = totalDistanceTraveled;
        this.totalCollisionsAvoided = totalCollisionsAvoided;
        this.profit = profit;
        this.timesFueled = timesFueled;
        this.fuelUsed = fuelUsed;
        this.weightCarried = weightCarried;
        this.maxWeightCarried = maxWeightCarried;
    }
    @Override
    public TrainType getTrainType() {
        return TrainType.FREIGHT;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    @Override
    public int getTotalCollisionsAvoided() {
        return totalCollisionsAvoided;
    }

    @Override
    public double getProfit() {
        return profit;
    }

    @Override
    public int getTimesFueled() {
        return timesFueled;
    }

    @Override
    public double getFuelUsed() {
        return fuelUsed;
    }

    @Override
    public int getWeightCarried() {
        return weightCarried;
    }

    @Override
    public int getMaxWeightCarried() {
        return maxWeightCarried;
    }
}
