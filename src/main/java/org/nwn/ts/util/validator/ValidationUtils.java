package org.nwn.ts.util.validator;

import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.exceptions.InvalidHeaderException;
import org.nwn.ts.util.validator.data.HeaderData;
import org.nwn.ts.util.validator.data.PassengerRouteData;
import org.nwn.ts.util.validator.data.RouteType;
import org.nwn.ts.util.validator.data.TrainSimulationUpdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern headerPattern = Pattern.compile("H([0-9]{4})\\s*([0-9]{2}\\/[0-9]{2}\\/[0-9]{4})\\s*");
    private static final Pattern routeStopPattern = Pattern.compile("(.*?)\\s+([0-9]{2}\\:[0-9]{2})\\s*");

    public static HeaderData parseHeader(BufferedReader br) throws IOException, InvalidHeaderException {
        String line;
        do {
            line = br.readLine();
            if (line == null)
                throw new InvalidHeaderException("Expected File Header, Found EOF.");
        } while (line.isEmpty());
        Matcher matcher = headerPattern.matcher(line);
        if (!matcher.matches())
            throw new InvalidHeaderException(String.format("Expected File header, Found: %s", line));

        return new HeaderData(Integer.valueOf(matcher.group(1)), matcher.group(2));
    }

    public static int timeToMinutes(String value) {
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

    public static PassengerRouteData processRoute(BufferedReader br, RouteType type, int expectedStops, TrainSimulationUpdater updater) throws IOException, FileValidationException {
        int totalStops = 0;
        Matcher matcher;
        String line;
        PassengerRouteData route = new PassengerRouteData(type);
        while (totalStops < expectedStops) {
            line = br.readLine();
            matcher = routeStopPattern.matcher(line);
            if (lineCheck(line, "Route", expectedStops, totalStops))
                continue;
            if (matcher.matches())
                route.getStops().add(new PassengerRouteData.Stop(matcher.group(1), ValidationUtils.timeToMinutes(matcher.group(2))));
            else {
                updater.getIssues().add("Can't Process Line, Skipping");
            }
            totalStops += 1;
        }
        return route;

    }

    public static boolean lineCheck(String line, String control, int expectedCount, int count) throws FileValidationException {
        if (line == null)
            throw new FileValidationException(String.format("Expected %s Control Structore have have %d elements, found %d", control, expectedCount, count));
        return line.isEmpty();
    }


}
