package org.nwn.ts;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.nwn.ts.simulation.TrainSimulation;
import org.nwn.ts.stats.MetricHolder;
import org.nwn.ts.stats.SimulationDay;

public class Model {
    private static Model INSTANCE = new Model();

    public static Model getInstance() {
        return INSTANCE;
    }

    private Model() {

    }


    private ObjectProperty<TrainSimulation> simulation = new SimpleObjectProperty<>(new TrainSimulation());
    private ListProperty<SimulationDay> metrics = new SimpleListProperty<>(FXCollections.observableArrayList());
    private BooleanBinding metricsExists = Bindings.isNotEmpty(metrics);


    public Boolean metricsExists() {
        return metricsExists.get();
    }

    public BooleanBinding metricsExistsProperty() {
        return metricsExists;
    }

    public ObservableList<SimulationDay> getMetrics() {

        return metrics.get();
    }

    public ListProperty<SimulationDay> metricsProperty() {
        return metrics;
    }

    public void setMetrics(ObservableList<SimulationDay> metrics) {
        this.metrics.set(metrics);
    }



    public TrainSimulation getSimulation() {
        return simulation.get();
    }

    public ObjectProperty<TrainSimulation> simulationProperty() {
        return simulation;
    }

    public void setSimulation(TrainSimulation simulation) {
        this.simulation.set(simulation);
    }


}
