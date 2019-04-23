package org.nwn.ts.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.nwn.ts.simulation.data.TrainConfigurationData;
import org.nwn.ts.simulation.data.TrainType;
import org.nwn.ts.util.FieldFormatter;
import org.nwn.ts.util.TrainConfigurationProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class TrainConfigController implements Initializable {


    private ObjectProperty<TrainType> type = new SimpleObjectProperty<>();
    private TrainConfigurationProperty configurationProperty = new TrainConfigurationProperty();
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

        name.textProperty().bind(typeProperty().asString());

        Bindings.bindBidirectional(fuelCapacity.textProperty(), configurationProperty.fuelCapacityProperty(), new FieldFormatter());
        Bindings.bindBidirectional(capacity.textProperty(), configurationProperty.capacityProperty(), new FieldFormatter());
        Bindings.bindBidirectional(speed.textProperty(), configurationProperty.speedProperty(), new FieldFormatter());
        Bindings.bindBidirectional(fuelCost.textProperty(), configurationProperty.fuelCostProperty(), new FieldFormatter());
    }

    public TrainType getType() {
        return type.get();
    }

    public ObjectProperty<TrainType> typeProperty() {
        return type;
    }

    public void setType(TrainType type) {
        this.type.set(type);
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

    public void set(TrainConfigurationData data) {
        configurationProperty.set(data);
    }


}
