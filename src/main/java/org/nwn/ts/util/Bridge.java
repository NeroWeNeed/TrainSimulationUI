package org.nwn.ts.util;

import org.nwn.ts.stats.MetricHolder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

/*
 * Not Ready Yet
 * */
public class Bridge {
    private static final String LIBRARY_LOCATION = "/lib/TrainSimulator.dll";
    private static Bridge INSTANCE = new Bridge();
    public static Bridge getInstance() {
        return INSTANCE;
    }

    static {
        File dir = new File(System.getProperty("user.dir"), "lib");
        dir.mkdirs();

        File libraryFile = new File(System.getProperty("user.dir") + LIBRARY_LOCATION);
        System.out.println(libraryFile);
        try {
            System.out.println(LIBRARY_LOCATION);
            Files.copy(Bridge.class.getResourceAsStream(LIBRARY_LOCATION), libraryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.setProperty("library.path",dir.toString());
        System.out.println(System.getProperty("library.path"));
        System.loadLibrary("TrainSimulatorx64");
    }

    private Bridge() {
    }

    /*
     * Large Function Signature
     * Returns output file location
     * */
    public native String startSimulation(String layoutFile,
                                         String maintenanceFile,
                                         String configurationFile,
                                         String dailyRoutesFile,
                                         String repeatableRoutesFile,
                                         float weatherSeverity,
                                         int weatherType,
                                         boolean avoidCollisions,
                                         int duration,
                                         int transportCost
    );

    public List<MetricHolder> parseResults(File resultsFile) {
        //TODO: Parse Output File
        return Collections.emptyList();
    }


}
