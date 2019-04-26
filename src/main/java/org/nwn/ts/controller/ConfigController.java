package org.nwn.ts.controller;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.simulation.data.TrainType;
import org.nwn.ts.util.FieldFormatter;
import org.nwn.ts.util.FilePicker;
import org.nwn.ts.simulation.TrainSimulation;
import org.nwn.ts.simulation.TrainSimulationConfiguration;
import org.nwn.ts.simulation.TrainSimulationOverrides;
import org.nwn.ts.simulation.TrainSimulationUpdater;
import org.nwn.ts.util.TrainConfigurationProperty;
import org.nwn.ts.validator.ConfigurationFileValidator;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/config.fxml");
    private static final String WEATHER_TYPE_VALUE = "weatherTypeValue";

    private TrainSimulationOverrides overrides = new TrainSimulationOverrides();
    @FXML
    private Parent container;


    /*
     * Files
     * */
    @FXML
    private FilePicker structureFilePicker;
    @FXML
    private FilePicker maintenanceFilePicker;
    @FXML
    private FilePicker configurationFilePicker;
    @FXML
    private FilePicker dailyRoutesFilePicker;
    @FXML
    private FilePicker repeatableRoutesFilePicker;
    @FXML
    private FilePicker outputFilePicker;

    /*
     *    Options
     */
    @FXML
    private Slider weatherSeverity;

    @FXML
    private TextField duration;

    @FXML
    private TextField crewsPerHub;

    @FXML
    private TextField fuelPerHub;


    @FXML
    private Button startSimulationButton;

    @FXML
    private TrainConfigController freightTrainConfigController;

    @FXML
    private TrainConfigController passengerTrainConfigController;

    @FXML
    private ComboBox<TrainSimulation.WeatherType> weatherType;


    public ConfigController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (TrainSimulation.WeatherType type : TrainSimulation.WeatherType.values()) {
            weatherType.getItems().add(type);
        }


        freightTrainConfigController.setType(TrainType.FREIGHT);
        passengerTrainConfigController.setType(TrainType.PASSENGER);
        weatherType.getSelectionModel().select(0);

        configurationFilePicker.getValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                ConfigurationFileValidator validator = new ConfigurationFileValidator();
                try {
                    validator.validate(newValue, null);
                    Integer parsedCrewsPerHub = validator.getTotalCrews();
                    if (parsedCrewsPerHub != null)
                        overrides.setCrewsPerHub(parsedCrewsPerHub);
                    Integer parsedRunDuration = validator.getRunDuration();
                    if (parsedRunDuration != null)
                        overrides.setDaysToRun(parsedRunDuration);
                    Integer parsedFuelCapacity = validator.getHubFuelCapacity();
                    if (parsedFuelCapacity != null)
                        overrides.setFuelPerHub(parsedFuelCapacity);
                    validator.getTrainConfigurations().forEach((key, value) -> {
                        switch (key) {

                            case FREIGHT:
                                freightTrainConfigController.set(value);
                                break;
                            case PASSENGER:
                                passengerTrainConfigController.set(value);
                                break;
                        }
                    });
                } catch (FileValidationException | IOException e) {

                }
            }
        });

        Bindings.bindBidirectional(crewsPerHub.textProperty(), overrides.crewsPerHubProperty(), new FieldFormatter());
        Bindings.bindBidirectional(duration.textProperty(), overrides.daysToRunProperty(), new FieldFormatter());
        Bindings.bindBidirectional(fuelPerHub.textProperty(), overrides.fuelPerHubProperty(), new FieldFormatter());

        startSimulationButton.disableProperty().bind(Bindings.createBooleanBinding(() -> structureFilePicker.getValue() != null &&
                        configurationFilePicker.getValue() != null &&
                        dailyRoutesFilePicker.getValue() != null &&
                        overrides.getValid() && overrides.getTrainConfigurations().values().stream().allMatch(TrainConfigurationProperty::isValid)

                ,

                submitButtonDependencies()).not());


    }

    private Observable[] submitButtonDependencies() {
        ArrayList<Observable> observables = new ArrayList<>();
        observables.add(structureFilePicker.getValueProperty());
        observables.add(dailyRoutesFilePicker.getValueProperty());
        observables.add(overrides.validProperty());
        overrides.getTrainConfigurations().values().forEach(x -> {

            observables.add(x.validProperty());
        });
        return observables.stream().toArray(Observable[]::new);
    }

    @FXML
    public void handleStartSimulationButton(ActionEvent event) {
        TrainSimulationConfiguration configuration = new TrainSimulationConfiguration();

        configuration.setStructureFile(structureFilePicker.getValue());
        configuration.setConfigurationFile(configurationFilePicker.getValue());
        configuration.setDailyRoutesFile(dailyRoutesFilePicker.getValue());
        configuration.setRepeatableRoutesFile(repeatableRoutesFilePicker.getValue());
        configuration.setMaintenanceFile(maintenanceFilePicker.getValue());
        configuration.setOutputDirectory(outputFilePicker.getValue());
        TrainSimulationUpdater updater;
        TrainSimulation instance = TrainSimulation.getInstance();

        try {
            updater = configuration.createUpdater();
            if (!updater.getIssues().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                StringBuilder sb = new StringBuilder();
                updater.getIssues().forEach(x -> {
                    sb.append(x).append("\n");
                });
                alert.setContentText("Proceed with the following errors:\n" + sb.toString());
                alert.getButtonTypes().setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES), new ButtonType("No", ButtonBar.ButtonData.NO));
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {

                    updater.apply(instance);
                    instance.simulate(configuration.getOutputDirectory());

                }

            } else {
                updater.apply(instance);
                if (instance.getHubs().isEmpty() || instance.getTrains().isEmpty() || instance.getRails().isEmpty() || instance.getStations().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Must include at least 1 Hub, Station, Train, and rail");

                    alert.showAndWait();

                    return;

                }
                instance.simulate(configuration.getOutputDirectory());
            }
        } catch (FileValidationException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }


    }

    @FXML
    public void handleCancelSimulationButton(ActionEvent event) {
        ((Stage) container.getScene().getWindow()).close();
    }

    protected void updateConfiguration() {
/*        if (!Model.getInstance().getBaselineSet()) {
            Model.getInstance().getConfiguration().setLayoutFile(structureFilePicker.getValue());
            Model.getInstance().getConfiguration().setMaintenanceFile(maintenanceFilePicker.getValue());
            Model.getInstance().getConfiguration().setConfigurationFile(configurationFilePicker.getValue());
            Model.getInstance().getConfiguration().setDailyRoutesFile(dailyRoutesFilePicker.getValue());
            Model.getInstance().getConfiguration().setRepeatableRoutesFile(repeatableRoutesFilePicker.getValue());
        }*/
    }


}
