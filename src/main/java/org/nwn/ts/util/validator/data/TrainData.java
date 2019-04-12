package org.nwn.ts.util.validator.data;

import org.nwn.ts.stats.TrainType;

public class TrainData {
    final String name;
    final String hubName;
    final TrainType type;

    public TrainData(String name, String hubName, TrainType type) {
        this.name = name;
        this.hubName = hubName;
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
}
