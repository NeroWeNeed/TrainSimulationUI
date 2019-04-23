package org.nwn.ts.validator;

import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.simulation.TrainSimulationUpdater;

import java.io.File;
import java.io.IOException;

public interface Validator {
    void validate(File file, TrainSimulationUpdater updater) throws FileValidationException, IOException;



}
