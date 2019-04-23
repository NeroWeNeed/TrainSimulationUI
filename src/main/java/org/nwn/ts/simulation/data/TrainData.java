package org.nwn.ts.simulation.data;

import java.util.Objects;

public class TrainData {
    private final String name;
    private final String hubName;
    private final TrainType type;

    public TrainData(String name, String hubName, TrainType type) {
        this.name = name;
        this.hubName = hubName;
        this.type = type;
    }
    public TrainData(String name, HubData hub, TrainType type) {
        this.name = name;
        this.hubName = hub.name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getHubName() {
        return hubName;
    }

    public TrainType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainData)) return false;
        TrainData trainData = (TrainData) o;
        return name.equals(trainData.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "[TRAIN] " + name + " (" + hubName + ") " + " (" + type + ")";
    }
}
