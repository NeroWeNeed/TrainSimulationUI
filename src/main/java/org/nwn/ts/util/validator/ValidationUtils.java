package org.nwn.ts.util.validator;

import org.nwn.ts.util.validator.data.HeaderData;
import org.nwn.ts.util.validator.data.TrailerData;
import org.nwn.ts.util.validator.data.TrainData;
import org.nwn.ts.util.validator.exception.FileValidationException;
import org.nwn.ts.util.validator.exception.InvalidHeaderException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern headerPattern = Pattern.compile("H([0-9]{4})\\s*([0-9]{2}\\/[0-9]{2}\\/[0-9]{4})\\s*");


    public static HeaderData parseHeader(BufferedReader br) throws IOException, InvalidHeaderException {
        String line;
        do {
            line = br.readLine();
            if (line == null)
                throw new InvalidHeaderException("Expected File Header, Found EOF.");
        } while (line.isEmpty());
        Matcher matcher = headerPattern.matcher(line);
        if (!matcher.matches())
            throw new InvalidHeaderException(String.format("Expected File header, Found: %s", line));

        return new HeaderData(Integer.valueOf(matcher.group(1)), matcher.group(2));
    }


}
