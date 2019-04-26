package org.nwn.ts.controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.nwn.ts.Model;
import org.nwn.ts.simulation.data.EdgeData;
import org.nwn.ts.simulation.data.HubData;
import org.nwn.ts.simulation.data.NodeData;
import org.nwn.ts.util.MinutesToTimeFormatter;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditorRailwayController implements Initializable {
    public static URL FXML_LOCATION = ConfigController.class.getResource("/fxml/editor-rails.fxml");
    @FXML
    private ListView<EdgeData> rails;

    @FXML
    private ComboBox<NodeData> station1;

    @FXML
    private ComboBox<NodeData> station2;

    @FXML
    private TextField distance;

    @FXML
    private ComboBox<Integer> restrictionStartTime;

    @FXML
    private ComboBox<Integer> restrictionEndTime;

    @FXML
    private Button addButton;

    @FXML
    private ContextMenu contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTimeBoxes();
        populateNodeBoxes();
        populateRails();
        rails.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }

    @FXML
    public void addButtonHandler(ActionEvent event) {
        NodeData node1 = station1.getValue();
        NodeData node2 = station2.getValue();
        if (node1 == node2) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to create a connection between the same stations.");
            alert.show();
            return;
        }
        if (node1 instanceof HubData && node2 instanceof HubData) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to create a connection between two hubs.");
            alert.show();
            return;
        }

        try {
            int distance = Integer.parseInt(this.distance.textProperty().get());
            if (distance <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Distances must be positive");
                alert.show();
                return;
            }
            int start = restrictionStartTime.getValue();
            int end = restrictionEndTime.getValue();
            if (start > end) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Start Time must be larger than end time.");
                alert.show();
                return;
            }
            EdgeData edge = new EdgeData(node1, node2, distance, start, end);
            Model.getInstance().getSimulation().getRails().add(edge);
            rails.getItems().add(edge);


        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to parse distance");
            alert.show();
        }

    }

    @FXML
    public void contextmenuDelete(ActionEvent event) {
        List<EdgeData> d = new ArrayList<>(rails.getSelectionModel().getSelectedItems());
//TODO: KEEP ONE RAIL, DELETE THE REST
        rails.getSelectionModel().clearSelection();
        rails.getItems().removeAll(d);
        d.forEach(x -> {
            if (!x.isUsed()) {
                Model.getInstance().getSimulation().getRails().remove(x);
            } else {
                x.disable();
            }
        });

    }



    private void populateNodeBoxes() {
        station1.getItems().addAll(Model.getInstance().getSimulation().getHubs());
        station1.getItems().addAll(Model.getInstance().getSimulation().getStations());
        station2.getItems().addAll(Model.getInstance().getSimulation().getHubs());
        station2.getItems().addAll(Model.getInstance().getSimulation().getStations());
        if (!station1.getItems().isEmpty())
            station1.getSelectionModel().select(0);
        if (!station2.getItems().isEmpty())
            station2.getSelectionModel().select(0);
    }

    private void populateTimeBoxes() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 5) {
                restrictionStartTime.getItems().add((i * 60) + j);
                restrictionEndTime.getItems().add((i * 60) + j);
            }

        }
        restrictionStartTime.setConverter(new MinutesToTimeFormatter());
        restrictionStartTime.getSelectionModel().select(0);
        restrictionEndTime.setConverter(new MinutesToTimeFormatter());
        restrictionEndTime.getSelectionModel().select(0);

    }

    private void populateRails() {


        rails.getItems().addAll(Model.getInstance().getSimulation().getRails());
    }

}
