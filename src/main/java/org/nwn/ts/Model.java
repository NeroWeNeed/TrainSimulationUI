package org.nwn.ts;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.nwn.ts.stats.MetricHolder;
import org.nwn.ts.stats.SimulationDay;
import org.nwn.ts.util.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private static Model INSTANCE = new Model();

    public static Model getInstance() {
        return INSTANCE;
    }

    private Model() {

    }

    private BooleanProperty baselineSet = new SimpleBooleanProperty(false);
    private ObjectProperty<Configuration> configuration = new SimpleObjectProperty<>(new Configuration());
    private ListProperty<SimulationDay> simulationDays = new SimpleListProperty<>(FXCollections.observableArrayList());
    private int sequence = 0;
    private int day = 0;

    public ObservableList<SimulationDay> getSimulationDays() {
        return simulationDays.get();
    }

    public ListProperty<SimulationDay> simulationDaysProperty() {
        return simulationDays;
    }

    public boolean getBaselineSet() {
        return baselineSet.get();
    }

    public BooleanProperty baselineSetProperty() {
        return baselineSet;
    }

    public void setBaselineSet(boolean baselineSet) {
        this.baselineSet.set(baselineSet);
    }

    public Configuration getConfiguration() {
        return configuration.get();
    }

    public ObjectProperty<Configuration> configurationProperty() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration.set(configuration);
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
