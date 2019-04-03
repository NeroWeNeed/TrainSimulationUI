package org.nwn.ts.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import org.nwn.ts.stats.MetricHolder;

import java.net.URL;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    @FXML
    private Parent container;

    @FXML
    private ComboBox<MetricHolder> comboBox;

    @FXML
    private VBox statContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
