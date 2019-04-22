package org.nwn.ts.util.validator.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TrainSimulationUpdater {
    private List<Consumer<TrainSimulation>> updates = new ArrayList<>();
    private List<String> issues = new ArrayList<>();

    public TrainSimulationUpdater() {
    }

    public List<Consumer<TrainSimulation>> getUpdates() {
        return updates;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void apply(TrainSimulation simulation) {
        updates.forEach(x -> {
            x.accept(simulation);

        });
        updates.clear();
        issues.clear();
    }
}
