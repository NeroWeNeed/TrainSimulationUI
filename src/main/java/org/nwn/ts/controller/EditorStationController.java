package org.nwn.ts.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.nwn.ts.Model;
import org.nwn.ts.simulation.data.*;
import org.nwn.ts.util.IntRange;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditorStationController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/editor-stations.fxml");
    private static final String namePattern = "\\S+";

    enum CreationType {
        HUB, STATION;
    }

    private BooleanBinding hubBinding;
    private BooleanBinding freightBinding;

    private ListProperty<HubData> hubList = new SimpleListProperty<>();
    private ListProperty<HubData> filteredHubList = new SimpleListProperty<>();
    private ListProperty<StationData> stationList = new SimpleListProperty<>();
    private ListProperty<StationData> filteredStationList = new SimpleListProperty<>();

    @FXML
    private ListView<HubData> hubListView;

    @FXML
    private ListView<StationData> stationListView;

    @FXML
    private ComboBox<CreationType> creationTypeComboBox;
    @FXML
    private TextField stationNameField;

    @FXML
    private ComboBox<TrainType> stationTypeBox;

    @FXML
    private TextField maxNumberOfTrainsField;

    @FXML
    private TextField randomOnRangeField;

    @FXML
    private TextField randomOffRangeField;

    @FXML
    private TextField ticketPrice;

    @FXML
    private Button addButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        creationTypeComboBox.getItems().addAll(CreationType.values());
        creationTypeComboBox.getSelectionModel().selectFirst();
        stationTypeBox.getItems().addAll(TrainType.values());
        stationTypeBox.getSelectionModel().selectFirst();
        populate();

        //Bindings
        hubBinding = Bindings.createBooleanBinding(() -> creationTypeComboBox.valueProperty().get() == CreationType.HUB, creationTypeComboBox.valueProperty());
        freightBinding = Bindings.createBooleanBinding(() -> stationTypeBox.valueProperty().get() == TrainType.FREIGHT, stationTypeBox.valueProperty());
        ticketPrice.disableProperty().bind(hubBinding.or(freightBinding));
        randomOffRangeField.disableProperty().bind(hubBinding.or(freightBinding));
        randomOnRangeField.disableProperty().bind(hubBinding.or(freightBinding));
        maxNumberOfTrainsField.disableProperty().bind(hubBinding);
        stationTypeBox.disableProperty().bind(hubBinding);
    }

    private void populate() {
        hubList.set(FXCollections.observableList(Model.getInstance().getSimulation().getHubs()));
        filteredHubList.set(hubList.filtered(HubData::isAvailable));
        hubListView.itemsProperty().bind(filteredHubList);
        hubListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        stationList.set(FXCollections.observableList(Model.getInstance().getSimulation().getStations()));
        filteredStationList.set(stationList.filtered(StationData::isAvailable));
        stationListView.itemsProperty().bind(filteredStationList);
        stationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    @FXML
    public void addButtonHandler(ActionEvent event) {
        String name = stationNameField.getText();
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, (hubBinding.get() ? "Hub" : "Station") + " Name is Required");
            alert.showAndWait();
            return;

        }


        if (hubBinding.get()) {
            if (Model.getInstance().getSimulation().getStations().stream().anyMatch(x -> x.getName().equals(name))) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Station with same hub name found.");
                alert.showAndWait();
                return;
            }
            List<HubData> conflicts = hubList.stream().filter(x -> x.isAvailable() && x.getName().equals(name)).collect(Collectors.toList());
            if (conflicts.size() == 1 && !conflicts.get(0).isAvailable()) {
                hubList.remove(conflicts.get(0));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Hub " + name + " already exists.");
                alert.showAndWait();
                return;
            }


            hubList.add(new HubData(name));

        } else {
            if (Model.getInstance().getSimulation().getHubs().stream().anyMatch(x -> x.getName().equals(name))) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Hub with same station name already exists");
                alert.showAndWait();
                return;
            }
            int maxNumberOfTrains;

            try {
                maxNumberOfTrains = Integer.parseInt(maxNumberOfTrainsField.getText());
                if (maxNumberOfTrains < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Max Number of trains must be non-negative.");
                    alert.showAndWait();
                    return;
                }

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to determine Max Number Of Trains");
                alert.showAndWait();
                return;
            }
            if (freightBinding.get()) {
                stationList.add(new StationData(name, TrainType.FREIGHT, maxNumberOfTrains, IntRange.EMPTY, IntRange.EMPTY, 0.0));

            } else {
                IntRange randomOn, randomOff;
                double price;

                try {
                    randomOn = IntRange.parse(randomOnRangeField.getText());
                    if (randomOn.getLow() < 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Random Loading Range must not be negative.");
                        alert.showAndWait();
                        return;
                    }

                } catch (IllegalArgumentException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to determine Random on Range");
                    alert.showAndWait();
                    return;
                }
                try {
                    randomOff = IntRange.parse(randomOffRangeField.getText());
                    if (randomOff.getLow() < 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Random Unloading Range must not be negative.");
                        alert.showAndWait();
                        return;
                    }

                } catch (IllegalArgumentException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to determine Random off Range");
                    alert.showAndWait();
                    return;
                }
                try {
                    price = Double.valueOf(ticketPrice.getText());
                    if (price < 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Ticket Price must not be negative.");
                        alert.showAndWait();
                        return;
                    }

                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to determine Random off Range");
                    alert.showAndWait();
                    return;
                }

                stationList.add(new StationData(name, stationTypeBox.getValue(), maxNumberOfTrains, randomOn, randomOff, price));
            }

        }
    }

    public void removeSelectedHubs(ActionEvent event) {
        List<HubData> targets = new ArrayList<>(hubListView.getSelectionModel().getSelectedItems());
        for (HubData d : targets) {
            if (d.isUsed()) {
                d.setAvailable(false);

            } else {
                hubList.remove(d);
            }
        }
    }

    public void contextDeleteStations(ActionEvent event) {
        List<StationData> targets = new ArrayList<>(stationListView.getSelectionModel().getSelectedItems());
        for (StationData d : targets) {
            if (d.isUsed()) {
                d.setAvailable(false);

            } else {
                stationList.remove(d);
            }
        }


    }

}
