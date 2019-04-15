package org.nwn.ts;

import org.nwn.ts.exceptions.ConfigurationFileValidationException;
import org.nwn.ts.exceptions.LayoutFileValidationException;
import org.nwn.ts.exceptions.ValidationException;
import org.nwn.ts.stats.SimulationDay;
import org.nwn.ts.util.Bridge;
import org.nwn.ts.util.Configuration;
import org.nwn.ts.util.validator.ConfigurationFileValidator;
import org.nwn.ts.util.validator.LayoutFileValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TrainSimulation {
    public static void validateConfiguration(Configuration configuration) throws Exception {
        System.out.println(configuration);
    }

    public static List<SimulationDay> runSimulation(Configuration configuration) throws ValidationException {
        //Validation
        try {
            LayoutFileValidator.getInstance().validate(configuration.getLayoutFile());
        } catch (LayoutFileValidationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new LayoutFileValidationException(exception);
        }

        try {
            ConfigurationFileValidator.getInstance().validate(configuration.getConfigurationFile());
        } catch (ConfigurationFileValidationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ConfigurationFileValidationException(exception);
        }


        //Native Call, Commented out for now
/*        String output = Bridge.getInstance().startSimulation(
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
        File outputFile = new File(output);*/

        //TODO: Replace after native bindings are done
        File outputFile = new File("output");

        List<SimulationDay> metrics = parseResults(outputFile);


        Model.getInstance().setBaselineSet(true);
        return metrics;
    }

    public static List<SimulationDay> parseResults(File resultsFile) {
        List<SimulationDay> r = new ArrayList<SimulationDay>();
        for (int i = 1; i < 100; i++) {
            r.add(new SimulationDay(i, Collections.emptyList()));
        }
        //TODO: Parse Output File
        return r;
    }
}
