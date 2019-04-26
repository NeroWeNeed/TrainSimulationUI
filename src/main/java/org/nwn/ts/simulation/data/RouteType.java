package org.nwn.ts.simulation.data;

import org.nwn.ts.exceptions.RouteTypeParseException;

public enum RouteType {
    FULL_WEEK('F'), WORK_WEEK('W'), DAILY('D');

    private final char associatedChar;

    RouteType(char associatedChar) {
        this.associatedChar = associatedChar;
    }

    public static RouteType parse(String input) throws RouteTypeParseException {
        if (input == null)
            return DAILY;
        else if (input.equalsIgnoreCase("F"))
            return FULL_WEEK;
        else if (input.equalsIgnoreCase("W"))
            return WORK_WEEK;
        else
            throw new RouteTypeParseException(String.format("Unable to determine route type from: %s", input));

    }

    public char getAssociatedChar() {
        return associatedChar;
    }
}
