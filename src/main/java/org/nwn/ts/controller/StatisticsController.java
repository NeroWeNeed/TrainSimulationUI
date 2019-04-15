package org.nwn.ts.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.nwn.ts.Model;
import org.nwn.ts.stats.MetricHolder;
import org.nwn.ts.stats.SimulationDay;

import java.net.URL;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    @FXML
    private Parent container;

    @FXML
    private ListView<SimulationDay> simulationDaysListView;

    @FXML
    private VBox statContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationDaysListView.itemsProperty().bind(Model.getInstance().simulationDaysProperty());


    }
}
