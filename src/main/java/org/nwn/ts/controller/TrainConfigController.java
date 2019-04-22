package org.nwn.ts.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.nwn.ts.stats.TrainType;

import java.net.URL;
import java.util.ResourceBundle;

public class TrainConfigController implements Initializable {

    private TrainType type;
    @FXML
    private Label name;

    @FXML
    private TextField fuelCapacity;

    @FXML
    private TextField fuelCost;

    @FXML
    private TextField speed;

    @FXML
    private TextField capacity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public TrainType getType() {
        return type;
    }

    public void setType(TrainType type) {
        this.type = type;
    }

    public Label getName() {
        return name;
    }

    public void setName(Label name) {
        this.name = name;
    }

    public TextField getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(TextField fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public TextField getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(TextField fuelCost) {
        this.fuelCost = fuelCost;
    }

    public TextField getSpeed() {
        return speed;
    }

    public void setSpeed(TextField speed) {
        this.speed = speed;
    }

    public TextField getCapacity() {
        return capacity;
    }

    public void setCapacity(TextField capacity) {
        this.capacity = capacity;
    }

}
