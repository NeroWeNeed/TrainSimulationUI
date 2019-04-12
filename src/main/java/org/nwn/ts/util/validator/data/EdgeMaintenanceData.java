package org.nwn.ts.util.validator.data;

public class EdgeMaintenanceData implements ComponentMaintenanceData {

    private final int downtime;
    private final String vertexA,vertexB;

    public EdgeMaintenanceData(int downtime, String vertexA, String vertexB) {
        this.downtime = downtime;
        this.vertexA = vertexA;
        this.vertexB = vertexB;
    }

    @Override
    public int getDowntime() {
        return downtime;
    }

    public String getVertexA() {
        return vertexA;
    }

    public String getVertexB() {
        return vertexB;
    }

    @Override
    public String toString() {
        return "EdgeMaintenanceData{" +
                "downtime=" + downtime +
                ", vertexA='" + vertexA + '\'' +
                ", vertexB='" + vertexB + '\'' +
                '}';
    }
}
