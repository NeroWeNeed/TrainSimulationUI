package org.nwn.ts.simulation.data;

import org.nwn.ts.util.IntRange;

public class StationData extends NodeData {


    private final TrainType type;
    private final int maxTrains;
    private final IntRange randomOnRange;
    private final IntRange randomOffRange;
    private final double ticketPrice;
    private boolean available = true;
    private boolean used = false;

    public StationData(String name, TrainType type, int maxTrains, IntRange randomOnRange, IntRange randomOffRange, double ticketPrice) {
        this.name = name;
        this.type = type;
        this.maxTrains = maxTrains;
        this.randomOnRange = randomOnRange;
        this.randomOffRange = randomOffRange;
        this.ticketPrice = ticketPrice;

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


    public TrainType getType() {
        return type;
    }

    public int getMaxTrains() {
        return maxTrains;
    }

    public IntRange getRandomOnRange() {
        return randomOnRange;
    }

    public IntRange getRandomOffRange() {
        return randomOffRange;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    @Override
    public String toString() {
        return "[STATION] " + name;
    }
}
