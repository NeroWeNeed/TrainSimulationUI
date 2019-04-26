package org.nwn.ts.validator;

import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.simulation.TrainSimulation;
import org.nwn.ts.simulation.TrainSimulationUpdater;
import org.nwn.ts.simulation.data.*;
import org.nwn.ts.simulation.data.TrainType;
import org.nwn.ts.util.IntRange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/*
 * TODO: Better Error Messages
 * */
public class StructureFileValidator implements Validator {
    private static StructureFileValidator INSTANCE = new StructureFileValidator();

    public static StructureFileValidator getInstance() {
        return INSTANCE;
    }
    //Regex

    private static final Pattern controlPattern = Pattern.compile("C\\s+(STATION|LOCOMOTIVE|HUB|EDGE)\\s+([0-9]{4})\\s*");
    private static final Pattern stationPattern = Pattern.compile("(.*?)\\s+(.)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+\\.[0-9]+)\\s*");
    private static final Pattern edgePattern = Pattern.compile("(.*?)\\s+(.*?)\\s+([-]?[0-9]+)\\s+([0-9]{2}:[0-9]{2})\\s+([0-9]{2}\\:[0-9]{2})\\s*");
    private static final Pattern trainPattern = Pattern.compile("(.*?)\\s+(.*?)\\s+(.)\\s*");
    private static final Pattern trailerPattern = Pattern.compile("T\\s+([0-9]+)\\s+([0-9]+)\\s*");

    @Override
    public void validate(File file, TrainSimulationUpdater updater) throws FileValidationException, IOException {
        HeaderData header;

        Map<String, StationData> stations = new HashMap<>();
        Set<EdgeData> edges = new HashSet<>();
        Map<String, TrainData> trains = new HashMap<>();


        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        //TODO: Compare to previous headers
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
                                if (stations.containsKey(line)) {
                                    throw new FileValidationException(String.format("Conflicting hubs/stations found with name: %s", line));
                                }
                                stations.put(line.trim(), null);
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

                                if (stations.containsKey(itemMatcher.group(1))) {
                                    throw new FileValidationException(String.format("Conflicting hubs/stations found with name: %s", itemMatcher.group(1)));
                                }
                                type = TrainType.parse(itemMatcher.group(2));
                                if (type == null)

                                    updater.getIssues().add(String.format("Can't determine if station %s is Passenger or Freight.", itemMatcher.group(1)));
                                else
                                    stations.put(itemMatcher.group(1),
                                            new StationData(itemMatcher.group(1),
                                                    type,
                                                    Integer.valueOf(itemMatcher.group(3)),
                                                    new IntRange(Integer.valueOf(itemMatcher.group(4)),Integer.valueOf(itemMatcher.group(5))),
                                                    new IntRange(Integer.valueOf(itemMatcher.group(6)),Integer.valueOf(itemMatcher.group(7))),
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
                                        ValidationUtils.timeToMinutes(edgeMatcher.group(4)),
                                        ValidationUtils.timeToMinutes(edgeMatcher.group(5))
                                );
                                if (!stations.containsKey(edge.getVertexA()))
                                    throw new FileValidationException(String.format("Unknown Station/Hub: %s", edge.getVertexA()));
                                if (!stations.containsKey(edge.getVertexB()))
                                    throw new FileValidationException(String.format("Unknown Station/Hub: %s", edge.getVertexB()));
                                if (edge.getDistance() <= 0)
                                    updater.getIssues().add(String.format("Distance between %s and %s is non-positive (%d), skipping", edge.getVertexA(), edge.getVertexB(), edge.getDistance()));
                                else
                                    edges.add(edge);
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
                                    updater.getIssues().add(String.format("Can't determine if train %s is Passenger or Freight.", trainMatcher.group(1)));
                                } else {
                                    train = new TrainData(trainMatcher.group(1), trainMatcher.group(2), type);
                                    if (!stations.containsKey(train.getHubName()))
                                        throw new FileValidationException(String.format("Unknown Hub: %s", train.getHubName()));
                                    if (stations.get(train.getHubName()) != null)
                                        throw new FileValidationException(String.format("Station provided instead of hub: %s", train.getHubName()));
                                    if (trains.containsKey(train.getName()))
                                        throw new FileValidationException(String.format("Duplicate Trains found: %s", train.getName()));
                                    trains.put(train.getName(), train);
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

        updater.getUpdates().addAll(stations.entrySet().stream().filter(x -> x.getValue() == null).map(x -> (Consumer<TrainSimulation>) simulation -> simulation.getHubs().add(new HubData(x.getKey()))).collect(Collectors.toSet()));
        updater.getUpdates().addAll(stations.values().stream().filter(Objects::nonNull).map(stationData -> (Consumer<TrainSimulation>) simulation -> simulation.getStations().add(stationData)).collect(Collectors.toSet()));
        updater.getUpdates().addAll(edges.stream().map(x -> (Consumer<TrainSimulation>) simulation -> simulation.getRails().add(x)).collect(Collectors.toList()));
        updater.getUpdates().addAll(trains.values().stream().map(x -> (Consumer<TrainSimulation>) simulation -> simulation.getTrains().add(x)).collect(Collectors.toSet()));



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




}
