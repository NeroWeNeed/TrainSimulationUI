package org.nwn.ts.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/*
 * Not Ready Yet
 * */
public class BridgeLibraryLoader {
    public String sample;
    private static final String LIBRARY_LOCATION = "/lib/TrainSimulator.dll";
    private static BridgeLibraryLoader INSTANCE = new BridgeLibraryLoader();
    public static BridgeLibraryLoader getInstance() {
        return INSTANCE;
    }

    static {
        File dir = new File(System.getProperty("user.dir"), "lib");
        dir.mkdirs();

        File libraryFile = new File(System.getProperty("user.dir") + LIBRARY_LOCATION);
        System.out.println(libraryFile);
        try {
            System.out.println(LIBRARY_LOCATION);
            Files.copy(BridgeLibraryLoader.class.getResourceAsStream(LIBRARY_LOCATION), libraryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.setProperty("library.path",dir.toString());
        System.out.println(System.getProperty("library.path"));
        System.loadLibrary("TrainSimulatorx64");
    }

    private BridgeLibraryLoader() {
    }




}
