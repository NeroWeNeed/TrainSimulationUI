package org.nwn.ts.simulation;

import org.nwn.ts.simulation.data.*;
import org.nwn.ts.stats.SimulationDay;
import org.nwn.ts.simulation.data.TrainType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private TrainBridgeWrapper bridge = new TrainBridgeWrapper();
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
        if (!bridge.isInitiated()) {
            bridge.create();
        }

        hubs.stream().filter(x -> !x.isUsed()).forEach(x -> bridge.addHub(x));
        stations.stream().filter(x -> !x.isUsed()).forEach(x -> bridge.addStation(x));
        rails.stream().filter(x -> !x.isUsed()).forEach(x -> bridge.addRail(x));
        trains.stream().filter(x -> !x.isUsed()).forEach(x -> bridge.addTrain(x));
        edgeMaintenance.forEach((k, v) -> v.forEach(m -> bridge.addEdgeMaintenanceInfo(k, m)));
        trainMaintenance.forEach((k, v) -> v.forEach(m -> bridge.addTrainMaintenanceInfo(k, m)));
        freightRoutes.forEach((k, v) -> {
            if (k == null) {
                v.forEach(route -> {
                    bridge.addRepeatableRoute(route);
                });
            } else {
                v.forEach(route -> {
                    bridge.addDailyRoute(k, route);
                });
            }

        });
        passengerRoutes.forEach((k, v) -> {
            if (k == null) {
                v.forEach(route -> {
                    bridge.addRepeatableRoute(route);
                });
            } else {
                v.forEach(route -> {
                    bridge.addDailyRoute(k, route);
                });
            }

        });
        bridge.start(outputDir);

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

    public List<TrainData> getActiveTrains() {
        return trains.stream().filter(TrainData::isAvailable).collect(Collectors.toList());
    }

    public List<StationData> getStations() {
        return stations;
    }

    public List<StationData> getActiveStations() {
        return stations.stream().filter(StationData::isAvailable).collect(Collectors.toList());
    }

    public List<EdgeData> getRails() {
        return rails;
    }

    public List<EdgeData> getActiveRails() {
        return rails.stream().filter(EdgeData::isAvailable).collect(Collectors.toList());
    }

    public List<HubData> getHubs() {
        return hubs;
    }

    public List<HubData> getActiveHubs() {
        return hubs.stream().filter(HubData::isAvailable).collect(Collectors.toList());
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
