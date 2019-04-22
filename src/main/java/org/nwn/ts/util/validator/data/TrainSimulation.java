package org.nwn.ts.util.validator.data;

import org.nwn.ts.stats.SimulationDay;
import org.nwn.ts.stats.TrainType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainSimulation {
    private static TrainSimulation INSTANCE;

    public static TrainSimulation getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TrainSimulation();
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE = new TrainSimulation();
    }

    public enum WeatherType {
        FAIR, WINDY, RAINY, SNOWY;
    }

    private int sequence = 0;
    //Structure
    private List<TrainData> trains = new ArrayList<>();
    private List<StationData> stations = new ArrayList<>();
    private List<EdgeData> rails = new ArrayList<>();
    private List<HubData> hubs = new ArrayList<>();

    //Maintenance
    private Map<Integer, List<EdgeMaintenanceData>> edgeMaintenance = new HashMap<>();
    private Map<Integer, List<TrainMaintenanceData>> trainMaintenance = new HashMap<>();

    //Configuration

    private Map<TrainType, TrainConfigurationData> trainConfigurations = new HashMap<>();
    private int crewsPerHub = 5;
    private int fuelPerHub = 27000;
    private int daysToRun = 14;
    private WeatherType weatherType = WeatherType.FAIR;
    private double weatherSeverity = 0.0;


    //Routes
    //null key = repeatable
    private Map<Integer, List<FreightRouteData>> freightRoutes = new HashMap<>();
    private Map<Integer, List<PassengerRouteData>> passengerRoutes = new HashMap<>();

    public TrainSimulation() {
    }

    public List<SimulationDay> simulate(File outputDir) {


        return new ArrayList<>();
    }

    private native String _simulate();


    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getCrewsPerHub() {
        return crewsPerHub;
    }

    public void setCrewsPerHub(int crewsPerHub) {
        this.crewsPerHub = crewsPerHub;
    }

    public int getFuelPerHub() {
        return fuelPerHub;
    }

    public void setFuelPerHub(int fuelPerHub) {
        this.fuelPerHub = fuelPerHub;
    }

    public int getDaysToRun() {
        return daysToRun;
    }

    public void setDaysToRun(int daysToRun) {
        this.daysToRun = daysToRun;
    }

    public List<TrainData> getTrains() {
        return trains;
    }

    public List<StationData> getStations() {
        return stations;
    }

    public List<EdgeData> getRails() {
        return rails;
    }

    public List<HubData> getHubs() {
        return hubs;
    }

    public Map<Integer, List<EdgeMaintenanceData>> getEdgeMaintenance() {
        return edgeMaintenance;
    }

    public Map<Integer, List<TrainMaintenanceData>> getTrainMaintenance() {
        return trainMaintenance;
    }

    public Map<TrainType, TrainConfigurationData> getTrainConfigurations() {
        return trainConfigurations;
    }

    public Map<Integer, List<FreightRouteData>> getFreightRoutes() {
        return freightRoutes;
    }

    public Map<Integer, List<PassengerRouteData>> getPassengerRoutes() {
        return passengerRoutes;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(WeatherType weatherType) {
        this.weatherType = weatherType;
    }

    public double getWeatherSeverity() {
        return weatherSeverity;
    }

    public void setWeatherSeverity(double weatherSeverity) {
        this.weatherSeverity = weatherSeverity;
    }
}
