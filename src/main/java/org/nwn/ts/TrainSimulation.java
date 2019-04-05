package org.nwn.ts;

import org.nwn.ts.stats.MetricHolder;
import org.nwn.ts.util.Bridge;
import org.nwn.ts.util.Configuration;

import java.io.File;
import java.util.List;

public class TrainSimulation {
    public static void validateConfiguration(Configuration configuration) throws Exception {
        System.out.println(configuration);
    }

    public static List<MetricHolder> runSimulation(Configuration configuration) {
        String output = Bridge.getInstance().startSimulation(
                configuration.getLayoutFile().toString(),
                configuration.getMaintenanceFile().toString(),
                configuration.getConfigurationFile().toString(),
                configuration.getDailyRoutesFile().toString(),
                configuration.getRepeatableRoutesFile().toString(),
                configuration.getWeatherSeverity(),
                configuration.getWeatherType().ordinal(),
                configuration.isCollisionAvoidance(),
                configuration.getDuration(),
                configuration.getTransportCost()


        );
        File outputFile = new File(output);
        List<MetricHolder> metrics = Bridge.getInstance().parseResults(outputFile);


        Model.getInstance().setBaselineSet(true);
        return metrics;
    }
}
