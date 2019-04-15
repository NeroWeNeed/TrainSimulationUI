package org.nwn.ts.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimulationDay {
    private List<MetricHolder> metrics = new ArrayList<>();
    private Integer day;

    public SimulationDay(Integer day, Collection<MetricHolder> metrics) {
        this.day = day;
        this.metrics.addAll(metrics);
    }

    public List<MetricHolder> getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return "Day " + day;
    }
}
