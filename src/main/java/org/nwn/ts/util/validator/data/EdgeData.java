package org.nwn.ts.util.validator.data;

public class EdgeData {
    private final String vertexA;
    private final String vertexB;
    private final int distance;
    private final int restrictionStart;
    private final int restrictionEnd;

    public EdgeData(String vertexA, String vertexB, int distance, int restrictionStart, int restrictionEnd) {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.distance = distance;
        this.restrictionStart = restrictionStart;
        this.restrictionEnd = restrictionEnd;
    }

    public String getVertexA() {
        return vertexA;
    }

    public String getVertexB() {
        return vertexB;
    }

    public int getDistance() {
        return distance;
    }

    public int getRestrictionStart() {
        return restrictionStart;
    }

    public int getRestrictionEnd() {
        return restrictionEnd;
    }
}
