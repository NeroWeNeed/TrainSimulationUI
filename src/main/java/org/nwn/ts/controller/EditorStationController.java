package org.nwn.ts.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.nwn.ts.Model;
import org.nwn.ts.simulation.data.*;

import java.net.URL;
import java.util.ResourceBundle;

public class EditorStationController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/editor-trains.fxml");
    private static final String namePattern = "\\S+";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    enum CreationType {
        HUB, STATION;
    }


    @FXML
    private ComboBox<CreationType> type;
    @FXML
    private ListView<NodeData> nodes;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<TrainType> trainTypeBox;

    @FXML
    private TextField maxNumberOfTrainsField;

    @FXML
    private TextField randomOnRangeField;

    @FXML
    private TextField randomOffRangeField;

    @FXML
    private ComboBox<HubData> trainHubField;


/*    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trainTypeBox.getItems().addAll(TrainType.values());
        trainTypeBox.getSelectionModel().select(0);
        populateTrains();
        populateHubs();
    }

    @FXML
    public void addButton() {
        if (nameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Train Name is Required");
            alert.showAndWait();
            return;
        }
        if (!nameField.getText().matches(namePattern)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Train Name may not contain whitespace characters. (no spaces)");
            alert.showAndWait();
            return;
        }

        TrainData trainData = new TrainData(nameField.getText(), trainHubField.getValue(), trainTypeBox.getValue());

        if (Model.getInstance().getSimulation().getTrains().contains(trainData)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Train " + trainData.getName() + " already exists.");
            alert.showAndWait();
            return;
        }
        Model.getInstance().getSimulation().getTrains().add(trainData);
        nodes.getItems().add(trainData);
    }

    private void populateTrains() {
        nodes.getItems().addAll(Model.getInstance().getSimulation().getTrains());

    }

    private void populateHubs() {
        trainHubField.getItems().addAll(Model.getInstance().getSimulation().getHubs());
        trainHubField.getSelectionModel().selectFirst();
    }*/
}
