package org.nwn.ts.validator;

import org.nwn.ts.stats.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReturnFileParser {

    private static final Pattern dayPattern = Pattern.compile("DAY ([0-9]+)\\s*");
    private static final Pattern passengerTrainPattern = Pattern.compile("TRAIN_P\\s+(.*?)\\s+([0-9]+)\\s+([0-9]+\\.[0-9]+)\\s+([0-9]+)\\s+([0-9]+\\.[0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s*");
    private static final Pattern freightTrainPattern = Pattern.compile("TRAIN_F\\s+(.*?)\\s+([0-9]+)\\s+([0-9]+\\.[0-9]+)\\s+([0-9]+)\\s+([0-9]+\\.[0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s*");
    private static final Pattern stationPattern = Pattern.compile("STATION\\s+(.*?)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s*");
    private static final Pattern trackPattern = Pattern.compile("TRACK\\s+(.*?)\\s+([0-9]+)\\s*");
    private static final Pattern hubPattern = Pattern.compile("HUB\\s+(.*?)\\s+([0-9]+)\\s+([0-9]+)\\s*");

    public List<SimulationDay> parse(File input) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(input));
        SimulationDay day = null;
        List<SimulationDay> days = new ArrayList<>();
        String line;
        Matcher matcher;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty())
                continue;
            if (line.matches(dayPattern.pattern())) {
                if (day != null) {
                    days.add(day);
                    matcher = dayPattern.matcher(line);
                    day = new SimulationDay(Integer.parseInt(matcher.group(1)));

                }
            } else if (day == null) {
                continue;

            } else if (line.matches(freightTrainPattern.pattern())) {
                matcher = freightTrainPattern.matcher(line);
                day.getMetrics().add(new FreightTrainMetricHolderImpl(matcher.group(1),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Double.parseDouble(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)),
                        Double.parseDouble(matcher.group(6)),
                        Integer.parseInt(matcher.group(7)),
                        Integer.parseInt(matcher.group(8))));
            } else if (line.matches(passengerTrainPattern.pattern())) {
                matcher = passengerTrainPattern.matcher(line);
                day.getMetrics().add(new PassengerTrainMetricHolderImpl(matcher.group(1),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Double.parseDouble(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)),
                        Double.parseDouble(matcher.group(6)),
                        Integer.parseInt(matcher.group(7)),
                        Integer.parseInt(matcher.group(8))));
            } else if (line.matches(stationPattern.pattern())) {
                matcher = stationPattern.matcher(line);
                day.getMetrics().add(new StationMetricHolderImpl(matcher.group(1),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(5))));
            } else if (line.matches(trackPattern.pattern())) {
                matcher = trackPattern.matcher(line);
                day.getMetrics().add(new TrackMetricHolderImpl(matcher.group(1), Integer.parseInt(matcher.group(2))));
            } else if (line.matches(hubPattern.pattern())) {
                matcher = hubPattern.matcher(line);
                day.getMetrics().add(new HubMetricHolderImpl(matcher.group(1), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))));
            }
        }
        return days;
    }
}
