package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.TrainType;

public class PassengerTrainMetricHolderImpl implements PassengerTrainMetricHolder {

    private String name;
    private int totalDistanceTraveled;
    private int totalCollisionsAvoided;
    private double profit;
    private int timesFueled;
    private double fuelUsed;
    private int totalPassengers;
    private int acceptedPassengers;

    public PassengerTrainMetricHolderImpl(String name, int totalDistanceTraveled, int totalCollisionsAvoided, double profit, int timesFueled, double fuelUsed, int totalPassengers, int acceptedPassengers) {
        this.name = name;
        this.totalDistanceTraveled = totalDistanceTraveled;
        this.totalCollisionsAvoided = totalCollisionsAvoided;
        this.profit = profit;
        this.timesFueled = timesFueled;
        this.fuelUsed = fuelUsed;
        this.totalPassengers = totalPassengers;
        this.acceptedPassengers = acceptedPassengers;
    }

    @Override
    public TrainType getTrainType() {
        return TrainType.PASSENGER;
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
    public int getTotalPassengers() {
        return totalPassengers;
    }

    @Override
    public int getAcceptedPassengers() {
        return acceptedPassengers;
    }
}
