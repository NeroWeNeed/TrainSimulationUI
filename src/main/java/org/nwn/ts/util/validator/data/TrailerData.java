package org.nwn.ts.util.validator.data;

public class TrailerData {
    private final int expectedLines;
    private final int expectedContentLines;

    public TrailerData(int expectedLines, int expectedContentLines) {
        this.expectedLines = expectedLines;
        this.expectedContentLines = expectedContentLines;
    }

    public int getExpectedLines() {
        return expectedLines;
    }

    public int getExpectedContentLines() {
        return expectedContentLines;
    }
}
