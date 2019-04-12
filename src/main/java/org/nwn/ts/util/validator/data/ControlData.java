package org.nwn.ts.util.validator.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ControlData {
    private final Map<String, StationData> stations = new HashMap<>();
    private final Set<EdgeData> edges = new HashSet<>();
    private final Map<String, TrainData> trains = new HashMap<>();

    public Map<String, StationData> getStations() {
        return stations;
    }

    public Set<EdgeData> getEdges() {
        return edges;
    }

    public Map<String, TrainData> getTrains() {
        return trains;
    }
}
