package org.nwn.ts.simulation.data;

public class HeaderData {
    private final int sequenceNumber;
    private final String creationDate;

    public HeaderData(int sequenceNumber, String creationDate) {
        this.sequenceNumber = sequenceNumber;
        this.creationDate = creationDate;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getCreationDate() {
        return creationDate;
    }
}
