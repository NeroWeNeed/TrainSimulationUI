package org.nwn.ts.simulation.data;

public class StationData extends NodeData {


    private final TrainType type;
    private final int maxTrains;
    private final int randomOnRange;
    private final int randomOffRange;
    private final double ticketPrice;

    public StationData(String name, TrainType type, int maxTrains, int randomOnRange, int randomOffRange, double ticketPrice) {
        this.name = name;
        this.type = type;
        this.maxTrains = maxTrains;
        this.randomOnRange = randomOnRange;
        this.randomOffRange = randomOffRange;
        this.ticketPrice = ticketPrice;

    }



    public TrainType getType() {
        return type;
    }

    public int getMaxTrains() {
        return maxTrains;
    }

    public int getRandomOnRange() {
        return randomOnRange;
    }

    public int getRandomOffRange() {
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
