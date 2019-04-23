package org.nwn.ts.simulation.data;

import org.nwn.ts.util.MinutesToTimeFormatter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class EdgeData {
    private static AtomicInteger counter = new AtomicInteger();
    private final String vertexA;
    private final String vertexB;
    private final int distance;
    private final int restrictionStart;
    private final int restrictionEnd;
    private int ID = counter.getAndAdd(1);
    private boolean enabled = true;
    private boolean used = false;

    public EdgeData(String vertexA, String vertexB, int distance, int restrictionStart, int restrictionEnd) {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.distance = distance;
        this.restrictionStart = restrictionStart;
        this.restrictionEnd = restrictionEnd;
    }
    public EdgeData(NodeData vertexA, NodeData vertexB, int distance, int restrictionStart, int restrictionEnd) {
        this.vertexA = vertexA.name;
        this.vertexB = vertexB.name;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUsed() {
        return used;
    }
    public void toggle() {
        this.enabled = !this.enabled;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void enable() {
        this.enabled = true;
    }
    public void disable() {
        this.enabled = false;
    }

    @Override
    public String toString() {
        return "[RAIL] " + vertexA + " < - > " + vertexB + " (" + distance + ")" + (restrictionStart == 0 && restrictionEnd == 0 ? "" : " ("
                + MinutesToTimeFormatter.toTimeString(restrictionStart) + " - " + MinutesToTimeFormatter.toTimeString(restrictionEnd) + ")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdgeData)) return false;
        EdgeData edgeData = (EdgeData) o;
        return distance == edgeData.distance &&
                restrictionStart == edgeData.restrictionStart &&
                restrictionEnd == edgeData.restrictionEnd &&
                ID == edgeData.ID &&
                vertexA.equals(edgeData.vertexA) &&
                vertexB.equals(edgeData.vertexB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexA, vertexB, distance, restrictionStart, restrictionEnd, ID);
    }
}
