package org.nwn.ts.util.validator;

import org.nwn.ts.util.validator.data.HeaderData;

import java.io.File;
import java.io.FileNotFoundException;

public interface Validator {
    void validate(File file) throws Exception;

}
