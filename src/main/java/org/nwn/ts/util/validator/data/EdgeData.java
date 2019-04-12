package org.nwn.ts.util.validator.data;

public class EdgeData {
    final String vertexA;
    final String vertexB;
    final int distance;
    final int restrictionStart;
    final int restrictionEnd;

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
