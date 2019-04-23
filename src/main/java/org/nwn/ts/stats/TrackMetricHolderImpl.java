package org.nwn.ts.stats;

import org.nwn.ts.simulation.data.EdgeData;

public class TrackMetricHolderImpl implements TrackMetricHolder {
    private String name;
    private int visits;


    public TrackMetricHolderImpl(String name, int visits) {
        this.visits = visits;
        this.name = name;
    }

    public TrackMetricHolderImpl(EdgeData data, int visits) {
        this(String.format("%s <-> %s", data.getVertexA(), data.getVertexB()), visits);

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
