package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.HubData;

public class HubMetricHolderImpl implements HubMetricHolder {
    private String name;
    private int visits;

    public HubMetricHolderImpl(String name, int visits) {
        this.visits = visits;
        this.name = name;
    }

    public HubMetricHolderImpl(HubData hub,int visits) {
        this(hub.getName(),visits);
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
