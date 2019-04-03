package org.nwn.ts.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.nwn.ts.Model;
import org.nwn.ts.TrainSimulation;
import org.nwn.ts.util.Configuration;
import org.nwn.ts.util.FilePicker;


import java.net.URL;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/config.fxml");

    @FXML
    private Parent container;


    /*
     * Files
     * */
    @FXML
    private FilePicker layoutFilePicker;
    @FXML
    private FilePicker maintenanceFilePicker;
    @FXML
    private FilePicker configurationFilePicker;
    @FXML
    private FilePicker dailyRoutesFilePicker;
    @FXML
    private FilePicker repeatableRoutesFilePicker;

    /*
    *    Options
    */
    @FXML
    private Slider weatherSeverity;

    @FXML
    private CheckBox collisionAvoidance;

    @FXML
    private TextField duration;

    @FXML
    private TextField transportationCost;


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
            HBox.setHgrow(toggleButton, Priority.ALWAYS);
            toggleButton.setToggleGroup(weatherTypeToggleGroup);
            weatherTypes.getChildren().add(toggleButton);
        }

        //Bindings
        BooleanBinding baselineSetInverse = Model.getInstance().baselineSetProperty().not();

        layoutFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        maintenanceFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        configurationFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        dailyRoutesFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());
        repeatableRoutesFilePicker.disableProperty().bind(Model.getInstance().baselineSetProperty());

        duration.disableProperty().bind(baselineSetInverse);
        transportationCost.disableProperty().bind(baselineSetInverse);
        weatherSeverity.disableProperty().bind(baselineSetInverse);
        collisionAvoidance.disableProperty().bind(baselineSetInverse);
        weatherTypes.disableProperty().bind(baselineSetInverse);

    }

    @FXML
    public void handleStartSimulationButton(ActionEvent event) {
        updateConfiguration();
        try {
            TrainSimulation.validateConfiguration(Model.getInstance().getConfiguration());
            TrainSimulation.runSimulation(Model.getInstance().getConfiguration());
            ((Stage) container.getScene().getWindow()).close();

        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            //TODO: Expand on Error Dialog
            alert.setHeaderText(null);
            alert.setContentText(exception.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    protected void updateConfiguration() {
        if (!Model.getInstance().getBaselineSet()) {
            Model.getInstance().getConfiguration().setLayoutFile(layoutFilePicker.getValue());
            Model.getInstance().getConfiguration().setMaintenanceFile(maintenanceFilePicker.getValue());
            Model.getInstance().getConfiguration().setConfigurationFile(configurationFilePicker.getValue());
            Model.getInstance().getConfiguration().setDailyRoutesFile(dailyRoutesFilePicker.getValue());
            Model.getInstance().getConfiguration().setRepeatableRoutesFile(repeatableRoutesFilePicker.getValue());
        }
    }


}
