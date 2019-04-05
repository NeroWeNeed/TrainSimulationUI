package org.nwn.ts.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import org.nwn.ts.stats.MetricHolder;

import java.util.ArrayList;
import java.util.List;

public class UpdateMetricEvent extends Event {
    public static EventType<UpdateMetricEvent> UPDATE_METRICS_ALL = new EventType<UpdateMetricEvent>("UPDATE_METRICS_ALL");
    public static EventType<UpdateMetricEvent> UPDATE_METRICS = new EventType<>(UPDATE_METRICS_ALL, "UPDATE_METRICS");

    private List<MetricHolder> metrics;
    public UpdateMetricEvent(List<MetricHolder> metrics) {
        super(UPDATE_METRICS);
        this.metrics = new ArrayList<>(metrics);
    }

    public UpdateMetricEvent(Object source, EventTarget target,List<MetricHolder> metrics) {
        super(source, target, UPDATE_METRICS);
        this.metrics = new ArrayList<>(metrics);
    }
}
