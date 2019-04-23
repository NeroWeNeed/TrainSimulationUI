package org.nwn.ts.validator;

import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.exceptions.MaintenanceFileValidationException;
import org.nwn.ts.simulation.TrainSimulationUpdater;
import org.nwn.ts.simulation.data.EdgeMaintenanceData;
import org.nwn.ts.simulation.data.HeaderData;
import org.nwn.ts.simulation.data.MaintenanceData;
import org.nwn.ts.simulation.data.TrainMaintenanceData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaintenanceFileValidator implements Validator {
    private static final Pattern controlPattern1 = Pattern.compile("C\\s*DAY\\s*([0-9]+)\\s*");
    private static final Pattern controlPattern2 = Pattern.compile("C\\s*(LOCOMOTIVE|EDGE)\\s*([0-9]+)\\s*");
    private static final Pattern edgePattern = Pattern.compile("(.*?)\\s+(.*?)\\s+([0-9]+)\\s*");
    private static final Pattern trainPattern = Pattern.compile("(.*?)\\s+([0-9]+)\\s*");
    private static final Pattern trailerPattern = Pattern.compile("T\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s*");

    @Override
    public void validate(File file, TrainSimulationUpdater updater) throws FileValidationException, IOException {
        HeaderData header;

        Map<Integer, MaintenanceData> maintenance = new HashMap<>();


        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        header = ValidationUtils.parseHeader(br);
        int lines = 0, content;
        int day = 0;
        int newDay;
        int expected, count;
        int totalLines = 0, totalControls = 0, totalDays = 0;

        String control;

        Matcher matcher;
        while ((line = br.readLine()) != null) {
            System.out.println("LOOP A");
            if (line.isEmpty())
                continue;
            matcher = controlPattern1.matcher(line);
            System.out.println(matcher.matches());
            if (matcher.matches()) {
                newDay = Integer.valueOf(matcher.group(1));
                if (newDay <= day)
                    throw new FileValidationException("Maintenance File Days should be ordered and start at Day 1");
                day = newDay;
                totalDays += 1;
                totalLines += 1;
                totalControls += 1;
                System.out.println(String.format("DAY %d", day));
            } else if (line.startsWith("T")) {
                matcher = trailerPattern.matcher(line);
                if (matcher.matches()) {
                    int expectedLines = Integer.valueOf(matcher.group(1));
                    int expectedControls = Integer.valueOf(matcher.group(2));
                    int expectedDays = Integer.valueOf(matcher.group(3));
                    if (expectedLines != totalLines || expectedControls != totalControls || expectedDays != totalDays)
                        throw new MaintenanceFileValidationException("Line Counts/Controls Don't add up");
                    else
                        break;
                } else {
                    throw new MaintenanceFileValidationException("Unable to Parse line");
                }

            } else if (day > 0) {
                matcher = controlPattern2.matcher(line);
                if (matcher.matches()) {
                    expected = Integer.valueOf(matcher.group(2));
                    count = 0;
                    control = matcher.group(1);
                    totalLines += 1;
                    totalControls += 1;
                    switch (control) {
                        case "EDGE":
                            while (count < expected) {
                                line = br.readLine();
                                if (controlLineCheck(line, control, expected, count))
                                    continue;
                                matcher = edgePattern.matcher(line);
                                if (!matcher.matches())
                                    updater.getIssues().add("Invalid Edge Found! Skipping");
                                else {
                                    if (!maintenance.containsKey(day)) {
                                        maintenance.put(day, new MaintenanceData());
                                    }

                                    maintenance.get(day).getEdges().add(new EdgeMaintenanceData(Integer.valueOf(matcher.group(3)), matcher.group(1), matcher.group(2)));
                                }
                                count += 1;
                            }


                            break;
                        case "LOCOMOTIVE":
                            while (count < expected) {
                                line = br.readLine();
                                if (controlLineCheck(line, control, expected, count))
                                    continue;
                                matcher = trainPattern.matcher(line);
                                if (!matcher.matches())
                                    updater.getIssues().add("Invalid Train Found! Skipping");
                                else {
                                    if (!maintenance.containsKey(day)) {
                                        maintenance.put(day, new MaintenanceData());
                                    }
                                    maintenance.get(day).getTrains().add(new TrainMaintenanceData(matcher.group(1), Integer.valueOf(matcher.group(2))));
                                }
                                count += 1;
                            }
                            break;


                    }
                    totalLines += count;


                }

            }


        }
        br.close();

        maintenance.forEach((key, value) -> {
            updater.getUpdates().add(simulation -> {
                        if (value != null && value.getEdges() != null && !value.getEdges().isEmpty()) {
                            if (!simulation.getEdgeMaintenance().containsKey(key)) {
                                simulation.getEdgeMaintenance().put(key, new ArrayList<>());
                            }
                            simulation.getEdgeMaintenance().get(key).addAll(value.getEdges());

                        }
                        if (value != null && value.getTrains() != null && !value.getTrains().isEmpty()) {
                            if (!simulation.getTrainMaintenance().containsKey(key)) {
                                simulation.getTrainMaintenance().put(key, new ArrayList<>());
                            }
                            simulation.getTrainMaintenance().get(key).addAll(value.getTrains());

                        }

                    }
            );
        });


    }

    private boolean controlLineCheck(String line, String control, int expectedCount, int count) throws FileValidationException {
        if (line == null)
            throw new FileValidationException(String.format("Expected %s count to be %d, found %d", control, expectedCount, count));
        if (line.isEmpty())
            return true;
        if (line.matches(controlPattern1.pattern()) || line.matches(controlPattern2.pattern()))
            throw new FileValidationException(String.format("Expected %s count to be %d, found %d", control, expectedCount, count));
        return false;
    }

}
