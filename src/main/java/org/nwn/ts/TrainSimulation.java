package org.nwn.ts;

import org.nwn.ts.util.Configuration;

public class TrainSimulation {
    public static void validateConfiguration(Configuration configuration) throws Exception {
        System.out.println(configuration);
    }

    public static void runSimulation(Configuration configuration) {
//TODO Call Simulation
        System.out.println("HEY");
        System.out.println(Model.getInstance().baselineSetProperty());
        Model.getInstance().setBaselineSet(true);
        System.out.println(Model.getInstance().baselineSetProperty());
    }
}
