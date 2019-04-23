package org.nwn.ts.util;

import javafx.util.StringConverter;

public class FieldFormatter extends StringConverter<Number> {

    @Override
    public String toString(Number object) {
        if (object == null || object.intValue() == 0)
            return "";
        else
            return object.toString();
    }

    @Override
    public Integer fromString(String string) {
        if (string == null || string.isEmpty())
            return 0;
        else {
            try {
                return Integer.valueOf(string);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
