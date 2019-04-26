package org.nwn.ts.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IntRange {
    private final int low;
    private final int high;
    private static final Pattern RANGE_PATTERN = Pattern.compile("([0-9]+)\\-([0-9]+)");
    public static final IntRange EMPTY = new IntRange(0,0);

    public IntRange(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }
    public static IntRange parse(String input) {
        Matcher matcher = RANGE_PATTERN.matcher(input);
        if (matcher.matches()) {
            int value1 = Integer.valueOf(matcher.group(1));
            int value2 = Integer.valueOf(matcher.group(2));
            if (value1 < value2) {
                return new IntRange(value1,value2);
            }
            else
                throw new IllegalArgumentException("First Value must be less than second");

        }
        else
            throw new IllegalArgumentException("Must match pattern: " + RANGE_PATTERN);
    }

    @Override
    public String toString() {
        return low + "-" + high;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntRange)) return false;
        IntRange intRange = (IntRange) o;
        return low == intRange.low &&
                high == intRange.high;
    }

    @Override
    public int hashCode() {
        return Objects.hash(low, high);
    }
}
