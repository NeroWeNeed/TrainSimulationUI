package org.nwn.ts.util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.nwn.ts.util.validator.data.TrainConfigurationData;

public class TrainConfigurationProperty {
    private IntegerProperty fuelCapacity = new SimpleIntegerProperty(0);
    private IntegerProperty fuelCost = new SimpleIntegerProperty(0);
    private IntegerProperty speed = new SimpleIntegerProperty(0);
    private IntegerProperty capacity = new SimpleIntegerProperty(0);

    public TrainConfigurationProperty() {
    }
    public TrainConfigurationData toData() {
        return new TrainConfigurationData(fuelCapacity.get(),fuelCost.get(),speed.get(),capacity.get());
    }

    public int getFuelCapacity() {
        return fuelCapacity.get();
    }

    public IntegerProperty fuelCapacityProperty() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity.set(fuelCapacity);
    }

    public int getFuelCost() {
        return fuelCost.get();
    }

    public IntegerProperty fuelCostProperty() {
        return fuelCost;
    }

    public void setFuelCost(int fuelCost) {
        this.fuelCost.set(fuelCost);
    }

    public int getSpeed() {
        return speed.get();
    }

    public IntegerProperty speedProperty() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed.set(speed);
    }

    public int getCapacity() {
        return capacity.get();
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity.set(capacity);
    }
}
