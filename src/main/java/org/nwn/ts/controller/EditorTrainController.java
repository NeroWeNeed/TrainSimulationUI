package org.nwn.ts.controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.nwn.ts.Model;
import org.nwn.ts.simulation.data.HubData;
import org.nwn.ts.simulation.data.TrainType;
import org.nwn.ts.simulation.data.TrainData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditorTrainController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/editor-trains.fxml");
    private static final String namePattern = "\\S+";

    private ListProperty<TrainData> trainList = new SimpleListProperty<>();

    private ListProperty<TrainData> filteredTrainList = new SimpleListProperty<>();


    private ListProperty<HubData> hubList = new SimpleListProperty<>();

    private ListProperty<HubData> filteredHubList = new SimpleListProperty<HubData>();

    @FXML
    private ListView<TrainData> trainListView;

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
        populate();
    }

    @FXML
    public void addButton() {
        if (trainNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Train Name is Required");
            alert.showAndWait();
            return;
        }
        if (!trainNameField.getText().matches(namePattern)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Train Name may not contain whitespace characters. (no spaces)");
            alert.showAndWait();
            return;
        }

        TrainData trainData = new TrainData(trainNameField.getText(), trainHubField.getValue(), trainTypeBox.getValue());
        List<TrainData> conflicts = trainList.stream().filter(x -> x.isAvailable() && x.getName().equals(trainData.getName())).collect(Collectors.toList());
        if (!conflicts.isEmpty()) {
            if (conflicts.size() == 1 && !conflicts.get(0).isAvailable()) {
                trainList.remove(conflicts.get(0));

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Train " + trainData.getName() + " already exists.");
                alert.showAndWait();
                return;
            }
        }
        trainList.add(trainData);
    }

    private void populate() {
        trainList.set(FXCollections.observableList(Model.getInstance().getSimulation().getTrains()));
        filteredTrainList.set(trainList.filtered(TrainData::isAvailable));
        trainListView.itemsProperty().bind(filteredTrainList);
        trainListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hubList.set(FXCollections.observableList(Model.getInstance().getSimulation().getHubs()));
        filteredHubList.set(hubList.filtered(HubData::isAvailable));
        trainHubField.itemsProperty().bind(filteredHubList);
        trainHubField.getSelectionModel().selectFirst();

    }

    @FXML
    public void contextDeleteTrains() {
        List<TrainData> targets = new ArrayList<>(trainListView.getSelectionModel().getSelectedItems());
        for (TrainData d : targets) {
            if (d.isUsed()) {
                d.setAvailable(false);

            } else {
                trainList.remove(d);
            }
        }


    }

}
