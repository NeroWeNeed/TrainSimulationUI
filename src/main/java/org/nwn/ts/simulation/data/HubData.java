package org.nwn.ts.simulation.data;

public final class HubData extends NodeData {

    private boolean available = true;
    private boolean used = false;

    public HubData(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "[HUB] " + name;
    }
}
