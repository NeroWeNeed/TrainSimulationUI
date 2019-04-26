package org.nwn.ts.simulation;

import org.nwn.ts.simulation.data.*;
import org.nwn.ts.util.BridgeLibraryLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

public class TrainBridgeWrapper {
    private TrainBridge bridge;
    private long pointer = -1;
    private static final String LIBRARY_LOCATION = "/lib/TrainSimulator";
    private static boolean loaded = false;

    public static void load() {
        if (!loaded) {
            File dir = new File(System.getProperty("user.dir"), "lib");
            dir.mkdirs();

            File libraryFile = new File(System.getProperty("user.dir") + LIBRARY_LOCATION);
            System.out.println(libraryFile);
            try {
                System.out.println(LIBRARY_LOCATION);
                Files.copy(BridgeLibraryLoader.class.getResourceAsStream(LIBRARY_LOCATION), libraryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.setProperty("library.path", dir.toString());
            loaded = true;
        }
        System.loadLibrary("TrainSimulatorx64");
    }

    public TrainBridgeWrapper(TrainBridge bridge) {
        this.bridge = bridge;
    }

    public TrainBridgeWrapper() {
        this(new TrainBridge());
    }

    public boolean isInitiated() {
        return pointer != -1;
    }

    public void create() {
        pointer = bridge.create_simulation();
    }

    public void addHub(HubData data) {
        bridge.add_hub(pointer, data.getName());
        bridge.set_hub_active(pointer, data.getName(), data.isAvailable());
        data.setUsed(true);
    }

    public void addStation(StationData data) {
        bridge.add_station(pointer, data.getName(), data.getType().getAssociatedChar(), data.getMaxTrains(), data.getRandomOnRange().getLow(), data.getRandomOnRange().getHigh(), data.getRandomOffRange().getLow(), data.getRandomOffRange().getHigh(), data.getTicketPrice());
        bridge.set_station_active(pointer, data.getName(), data.isAvailable());
        data.setUsed(true);
    }

    public void addRail(EdgeData data) {
        bridge.add_rail(pointer, data.getVertexA(), data.getVertexB(), data.getDistance(), data.getRestrictionStart(), data.getRestrictionEnd());
        bridge.set_rail_active(pointer, data.getVertexA(), data.getVertexB(), data.isAvailable());
        data.setUsed(true);
    }

    public void addTrain(TrainData data) {
        bridge.add_train(pointer, data.getName(), data.getHubName(), data.getType().getAssociatedChar());
        bridge.set_train_active(pointer, data.getName(), data.isAvailable());
        data.setUsed(true);
    }

    public void addEdgeMaintenanceInfo(int day, EdgeMaintenanceData data) {
        bridge.add_maintenance_edge(pointer, day, data.getVertexA(), data.getVertexB(), data.getDowntime());

    }

    public void addTrainMaintenanceInfo(int day, TrainMaintenanceData data) {
        bridge.add_maintenance_train(pointer, day, data.getName(), data.getDowntime());
    }

    public void configureTrain(TrainType type, TrainConfigurationData data) {
        bridge.configure_train(pointer, type.getAssociatedChar(), data.getFuelCapacity(), data.getFuelCost(), data.getSpeed(), data.getCapacity());
    }

    public void configure(int crewsPerHub, int fuel, int duration) {
        bridge.configure(pointer, crewsPerHub, fuel, duration);

    }

    public void configureWeather(int type, float severity) {
        bridge.configure_weather(pointer, type, severity);
    }

    public void addRepeatableRoute(PassengerRouteData data) {
        bridge.repeatable_route(pointer);
        data.getStops().forEach(stop -> bridge.add_repeatable_passenger_route_stop(pointer, stop.getStation(), stop.getArrivalTime()));

    }

    public void addRepeatableRoute(FreightRouteData data) {
        bridge.repeatable_route(pointer);
        bridge.add_repeatable_freight_route_stop(pointer, data.getStart(), data.getEnd(), data.getType().getAssociatedChar(), data.getStartTime(), data.getCapacity());


    }

    public void addDailyRoute(int day, FreightRouteData data) {
        bridge.daily_route(pointer, day);
        bridge.add_daily_freight_route_stop(pointer, data.getStart(), data.getEnd(), data.getStartTime(), data.getCapacity());
    }

    public void addDailyRoute(int day, PassengerRouteData data) {
        bridge.daily_route(pointer, day);
        data.getStops().forEach(stop -> bridge.add_daily_passenger_route_stop(pointer, stop.getStation(), stop.getArrivalTime()));
    }

    public File start(File outputDirectory) {
        TrainBridgeWrapper.load();
        return new File(bridge.start(pointer, outputDirectory.toString()));
    }

}
