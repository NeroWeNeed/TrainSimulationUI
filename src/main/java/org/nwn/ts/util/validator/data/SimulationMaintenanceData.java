package org.nwn.ts.util.validator.data;

import java.util.HashMap;
import java.util.Map;

public class SimulationMaintenanceData {
    private final Map<Integer,MaintenanceData> days = new HashMap<>();

    public SimulationMaintenanceData() {
    }

    public Map<Integer, MaintenanceData> getDays() {
        return days;
    }

    @Override
    public String toString() {
        return "SimulationMaintenanceData{" +
                "days=" + days +
                '}';
    }
}
