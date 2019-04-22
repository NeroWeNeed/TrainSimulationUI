package org.nwn.ts.util.validator.data;

import org.nwn.ts.stats.TrainType;

public class TrainConfigurationData {
    private int fuelCapacity;
    private int fuelCost;
    private int speed;
    private int capacity;

    public TrainConfigurationData(int fuelCapacity, int fuelCost, int speed, int capacity) {
        this.fuelCapacity = fuelCapacity;
        this.fuelCost = fuelCost;
        this.speed = speed;
        this.capacity = capacity;
    }

    public TrainConfigurationData() {
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public int getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(int fuelCost) {
        this.fuelCost = fuelCost;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
