package org.nwn.ts.simulation.data;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceData {
    private final List<EdgeMaintenanceData> edges = new ArrayList<>();
    private final List<TrainMaintenanceData> trains = new ArrayList<>();

    public List<EdgeMaintenanceData> getEdges() {
        return edges;
    }

    public List<TrainMaintenanceData> getTrains() {
        return trains;
    }

    @Override
    public String toString() {
        return "MaintenanceData{" +
                "edges=" + edges +
                ", trains=" + trains +
                '}';
    }
}
