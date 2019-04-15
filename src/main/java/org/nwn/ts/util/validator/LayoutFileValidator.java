package org.nwn.ts.util.validator;

import org.nwn.ts.stats.TrainType;
import org.nwn.ts.util.validator.data.*;
import org.nwn.ts.util.validator.exception.FileValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * TODO: Better Error Messages
 * */
public class LayoutFileValidator implements Validator {
    private static LayoutFileValidator INSTANCE = new LayoutFileValidator();

    public static LayoutFileValidator getInstance() {
        return INSTANCE;
    }
    //Regex

    private static final Pattern controlPattern = Pattern.compile("C\\s*(STATION|LOCOMOTIVE|HUB|EDGE)\\s*([0-9]{4})\\s*");
    private static final Pattern stationPattern = Pattern.compile("(.*?)\\s+(.)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+\\.[0-9]+)\\s*");
    private static final Pattern edgePattern = Pattern.compile("(.*?)\\s+(.*?)\\s+([-]?[0-9]+)\\s+([0-9]{2}:[0-9]{2})\\s+([0-9]{2}\\:[0-9]{2})\\s*");
    private static final Pattern trainPattern = Pattern.compile("(.*?)\\s+(.*?)\\s+(.)\\s*");
    private static final Pattern trailerPattern = Pattern.compile("T\\s+([0-9]+)\\s+([0-9]+)\\s*");

