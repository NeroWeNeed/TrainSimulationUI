package org.nwn.ts.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.nwn.ts.Model;
import org.nwn.ts.simulation.data.HubData;
import org.nwn.ts.simulation.data.TrainType;
import org.nwn.ts.simulation.data.TrainData;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class EditorTrainController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/editor-trains.fxml");
    private static final String namePattern = "\\S+";

    @FXML
    private ListView<TrainData> trains;

    @FXML
    private TextField trainNameField;

    @FXML
    private ComboBox<HubData> trainHubField;

    @FXML
    private ComboBox<TrainType> trainTypeBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trainTypeBox.getItems().addAll(TrainType.values());
        trainTypeBox.getSelectionModel().select(0);
        populateTrains();
        populateHubs();
    }

    @FXML
    public void addButton() {
        if (trainNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Train Name is Required");
            alert.showAndWait();
            return;
        }
        if (!trainNameField.getText().matches(namePattern)) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Train Name may not contain whitespace characters. (no spaces)");
            alert.showAndWait();
            return;
        }

        TrainData trainData = new TrainData(trainNameField.getText(), trainHubField.getValue(), trainTypeBox.getValue());

        if (Model.getInstance().getSimulation().getTrains().contains(trainData)) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Train " + trainData.getName() + " already exists.");
            alert.showAndWait();
            return;
        }
        Model.getInstance().getSimulation().getTrains().add(trainData);
        trains.getItems().add(trainData);
    }

    private void populateTrains() {
        trains.getItems().addAll(Model.getInstance().getSimulation().getTrains());

    }
    private void populateHubs() {
        trainHubField.getItems().addAll(Model.getInstance().getSimulation().getHubs());
        trainHubField.getSelectionModel().selectFirst();
    }
}
