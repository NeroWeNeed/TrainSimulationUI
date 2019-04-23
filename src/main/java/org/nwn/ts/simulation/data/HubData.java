package org.nwn.ts.simulation.data;

public final class HubData extends NodeData {


    public HubData(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[HUB] " + name;
    }
}
