package org.nwn.ts.util.validator;

import org.nwn.ts.util.validator.data.*;
import org.nwn.ts.util.validator.exception.FileValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaintenanceFileValidator implements Validator {
    private static final Pattern controlPattern1 = Pattern.compile("C\\s*DAY\\s*([0-9]+)\\s*");
    private static final Pattern controlPattern2 = Pattern.compile("C\\s*(LOCOMOTIVE|EDGE)\\s*([0-9]+)\\s*");
    private static final Pattern edgePattern = Pattern.compile("(.*?)\\s+(.*?)\\s+([0-9]+)\\s*");
    private static final Pattern trainPattern = Pattern.compile("(.*?)\\s+([0-9]+)\\s*");
    private static final Pattern trailerPattern = Pattern.compile("T\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s*");

    @Override
    public void validate(File file) throws Exception {
        HeaderData header;
        SimulationMaintenanceData simulationMaintenanceData = new SimulationMaintenanceData();
        List<String> issues = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        header = ValidationUtils.parseHeader(br);
        int lines = 0, content;
        int day = 0;
        int newDay;
        int expected, count;
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
                System.out.println(String.format("DAY %d",day));
            } else if (day > 0) {
                matcher = controlPattern2.matcher(line);
                if (matcher.matches()) {
                    expected = Integer.valueOf(matcher.group(2));
                    count = 0;
                    control = matcher.group(1);
                    switch (control) {
                        case "EDGE":
                            while (count < expected) {
                                line = br.readLine();
                                if (controlLineCheck(line, control, expected, count))
                                    continue;
                                matcher = edgePattern.matcher(line);
                                if (!matcher.matches())
                                    issues.add("Invalid Edge Found! Skipping");
                                else {
                                    MaintenanceData smd;
                                    if (simulationMaintenanceData.getDays().containsKey(day)) {
                                        smd = simulationMaintenanceData.getDays().get(day);
                                    } else {
                                        smd = new MaintenanceData();
                                        simulationMaintenanceData.getDays().put(day, smd);
                                    }
                                    smd.getEdges().add(new EdgeMaintenanceData(Integer.valueOf(matcher.group(3)), matcher.group(1), matcher.group(2)));
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
                                    issues.add("Invalid Train Found! Skipping");
                                else {
                                    MaintenanceData smd;
                                    if (simulationMaintenanceData.getDays().containsKey(day)) {
                                        smd = simulationMaintenanceData.getDays().get(day);
                                    } else {
                                        smd = new MaintenanceData();
                                        simulationMaintenanceData.getDays().put(day, smd);
                                    }
                                    smd.getTrains().add(new TrainMaintenanceData(matcher.group(1), Integer.valueOf(matcher.group(2))));
                                }
                                count += 1;
                            }
                            break;


                    }
                    lines += expected;


                }

            }


        }
        System.out.println(String.format("DAY %d",day));
        System.out.println(simulationMaintenanceData);
        br.close();
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
    public static void main(String[] args) {


        try {
            (new MaintenanceFileValidator()).validate(new File("sample2.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
