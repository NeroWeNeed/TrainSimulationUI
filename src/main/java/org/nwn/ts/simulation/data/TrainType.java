package org.nwn.ts.simulation.data;

public enum TrainType {
    FREIGHT, PASSENGER;

    public static TrainType parse(String value) {
        if (value.equalsIgnoreCase("F"))
            return FREIGHT;
        else if (value.equalsIgnoreCase("P"))
            return PASSENGER;
        else
            return null;
    }

    public static TrainType parseFull(String value) {
        if (value.equalsIgnoreCase("FREIGHT"))
            return FREIGHT;
        else if (value.equalsIgnoreCase("PASSENGER"))
            return PASSENGER;
        else
            return null;
    }
    public static TrainType random() {
        if (Math.random() < 0.5) {
            return FREIGHT;
        }
        else {
            return PASSENGER;
        }

    }
}
