package org.nwn.ts.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/*
 * Not Ready Yet
 * */
public class Bridge {
    private static final String LIBRARY_LOCATION = "/lib/TrainSimulator.dll";

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

    //Large Function Signature
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


}
