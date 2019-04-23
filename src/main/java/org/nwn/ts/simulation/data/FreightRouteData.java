package org.nwn.ts.simulation.data;

public class FreightRouteData {

    private String start;
    private String end;
    private RouteType type;
    private int startTime;
    private int capacity;

    public FreightRouteData(String start, String end, RouteType type, int startTime, int capacity) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.startTime = startTime;
        this.capacity = capacity;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
