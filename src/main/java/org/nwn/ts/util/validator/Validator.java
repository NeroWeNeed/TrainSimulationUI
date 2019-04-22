package org.nwn.ts.util.validator;

import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.util.validator.data.HeaderData;
import org.nwn.ts.util.validator.data.TrainSimulation;
import org.nwn.ts.util.validator.data.TrainSimulationUpdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface Validator {
    void validate(File file, TrainSimulationUpdater updater) throws FileValidationException, IOException;



}