    @Override
    public void validate(File file) throws Exception {
        HeaderData header;
        ControlData control = new ControlData();
        List<String> issues = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;


        header = ValidationUtils.parseHeader(br);


        int lineCount = 0;
        int contentLineCount = 0;
        String controlName;
        int count;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("C")) {
                Matcher matcher = controlPattern.matcher(line);
                if (matcher.matches()) {
                    controlName = matcher.group(1).toUpperCase();
                    count = Integer.valueOf(matcher.group(2));
                    int current = 0;
                    TrainType type;
                    switch (controlName) {
                        case "HUB":
                            while (current < count) {
                                line = br.readLine();
                                if (controlLineCheck(line, controlName, count, current))
                                    continue;
                                if (control.getStations().containsKey(line)) {
                                    throw new FileValidationException(String.format("Conflicting hubs/stations found with name: %s", line));
                                }
                                control.getStations().put(line, null);
                                current += 1;
                            }
                            break;
                        case "STATION":
                            Matcher itemMatcher;

                            while (current < count) {
                                line = br.readLine();
                                if (controlLineCheck(line, controlName, count, current))
                                    continue;
                                itemMatcher = stationPattern.matcher(line);
                                if (!itemMatcher.matches())
                                    throw new FileValidationException("Invalid Station data found!");

                                if (control.getStations().containsKey(itemMatcher.group(1))) {
                                    throw new FileValidationException(String.format("Conflicting hubs/stations found with name: %s", itemMatcher.group(1)));
                                }
                                type = TrainType.parse(itemMatcher.group(2));
                                if (type == null)
                                    issues.add(String.format("Can't determine if station %s is Passenger or Freight.", itemMatcher.group(1)));
                                else
                                    control.getStations().put(itemMatcher.group(1),
                                            new StationData(itemMatcher.group(1),
                                                    type,
                                                    Integer.valueOf(itemMatcher.group(3)),
                                                    Integer.valueOf(itemMatcher.group(4)),
                                                    Integer.valueOf(itemMatcher.group(5)),
                                                    Double.valueOf(itemMatcher.group(6))
                                            ));

                                current += 1;
                            }
                        case "EDGE":
                            Matcher edgeMatcher;
                            EdgeData edge;
                            while (current < count) {
                                line = br.readLine();
                                if (controlLineCheck(line, controlName, count, current))
                                    continue;
                                edgeMatcher = edgePattern.matcher(line);
                                if (!edgeMatcher.matches())
                                    throw new FileValidationException("Invalid Station data found!");
                                edge = new EdgeData(edgeMatcher.group(1),
                                        edgeMatcher.group(2),
                                        Integer.valueOf(edgeMatcher.group(3)),
                                        timeToMinutes(edgeMatcher.group(4)),
                                        timeToMinutes(edgeMatcher.group(5))
                                );
                                if (!control.getStations().containsKey(edge.getVertexA()))
                                    throw new FileValidationException(String.format("Unknown Station/Hub: %s", edge.getVertexA()));
                                if (!control.getStations().containsKey(edge.getVertexB()))
                                    throw new FileValidationException(String.format("Unknown Station/Hub: %s", edge.getVertexB()));
                                if (edge.getDistance() <= 0)
                                    issues.add(String.format("Distance between %s and %s is non-positive (%d), skipping", edge.getVertexA(), edge.getVertexB(), edge.getDistance()));
                                else
                                    control.getEdges().add(edge);
                                current += 1;
                            }
                            break;
                        case "LOCOMOTIVE":
                            Matcher trainMatcher;
                            TrainData train;
                            while (current < count) {
                                line = br.readLine();
                                if (controlLineCheck(line, controlName, count, current))
                                    continue;
                                trainMatcher = trainPattern.matcher(line);

                                if (!trainMatcher.matches()) {
                                    throw new FileValidationException("Invalid Train data found!");
                                }
                                type = TrainType.parse(trainMatcher.group(3));
                                if (type == null) {
                                    issues.add(String.format("Can't determine if train %s is Passenger or Freight.", trainMatcher.group(1)));
                                } else {
                                    train = new TrainData(trainMatcher.group(1), trainMatcher.group(2), type);
                                    if (!control.getStations().containsKey(train.getHubName()))
                                        throw new FileValidationException(String.format("Unknown Hub: %s", train.getHubName()));
                                    if (control.getStations().get(train.getHubName()) != null)
                                        throw new FileValidationException(String.format("Station provided instead of hub: %s", train.getHubName()));
                                    if (control.getTrains().containsKey(train.getName()))
                                        throw new FileValidationException(String.format("Duplicate Trains found: %s", train.getName()));
                                    control.getTrains().put(train.getName(), train);
                                }
                                current += 1;

                            }
                            break;
                    }
                    contentLineCount += current;
                    lineCount += 1 + current;

                }

            } else if (line.startsWith("T")) {
                Matcher matcher = trailerPattern.matcher(line);
                if (matcher.matches()) {
                    int expectedLines = Integer.valueOf(matcher.group(1));
                    int expectedContentLines = Integer.valueOf(matcher.group(2));
                    System.out.println(String.format("EXPECTED: %d,EXPECTED CONTENT: %d", expectedLines, expectedContentLines));
                    if (lineCount != expectedLines) {
                        throw new FileValidationException(String.format("Parse Error. Expected %d lines, found %d lines", expectedLines, lineCount));
                    } else if (expectedContentLines != contentLineCount) {
                        throw new FileValidationException(String.format("Parse Error. Expected content %d lines, found content %d lines", expectedContentLines, contentLineCount));
                    }
                    break;


                } else {
                    throw new FileValidationException("Invalid Trailer Block");
                }
            } else {
                throw new FileValidationException("Error in validating file");
            }

        }
        br.close();


    }

    private boolean controlLineCheck(String line, String control, int expectedCount, int count) throws FileValidationException {
        if (line == null)
            throw new FileValidationException(String.format("Expected %s count to be %d, found %d", control, expectedCount, count));
        if (line.isEmpty())
            return true;
        if (line.matches(controlPattern.pattern()))
            throw new FileValidationException(String.format("Expected %s count to be %d, found %d", control, expectedCount, count));
        return false;
    }

    private int timeToMinutes(String value) {
        if (value.length() != 5)
            throw new IllegalArgumentException("Not Valid Time Input (needs to be xx:xx)");
        int colonIndex = value.indexOf(':');
        if (colonIndex == -1 || colonIndex + 1 >= value.length())
            throw new IllegalArgumentException("Not Valid Time Input (needs to be xx:xx)");
        int hours = Integer.valueOf(value.substring(0, colonIndex));
        int minutes = Integer.valueOf(value.substring(colonIndex + 1));

        if (hours > 23 || hours < 0 || minutes > 59 || minutes < 0)
            throw new IllegalArgumentException("Time values out of range");
        return (hours * 60) + minutes;
    }


}
