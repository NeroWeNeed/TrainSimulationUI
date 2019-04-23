package org.nwn.ts.simulation;

import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.exceptions.StructureFileValidationException;
import org.nwn.ts.validator.*;

import java.io.File;
import java.io.IOException;

public class TrainSimulationConfiguration {
    private File structureFile = null;
    private File maintenanceFile = null;
    private File configurationFile = null;
    private File repeatableRoutesFile = null;
    private File dailyRoutesFile = null;
    private File outputDirectory = null;

    public TrainSimulationConfiguration() {
    }

    public TrainSimulationUpdater createUpdater() throws FileValidationException, IOException {
        TrainSimulationUpdater updater = new TrainSimulationUpdater();
        if (structureFile == null)
            throw new StructureFileValidationException("Layout File has not been defined.");
        StructureFileValidator structureFileValidator = new StructureFileValidator();
        structureFileValidator.validate(structureFile, updater);
        testFile("Maintenance File", maintenanceFile, new MaintenanceFileValidator(), updater);
        testFile("Configuration File", configurationFile, new ConfigurationFileValidator(), updater);
        testFile("Repeatable Routes File", repeatableRoutesFile, new RepeatableRoutesFileValidator(), updater);
        testFile("Daily Routes File", dailyRoutesFile, new DailyRoutesFileValidator(), updater);


        return updater;
    }

    private void testFile(String name, File file, Validator validator, TrainSimulationUpdater updater) {
        if (file == null) {
            updater.getIssues().add(String.format("No %s provided.", name));
        } else {
            try {
                validator.validate(file, updater);
            } catch (FileValidationException | IOException e) {
                updater.getIssues().add(String.format("Unable to parse %s: %s", name, file));
            }


        }
    }

    public File getStructureFile() {
        return structureFile;
    }

    public void setStructureFile(File structureFile) {
        this.structureFile = structureFile;
    }

    public File getMaintenanceFile() {
        return maintenanceFile;
    }

    public void setMaintenanceFile(File maintenanceFile) {
        this.maintenanceFile = maintenanceFile;
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    public File getRepeatableRoutesFile() {
        return repeatableRoutesFile;
    }

    public void setRepeatableRoutesFile(File repeatableRoutesFile) {
        this.repeatableRoutesFile = repeatableRoutesFile;
    }

    public File getDailyRoutesFile() {
        return dailyRoutesFile;
    }

    public void setDailyRoutesFile(File dailyRoutesFile) {
        this.dailyRoutesFile = dailyRoutesFile;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
