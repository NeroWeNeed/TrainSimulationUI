package org.nwn.ts.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.nwn.ts.Model;
import org.nwn.ts.exceptions.FileValidationException;
import org.nwn.ts.stats.TrainType;
import org.nwn.ts.util.Configuration;
import org.nwn.ts.util.FilePicker;
import org.nwn.ts.util.TrainConfigurationProperty;
import org.nwn.ts.util.validator.data.TrainSimulation;
import org.nwn.ts.util.validator.data.TrainSimulationConfiguration;
import org.nwn.ts.util.validator.data.TrainSimulationOverrides;
import org.nwn.ts.util.validator.data.TrainSimulationUpdater;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/config.fxml");
    private static final String WEATHER_TYPE_VALUE = "weatherTypeValue";
    private TrainSimulationOverrides overrides = new TrainSimulationOverrides();
    private Map<TrainType, TrainConfigController> trainConfigs = new HashMap<>();
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
    private TextField transportationCost;

    @FXML
    private Button startSimulationButton;

    @FXML
    private TrainConfigController passengerTrainConfig;

    @FXML
    private TrainConfigController freightTrainConfig;


    @FXML
    private HBox weatherTypes;

    private ToggleGroup weatherTypeToggleGroup = new ToggleGroup();

    public ConfigController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleButton toggleButton;
        for (Configuration.WeatherType type : Configuration.WeatherType.values()) {
            toggleButton = new ToggleButton(type.name());
            toggleButton.getProperties().put(WEATHER_TYPE_VALUE, type);
            HBox.setHgrow(toggleButton, Priority.ALWAYS);
            toggleButton.setToggleGroup(weatherTypeToggleGroup);
            weatherTypes.getChildren().add(toggleButton);
        }
        trainConfigs.put(TrainType.FREIGHT, freightTrainConfig);
        trainConfigs.put(TrainType.PASSENGER, passengerTrainConfig);
        freightTrainConfig.setType(TrainType.FREIGHT);
        passengerTrainConfig.setType(TrainType.PASSENGER);

        TrainConfigurationProperty property;
        TrainConfigController config;
        for (TrainType value : TrainType.values()) {
            property = overrides.getTrainConfiguration(value);
            config = trainConfigs.get(value);

            Bindings.bindBidirectional(config.getFuelCapacity().textProperty(), property.fuelCapacityProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(config.getCapacity().textProperty(), property.capacityProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(config.getSpeed().textProperty(), property.speedProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(config.getFuelCost().textProperty(), property.fuelCostProperty(), new NumberStringConverter());

        }

        Bindings.bindBidirectional(crewsPerHub.textProperty(), overrides.crewsPerHubProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(fuelPerHub.textProperty(), overrides.fuelPerHubProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(duration.textProperty(), overrides.daysToRunProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(weatherSeverity.valueProperty(), overrides.weatherSeverityProperty());







        //Bindings
        BooleanBinding baselineSetInverse = Model.getInstance().baselineSetProperty().not();

        structureFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        maintenanceFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        configurationFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        dailyRoutesFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        repeatableRoutesFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());

        duration.disableProperty().bind(baselineSetInverse);
        transportationCost.disableProperty().bind(baselineSetInverse);
        weatherSeverity.disableProperty().bind(baselineSetInverse);
        weatherTypes.disableProperty().bind(baselineSetInverse);
        startSimulationButton.disableProperty().bind(Bindings.createBooleanBinding(() -> structureFilePicker.getValue() != null &&
                        configurationFilePicker.getValue() != null,
                structureFilePicker.getValueProperty(), configurationFilePicker.getValueProperty()).not());

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
        if (!Model.getInstance().getBaselineSet()) {
            Model.getInstance().getConfiguration().setLayoutFile(structureFilePicker.getValue());
            Model.getInstance().getConfiguration().setMaintenanceFile(maintenanceFilePicker.getValue());
            Model.getInstance().getConfiguration().setConfigurationFile(configurationFilePicker.getValue());
            Model.getInstance().getConfiguration().setDailyRoutesFile(dailyRoutesFilePicker.getValue());
            Model.getInstance().getConfiguration().setRepeatableRoutesFile(repeatableRoutesFilePicker.getValue());
        }
    }


}
