package org.nwn.ts;

import org.nwn.ts.simulation.TrainSimulation;
import org.nwn.ts.simulation.data.*;
import org.nwn.ts.stats.*;
import org.nwn.ts.util.IntRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Used for giving dummy data
public class DataProvider {
    public static void generateHubs(int count) {
        for (int i = 0; i < count; i++) {
            Model.getInstance().getSimulation().getHubs().add(new HubData("Hub_" + i));

        }

    }

    public static void generateStations(int count, int trainHigh, int trainLow) {
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < count; i++) {
            Model.getInstance().getSimulation().getStations().add(new StationData("Station_" + i, TrainType.random(),
                    random.nextInt(trainHigh - trainLow) + trainLow, new IntRange(50, 90), new IntRange(50, 90), 60
            ));

        }
    }

    public static void generateTrains(int count) {
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < count; i++) {
            Model.getInstance().getSimulation().getTrains().add(new TrainData("Train_" + i
                    , Model.getInstance().getSimulation().getHubs().get(random.nextInt(Model.getInstance().getSimulation().getHubs().size())),
                    TrainType.random()

            ));


        }
    }

    public static void generateRails(int count) {
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < count; i++) {

            Model.getInstance().getSimulation().getRails().add(new EdgeData(randomNode(random), randomNode(random), random.nextInt(300),
                    random.nextInt(287) * 5, random.nextInt(287) * 5));


        }
    }

    private static NodeData randomNode(Random random) {
        if (random.nextDouble() < 0.5) {
            return Model.getInstance().getSimulation().getStations().get(random.nextInt(Model.getInstance().getSimulation().getStations().size()));
        } else {
            return Model.getInstance().getSimulation().getHubs().get(random.nextInt(Model.getInstance().getSimulation().getHubs().size()));
        }
    }

    public static void generateSimulationDays(int count) {
        TrainSimulation simulation = Model.getInstance().getSimulation();
        List<SimulationDay> result = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < count; i++) {
            List<MetricHolder> metrics = new ArrayList<>();
            simulation.getTrains().forEach(x -> {

                if (x.getType() == TrainType.PASSENGER) {
                    metrics.add(new PassengerTrainMetricHolderImpl(x.getName(), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400)));
                } else if (x.getType() == TrainType.FREIGHT) {
                    metrics.add(new FreightTrainMetricHolderImpl(x.getName(), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400), random.nextInt(400)));
                }
            });

            simulation.getRails().forEach(x -> {
                metrics.add(new TrackMetricHolderImpl(x, random.nextInt(300)));
            });
            simulation.getStations().forEach(x -> {
                metrics.add(new StationMetricHolderImpl(x.getName(), random.nextInt(150), random.nextInt(200), random.nextInt(300), random.nextInt(500)));
            });
            simulation.getHubs().forEach(x -> {
                metrics.add(new HubMetricHolderImpl(x.getName(), random.nextInt(300), random.nextInt(300)));
            });
            result.add(new SimulationDay(i + 1, metrics));
        }
        Model.getInstance().getMetrics().addAll(result);
    }

}
