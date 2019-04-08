package org.nwn.ts.util;

import com.sun.javafx.PlatformUtil;

import java.io.File;

public class LibraryLoader {
    private static final File LIBRARY_DIR = new File(System.getProperty("user.dir"), "lib");
    private static final File LIBRARY_DIR_64 = new File(System.getProperty("user.dir"), "lib/x64");

    public static void loadLibrary(String name) {

        //Determine OS

        Bit bit = Bit.get();
        File libPath;
        switch (bit) {

            case BIT_64:
                libPath = LIBRARY_DIR_64;
                break;
            case BIT_32:
            case BIT_UNKNOWN:
                libPath = LIBRARY_DIR;
                break;
        }

        if (PlatformUtil.isWindows()) {
//TODO: Export DLL


        } else if (PlatformUtil.isMac()) {
//TODO: Export Mac File(?)
        } else if (PlatformUtil.isLinux() || PlatformUtil.isUnix()) {
//TODO: Export Linux File(?)
        }


    }


    enum Bit {
        BIT_64, BIT_32, BIT_UNKNOWN;

        public static Bit get() {
            String bit = System.getProperty("sun.arch.data.model");
            switch (bit) {
                case "64":
                    return BIT_64;
                case "32":
                    return BIT_32;
                default:
                    return BIT_UNKNOWN;
            }
        }
    }
}
