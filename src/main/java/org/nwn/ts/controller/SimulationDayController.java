package org.nwn.ts.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.nwn.ts.stats.*;


import java.net.URL;
import java.util.ResourceBundle;

public class SimulationDayController implements Initializable {
    private SimpleObjectProperty<SimulationDay> targetDay = new SimpleObjectProperty<>();
    public static final String SIMULATION_DAY_PROPERTY = "SIMULATION_DAY_PROPERTY";
    @FXML
    Parent container;
    @FXML
    private TableView<TrainMetricHolder> trainMetrics;

    @FXML
    private TableView<StationMetricHolder> stationMetrics;

    @FXML
    private TableView<TrackMetricHolder> trackMetrics;

    @FXML
    private TableView<HubMetricHolder> hubMetrics;
    @FXML
    private Label dayLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trainMetrics.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        stationMetrics.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        trackMetrics.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        hubMetrics.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        initFields(targetDay.getValue());

        targetDay.addListener((observable, oldValue, newValue) -> {
            initFields(newValue);


        });
    }

    private void initFields(SimulationDay newValue) {
        trainMetrics.getItems().clear();
        trackMetrics.getItems().clear();
        stationMetrics.getItems().clear();
        hubMetrics.getItems().clear();

        if (newValue != null) {
            newValue.getMetrics().forEach(metric -> {
                if (metric instanceof TrainMetricHolder) {
                    trainMetrics.getItems().add((TrainMetricHolder) metric);
                } else if (metric instanceof TrackMetricHolder) {
                    trackMetrics.getItems().add((TrackMetricHolder) metric);
                } else if (metric instanceof StationMetricHolder) {
                    stationMetrics.getItems().add((StationMetricHolder) metric);
                } else if (metric instanceof HubMetricHolder) {
                    hubMetrics.getItems().add((HubMetricHolder) metric);
                }
            });
            container.getProperties().put(SIMULATION_DAY_PROPERTY, newValue);
        }


    }

    public SimulationDay getTargetDay() {
        return targetDay.get();
    }

    public SimpleObjectProperty<SimulationDay> targetDayProperty() {
        return targetDay;
    }

    public void setTargetDay(SimulationDay targetDay) {
        this.targetDay.set(targetDay);
    }
}
