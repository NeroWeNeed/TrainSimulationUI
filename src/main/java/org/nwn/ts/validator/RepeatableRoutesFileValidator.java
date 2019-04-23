package org.nwn.ts.validator;

import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.exceptions.RepeatableRouteFileValidationException;
import org.nwn.ts.exceptions.RouteTypeParseException;
import org.nwn.ts.simulation.TrainSimulationUpdater;
import org.nwn.ts.simulation.data.FreightRouteData;
import org.nwn.ts.simulation.data.HeaderData;
import org.nwn.ts.simulation.data.PassengerRouteData;
import org.nwn.ts.simulation.data.RouteType;
import org.nwn.ts.simulation.data.TrainType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepeatableRoutesFileValidator implements Validator {
    private static final Pattern controlPattern = Pattern.compile("C\\s+(FREIGHT|PASSENGER)\\s+([0-9]{3})\\s*");
    private static final Pattern routeControlPattern = Pattern.compile("C\\s+(ROUTE)\\s+([0-9]{3})\\s+(.)\\s*");

    private static final Pattern freightRoutePattern = Pattern.compile("(.*?)\\s+(.*?)\\s+(.)\\s+([0-9]{2}\\:[0-9]{2})\\s+([0-9]+)\\s*");
    private static final Pattern trailerPattern = Pattern.compile("T\\s+([0-9]{6})\\s+([0-9]{6})\\s*");

    @Override
    public void validate(File file, TrainSimulationUpdater updater) throws FileValidationException,IOException {


        BufferedReader br = new BufferedReader(new FileReader(file));
        HeaderData headerData = ValidationUtils.parseHeader(br);
        String line;

        List<FreightRouteData> freightRoutes = new ArrayList<>();
        List<PassengerRouteData> passengerRoutes = new ArrayList<>();


        int totalLines = 0, totalControls = 0, totalControlItems = 0;
        int expectedLines, expectedControls, expectedControlItems;

        Matcher matcher;
        while ((line = br.readLine()) != null) {
            matcher = controlPattern.matcher(line);
            if (matcher.matches()) {
                totalControls += 1;
                totalLines += 1;
                TrainType type = TrainType.parseFull(matcher.group(1));
                if (type == null) {
                    throw new RepeatableRouteFileValidationException(String.format("Unable to determine route type '%s'", matcher.group(1)));
                }
                expectedControlItems = Integer.valueOf(matcher.group(2));
                totalControlItems = 0;
                switch (type) {


                    case FREIGHT:

                        while (totalControlItems < expectedControlItems) {
                            line = br.readLine();
                            if (ValidationUtils.lineCheck(line, "Freight", expectedControlItems, totalControlItems))
                                continue;
                            matcher = freightRoutePattern.matcher(line);
                            if (!matcher.matches()) {
                                updater.getIssues().add(String.format("Invalid line found at %d, skipping", totalLines + totalControlItems + 1));
                            } else {
                                try {
                                    freightRoutes.add(new FreightRouteData(matcher.group(1), matcher.group(2), RouteType.parse(matcher.group(3)), ValidationUtils.timeToMinutes(matcher.group(4)), Integer.valueOf(matcher.group(5))));
                                } catch (RouteTypeParseException exception) {
                                    updater.getIssues().add(String.format("Invalid line found at %d, skipping", totalLines + totalControlItems + 1));
                                }
                            }

                            totalControlItems += 1;
                        }
                        break;
                    case PASSENGER:
                        while (totalControlItems < expectedControlItems) {
                            line = br.readLine();
                            if (ValidationUtils.lineCheck(line, "Passenger", expectedControlItems, totalControlItems))
                                continue;
                            matcher = routeControlPattern.matcher(line);

                            if (!matcher.matches()) {
                                updater.getIssues().add(String.format("Invalid line found at %d, skipping", totalLines + totalControlItems + 1));
                            } else {
                                passengerRoutes.add(ValidationUtils.processRoute(br, RouteType.parse(matcher.group(3)), Integer.valueOf(matcher.group(2)), updater));
                            }

                            totalControlItems += 1;
                        }
                        break;
                }


            } else if (line.startsWith("T")) {
                matcher = trailerPattern.matcher(line);
                if (matcher.matches()) {
                    expectedLines = Integer.valueOf(matcher.group(1));
                    expectedControls = Integer.valueOf(matcher.group(2));
                    if (totalLines != expectedLines || totalControls != expectedControls)
                        throw new RepeatableRouteFileValidationException("Invalid Trailer");
                    else
                        break;
                } else {
                    throw new RepeatableRouteFileValidationException("Invalid Trailer");
                }

            }
        }
        br.close();
        //Apply
        updater.getUpdates().add(simulation -> {
            if (!simulation.getFreightRoutes().containsKey(null))
                simulation.getFreightRoutes().put(null, new ArrayList<>());
            simulation.getFreightRoutes().get(null).addAll(freightRoutes);

            if (!simulation.getPassengerRoutes().containsKey(null))
                simulation.getPassengerRoutes().put(null, new ArrayList<>());
            simulation.getPassengerRoutes().get(null).addAll(passengerRoutes);
        });


    }


}
