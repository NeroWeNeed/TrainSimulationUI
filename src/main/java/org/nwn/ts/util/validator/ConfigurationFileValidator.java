package org.nwn.ts.util.validator;

import org.nwn.ts.exceptions.ConfigurationFileValidationException;
import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.stats.TrainType;
import org.nwn.ts.util.validator.data.HeaderData;
import org.nwn.ts.util.validator.data.TrainConfigurationData;
import org.nwn.ts.util.validator.data.TrainSimulationUpdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationFileValidator implements Validator {
    private static ConfigurationFileValidator INSTANCE = new ConfigurationFileValidator();

    public static ConfigurationFileValidator getInstance() {
        return INSTANCE;
    }

    private Pattern trainRegex = Pattern.compile("LOCOMOTIVE\\s+(P|F)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s*");
    private Pattern crewsRegex = Pattern.compile("CREWS\\s+([0-9]+)\\s*");
    private Pattern fuelRegex = Pattern.compile("FUEL\\s+([0-9]+)\\s*");
    private Pattern runRegex = Pattern.compile("RUN\\s+([0-9]+)\\s*");
    private Pattern trailerRegex = Pattern.compile("T\\s+([0-9]+)\\s*");


    @Override
    public void validate(File file, TrainSimulationUpdater updater) throws FileValidationException, IOException {

        Integer newRunDuration = null;
        Map<TrainType, TrainConfigurationData> newTrainConfigurations = new HashMap<>();
        Integer newTotalCrews = null;
        Integer newHubFuelCapacity = null;

        HeaderData header;

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;


        header = ValidationUtils.parseHeader(br);
        Matcher matcher;
        int lineCount = 0;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("LOCOMOTIVE")) {
                matcher = trainRegex.matcher(line);
                if (matcher.matches()) {
                    TrainType t = TrainType.parse(matcher.group(1));

                    TrainConfigurationData tc = new TrainConfigurationData();

                    tc.setFuelCapacity(Integer.valueOf(matcher.group(2)));
                    tc.setFuelCost(Integer.valueOf(matcher.group(3)));
                    tc.setSpeed(Integer.valueOf(matcher.group(4)));
                    tc.setCapacity(Integer.valueOf(matcher.group(5)));
                    newTrainConfigurations.put(t, tc);


                } else {
                    updater.getIssues().add("Invalid Format for line: " + line + ", skipping");
                }

            } else if (line.startsWith("CREWS")) {
                matcher = crewsRegex.matcher(line);
                if (matcher.matches()) {
                    newTotalCrews = Integer.valueOf(matcher.group(1));
                } else {
                    updater.getIssues().add("Invalid Format for line: " + line + ", skipping");
                }

            } else if (line.startsWith("FUEL")) {
                matcher = fuelRegex.matcher(line);
                if (matcher.matches()) {
                    newHubFuelCapacity = Integer.valueOf(matcher.group(1));
                } else {
                    updater.getIssues().add("Invalid Format for line: " + line + ", skipping");
                }

            } else if (line.startsWith("RUN")) {
                matcher = runRegex.matcher(line);
                if (matcher.matches()) {
                    newRunDuration = Integer.valueOf(matcher.group(1));

                } else {
                    updater.getIssues().add("Invalid Format for line: " + line + ", skipping");
                }
            } else if (line.startsWith("T")) {
                matcher = trailerRegex.matcher(line);
                if (matcher.matches()) {
                    int expectedLineCount = Integer.valueOf(matcher.group(1));
                    if (expectedLineCount != lineCount) {
                        throw new ConfigurationFileValidationException(String.format("Control Count Mismatch. Expected: %d, Found: %d", expectedLineCount, lineCount));

                    }
                    break;
                } else {
                    updater.getIssues().add("Invalid Format for line: " + line + ", skipping");
                }
            } else {
                throw new ConfigurationFileValidationException("Unable to parse line: " + line);
            }

            lineCount += 1;
        }

        if (newRunDuration != null) {
            int finalNewRunDuration = newRunDuration;
            updater.getUpdates().add(simulation -> {
                simulation.setDaysToRun(finalNewRunDuration);
            });
        }
        if (newTotalCrews != null) {
            int finalNewTotalCrews = newTotalCrews;
            updater.getUpdates().add(simulation -> {
                simulation.setCrewsPerHub(finalNewTotalCrews);
            });

        }
        if (newHubFuelCapacity != null) {
            int finalNewHubFuelCapacity = newHubFuelCapacity;
            updater.getUpdates().add(simulation -> {
                simulation.setFuelPerHub(finalNewHubFuelCapacity);
            });

        }
        newTrainConfigurations.forEach((key, value) -> {
            updater.getUpdates().add(simulation -> {
                simulation.getTrainConfigurations().put(key, value);
            });
        });


    }
}
