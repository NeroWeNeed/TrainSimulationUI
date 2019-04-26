package org.nwn.ts.util;

import javafx.util.StringConverter;

public class RangeFieldFormatter extends StringConverter<IntRange> {
    @Override
    public String toString(IntRange object) {
        return object.toString();
    }

    @Override
    public IntRange fromString(String string) {
        try {
            return IntRange.parse(string);
        }
        catch (IllegalArgumentException e) {
            return null;
        }

    }
}
