package org.nwn.ts.util.validator.data;

import org.nwn.ts.exceptions.RouteTypeParseException;

public enum RouteType {
    FULL_WEEK, WORK_WEEK, DAILY;

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
}
