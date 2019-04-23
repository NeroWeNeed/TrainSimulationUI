package org.nwn.ts.simulation;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import org.nwn.ts.simulation.data.TrainType;
import org.nwn.ts.util.TrainConfigurationProperty;

import java.util.*;

public class TrainSimulationOverrides {
    private Map<TrainType, TrainConfigurationProperty> trainConfigurations = new HashMap<>();
    private IntegerProperty crewsPerHub = new SimpleIntegerProperty();
    private IntegerProperty fuelPerHub = new SimpleIntegerProperty();
    private IntegerProperty daysToRun = new SimpleIntegerProperty();
    private ObjectProperty<TrainSimulation.WeatherType> weatherType = new SimpleObjectProperty<>();
    private DoubleProperty weatherSeverity = new SimpleDoubleProperty();

    private BooleanBinding valid;

    private Observable[] validDependencies() {
        ArrayList<Observable> observables = new ArrayList<>();

        observables.add(crewsPerHub);
        observables.add(fuelPerHub);
        observables.add(daysToRun);
        observables.add(weatherType);
        observables.add(weatherSeverity);


        return observables.stream().toArray(Observable[]::new);
    }

    public TrainSimulationOverrides() {


        for (TrainType value : TrainType.values()) {
            TrainConfigurationProperty d = new TrainConfigurationProperty();


        }

        valid = Bindings.createBooleanBinding(() -> getCrewsPerHub() > 0 && getFuelPerHub() > 0 && getDaysToRun() > 0


                , crewsPerHub, fuelPerHub, daysToRun, weatherSeverity, weatherType);

    }

    public TrainConfigurationProperty getTrainConfiguration(TrainType type) {
        return trainConfigurations.get(type);
    }

    public Map<TrainType, TrainConfigurationProperty> getTrainConfigurations() {
        return trainConfigurations;
    }

    public int getCrewsPerHub() {
        return crewsPerHub.get();
    }

    public IntegerProperty crewsPerHubProperty() {
        return crewsPerHub;
    }

    public void setCrewsPerHub(int crewsPerHub) {
        this.crewsPerHub.set(crewsPerHub);
    }

    public int getFuelPerHub() {
        return fuelPerHub.get();
    }

    public IntegerProperty fuelPerHubProperty() {
        return fuelPerHub;
    }

    public void setFuelPerHub(int fuelPerHub) {
        this.fuelPerHub.set(fuelPerHub);
    }

    public int getDaysToRun() {
        return daysToRun.get();
    }

    public IntegerProperty daysToRunProperty() {
        return daysToRun;
    }

    public void setDaysToRun(int daysToRun) {
        this.daysToRun.set(daysToRun);
    }

    public TrainSimulation.WeatherType getWeatherType() {
        return weatherType.get();
    }

    public ObjectProperty<TrainSimulation.WeatherType> weatherTypeProperty() {
        return weatherType;
    }

    public void setWeatherType(TrainSimulation.WeatherType weatherType) {
        this.weatherType.set(weatherType);
    }

    public double getWeatherSeverity() {
        return weatherSeverity.get();
    }

    public DoubleProperty weatherSeverityProperty() {
        return weatherSeverity;
    }

    public void setWeatherSeverity(double weatherSeverity) {
        this.weatherSeverity.set(weatherSeverity);
    }

    public Boolean getValid() {
        return valid.get();
    }

    public BooleanBinding validProperty() {
        return valid;
    }
}
