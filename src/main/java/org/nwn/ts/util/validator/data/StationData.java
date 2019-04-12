package org.nwn.ts.util.validator.data;

import org.nwn.ts.stats.TrainType;

public class StationData {

    final String name;
    final TrainType type;
    final int maxTrains;
    final int randomOnRange;
    final int randomOffRange;
    final double ticketPrice;

    public StationData(String name, TrainType type, int maxTrains, int randomOnRange, int randomOffRange, double ticketPrice) {
        this.name = name;
        this.type = type;
        this.maxTrains = maxTrains;
        this.randomOnRange = randomOnRange;
        this.randomOffRange = randomOffRange;
        this.ticketPrice = ticketPrice;

    }

    public String getName() {
        return name;
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
}
