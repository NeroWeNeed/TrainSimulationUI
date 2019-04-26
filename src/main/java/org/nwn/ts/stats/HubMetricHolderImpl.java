package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.HubData;

public class HubMetricHolderImpl implements HubMetricHolder {
    private String name;
    private int fuelGiven;
    private int crewGiven;

    public HubMetricHolderImpl(String name, int fuelGiven, int crewGiven) {
        this.name = name;
        this.fuelGiven = fuelGiven;
        this.crewGiven = crewGiven;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getFuelGiven() {
        return fuelGiven;
    }

    @Override
    public int getCrewGiven() {
        return crewGiven;
    }
}
