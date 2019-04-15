package org.nwn.ts.util.validator;

import org.nwn.ts.Model;
import org.nwn.ts.controller.ConfigController;
import org.nwn.ts.stats.TrainType;
import org.nwn.ts.util.Configuration;
import org.nwn.ts.util.TrainConfiguration;
import org.nwn.ts.util.validator.data.HeaderData;
import org.nwn.ts.util.validator.exception.InvalidLineException;
import org.nwn.ts.util.validator.exception.LineCountMismatchException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void validate(File file) throws Exception {
        Configuration configuration = Model.getInstance().getConfiguration();
        int newRunDuration = configuration.getRunDuration();
        Map<TrainType, TrainConfiguration> newTrainConfigurations = new HashMap<>();
        int newTotalCrews = configuration.getTotalCrews();
        int newHubFuelCapacity = configuration.getHubFuelCapacity();

        HeaderData header;
        List<String> issues = new ArrayList<>();
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

                    TrainConfiguration tc = new TrainConfiguration();

                    tc.setFuelCapacity(Integer.valueOf(matcher.group(2)));
                    tc.setFuelCost(Integer.valueOf(matcher.group(3)));
                    tc.setSpeed(Integer.valueOf(matcher.group(4)));
                    tc.setCapacity(Integer.valueOf(matcher.group(5)));
                    newTrainConfigurations.put(t, tc);


                } else {
                    issues.add("Invalid Format for line: " + line + ", skipping");
                }

            } else if (line.startsWith("CREWS")) {
                matcher = crewsRegex.matcher(line);
                if (matcher.matches()) {
                    newTotalCrews = Integer.valueOf(matcher.group(1));
                } else {
                    issues.add("Invalid Format for line: " + line + ", skipping");
                }

            } else if (line.startsWith("FUEL")) {
                matcher = fuelRegex.matcher(line);
                if (matcher.matches()) {
                    newHubFuelCapacity = Integer.valueOf(matcher.group(1));
                } else {
                    issues.add("Invalid Format for line: " + line + ", skipping");
                }

            } else if (line.startsWith("RUN")) {
                matcher = runRegex.matcher(line);
                if (matcher.matches()) {
                    newRunDuration = Integer.valueOf(matcher.group(1));

                } else {
                    issues.add("Invalid Format for line: " + line + ", skipping");
                }
            } else if (line.startsWith("T")) {
                matcher = trailerRegex.matcher(line);
                if (matcher.matches()) {
                    int expectedLineCount = Integer.valueOf(matcher.group(1));
                    if (expectedLineCount != lineCount) {
                        throw new LineCountMismatchException(String.format("Expected %d lines, found %d", expectedLineCount, lineCount));
                    }
                    break;
                } else {
                    issues.add("Invalid Format for line: " + line + ", skipping");
                }
            } else {
                throw new InvalidLineException("Unknown line found: " + line);
            }

            lineCount += 1;
        }
        //Apply
        configuration.setRunDuration(newRunDuration);
        configuration.setHubFuelCapacity(newHubFuelCapacity);
        configuration.setTotalCrews(newTotalCrews);
        configuration.getTrainConfigurations().putAll(newTrainConfigurations);

    }
}
