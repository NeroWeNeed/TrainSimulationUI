package org.nwn.ts.util.validator;

import org.nwn.ts.exceptions.DailyRoutesFileValidationException;
import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.stats.TrainType;
import org.nwn.ts.util.validator.data.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyRoutesFileValidator implements Validator {
    private static final Pattern controlPattern = Pattern.compile("C\\s+(FREIGHT|PASSENGER)\\s+([0-9]{3})\\s*");
    private static final Pattern dayControlPattern = Pattern.compile("C\\s+DAY\\s+([0-9]{3})\\s*");
    private static final Pattern routeControlPattern = Pattern.compile("C\\s+ROUTE\\s+([0-9]{3})\\s*");
    private static final Pattern routeStopPattern = Pattern.compile("(.*?)\\s+([0-9]{2}\\:[0-9]{2})\\s*");
    private static final Pattern freightRoutePattern = Pattern.compile("(.*?)\\s+(.*?)\\s+([0-9]{2}\\:[0-9]{2})\\s+([0-9]+)\\s*");
    private static final Pattern trailerPattern = Pattern.compile("T\\s+([0-9]{6})\\s+([0-9]{6})\\s*");

    @Override
    public void validate(File file, TrainSimulationUpdater updater) throws FileValidationException,IOException {

        Map<Integer, List<FreightRouteData>> freightRoutes = new HashMap<>();
        Map<Integer, List<PassengerRouteData>> passengerRoutes = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;


        HeaderData headerData = ValidationUtils.parseHeader(br);
        int expectedLines, expectedControls, expectedControlItems = 0;
        int totalLines = 0, totalControls = 0;
        int currentDay = 0;
        Matcher matcher;
        while ((line = br.readLine()) != null) {
            matcher = dayControlPattern.matcher(line);
            if (matcher.matches()) {
                int day = Integer.valueOf(matcher.group(1));
                if (day > currentDay) {
                    currentDay = day;
                    totalLines += 1;
                    totalControls += 1;
                } else {
                    throw new DailyRoutesFileValidationException(String.format("Days out of order. Day expected: %d, Day found: %d", currentDay + 1, day));
                }
            } else if (line.startsWith("T")) {
                matcher = trailerPattern.matcher(line);
                if (matcher.matches()) {
                    expectedLines = Integer.valueOf(matcher.group(1));
                    expectedControls = Integer.valueOf(matcher.group(2));
                    if (totalControls != expectedControls)
                        throw new DailyRoutesFileValidationException(String.format("Control Count Mismatch. Expected: %d, Found: %d", expectedControls, totalControls));
                    if (totalLines != expectedLines)
                        throw new DailyRoutesFileValidationException(String.format("Total Line Count Mismatch. Expected: %d, Found: %d", expectedLines, totalLines));
                    break;

                } else {
                    throw new DailyRoutesFileValidationException("Unable to parse daily routes file");
                }

            } else if (currentDay > 0) {
                matcher = controlPattern.matcher(line);
                if (matcher.matches()) {
                    totalLines += 1;
                    totalControls += 1;
                    TrainType type = TrainType.parseFull(matcher.group(1));
                    expectedControlItems = Integer.parseInt(matcher.group(2));
                    if (type == null) {
                        throw new DailyRoutesFileValidationException(String.format("Unable to determine route type '%s'", matcher.group(1)));
                    }

                    switch (type) {

                        case FREIGHT:
                            if (!freightRoutes.containsKey(currentDay))
                                freightRoutes.put(currentDay, new ArrayList<>());

                            totalLines += processFreightRoute(br, expectedControlItems, freightRoutes.get(currentDay), currentDay, updater);

                            break;
                        case PASSENGER:

                            if (!passengerRoutes.containsKey(currentDay))
                                passengerRoutes.put(currentDay, new ArrayList<>());
                            totalLines += processPassengerRoute(br, expectedControlItems, passengerRoutes.get(currentDay), currentDay, updater);
                            totalControls += expectedControlItems;
                            break;
                    }

                } else {
                    throw new DailyRoutesFileValidationException("Unidentified control found");
                }

            } else {
                throw new DailyRoutesFileValidationException("Unidentified control found");
            }


        }
        updater.getUpdates().add(simulation -> {
            freightRoutes.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    if (!simulation.getFreightRoutes().containsKey(key))
                        simulation.getFreightRoutes().put(key, new ArrayList<>());
                    simulation.getFreightRoutes().get(key).addAll(value);
                }
            });
            passengerRoutes.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    if (!simulation.getPassengerRoutes().containsKey(key))
                        simulation.getPassengerRoutes().put(key, new ArrayList<>());
                    simulation.getPassengerRoutes().get(key).addAll(value);
                }
            });
        });
    }

    private int processFreightRoute(BufferedReader br, int expected, List<FreightRouteData> freightRouteData, int currentDay, TrainSimulationUpdater updater) throws IOException, FileValidationException, DailyRoutesFileValidationException {
        int total = 0;
        String line;
        Matcher matcher;
        while (total < expected) {
            line = br.readLine();
            if (ValidationUtils.lineCheck(line, "FREIGHT ROUTE", expected, total))
                continue;
            matcher = freightRoutePattern.matcher(line);
            if (matcher.matches()) {
                freightRouteData.add(new FreightRouteData(matcher.group(1), matcher.group(2), RouteType.DAILY, ValidationUtils.timeToMinutes(matcher.group(3)), Integer.valueOf(matcher.group(4))));
            } else if (line.startsWith("C ") && (line.matches(controlPattern.pattern()) || line.matches(routeControlPattern.pattern()) || line.matches(dayControlPattern.pattern()))) {
                throw new DailyRoutesFileValidationException(String.format("Freight Route Control Structure ended prematurely on day %d, unable to parse file.", currentDay));
            } else {
                updater.getIssues().add(String.format("Unable to parse stop %d for freight route on day %d.", total, currentDay));
            }
            total += 1;
        }
        return expected;
    }

    private int processPassengerRoute(BufferedReader br, int expected, List<PassengerRouteData> passengerRouteData, int currentDay, TrainSimulationUpdater updater) throws IOException, FileValidationException, DailyRoutesFileValidationException {
        int total = 0;
        String line;
        Matcher matcher;
        int totalLines = 0;
        while (total < expected) {
            line = br.readLine();
            if (ValidationUtils.lineCheck(line, "Passenger", expected, total))
                continue;
            matcher = routeControlPattern.matcher(line);
            if (matcher.matches()) {
                int t = Integer.valueOf(matcher.group(1));
                PassengerRouteData route = new PassengerRouteData(RouteType.DAILY);
                totalLines += processRoute(br, t, route, total, currentDay, updater);
                passengerRouteData.add(route);
            } else if (line.startsWith("C ") && (line.matches(controlPattern.pattern()) || line.matches(routeControlPattern.pattern()) || line.matches(dayControlPattern.pattern()))) {
                throw new DailyRoutesFileValidationException(String.format("Passenger Control Structure ended prematurely on day %d, unable to parse file.", currentDay));
            } else {
                updater.getIssues().add(String.format("Unable to parse passenger route %d on day %d.", total, currentDay));
            }
            totalLines += 1;
            total += 1;

        }
        return totalLines;
    }

    private int processRoute(BufferedReader br, int expectedStops, PassengerRouteData route, int routeNum, int currentDay, TrainSimulationUpdater updater) throws IOException, FileValidationException, DailyRoutesFileValidationException {
        int totalStops = 0;
        Matcher matcher;
        String line;

        while (totalStops < expectedStops) {
            line = br.readLine();
            matcher = routeStopPattern.matcher(line);
            if (ValidationUtils.lineCheck(line, "Passenger Route", expectedStops, totalStops))
                continue;
            if (matcher.matches())
                route.getStops().add(new PassengerRouteData.Stop(matcher.group(1), ValidationUtils.timeToMinutes(matcher.group(2))));
            else if (line.startsWith("C ") && (line.matches(controlPattern.pattern()) || line.matches(routeControlPattern.pattern()) || line.matches(dayControlPattern.pattern()))) {
                throw new DailyRoutesFileValidationException(String.format("Passenger Route Control Structure %d ended prematurely on day %d, unable to parse file.", routeNum, currentDay));
            } else {
                updater.getIssues().add("Can't Process Line, Skipping");
            }
            totalStops += 1;
        }
        return totalStops;

    }

    public static void main(String[] args) {
        try {
            (new DailyRoutesFileValidator()).validate(new File("testFiles/dailyRoutes.txt"), new TrainSimulationUpdater());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
