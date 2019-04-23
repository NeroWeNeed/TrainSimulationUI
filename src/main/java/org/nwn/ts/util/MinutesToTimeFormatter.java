package org.nwn.ts.util;

import javafx.util.StringConverter;

public class MinutesToTimeFormatter extends StringConverter<Integer> {
    @Override
    public String toString(Integer object) {
        return MinutesToTimeFormatter.toTimeString(object);
    }

    @Override
    public Integer fromString(String string) {
        return fromTimeString(string);
    }

    public static int fromTimeString(String string) {
        if (string != null && string.length() == 5) {
            int h = Integer.parseInt(string.substring(0, 2));
            int m = Integer.parseInt(string.substring(3, 5));
            return (h * 60) + m;
        } else
            return 0;
    }

    public static String toTimeString(Integer object) {
        if (object == null || object <= 0)
            return "00:00";
        else {
            int h = object / 60;
            int m = object % 60;
            return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
        }
    }

}
