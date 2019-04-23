package org.nwn.ts.simulation.data;

public class TrainMaintenanceData implements ComponentMaintenanceData {
    private final String name;
    private final int downtime;

    public TrainMaintenanceData(String name, int downtime) {
        this.name = name;
        this.downtime = downtime;
    }

    public String getName() {
        return name;
    }

    public int getDowntime() {
        return downtime;
    }

    @Override
    public String toString() {
        return "TrainMaintenanceData{" +
                "name='" + name + '\'' +
                ", downtime=" + downtime +
                '}';
    }
}
