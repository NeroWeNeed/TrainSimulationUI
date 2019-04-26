package org.nwn.ts.controller;

import com.sun.javafx.geom.Edge;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.nwn.ts.Launcher;
import org.nwn.ts.Model;
import org.nwn.ts.simulation.TrainSimulation;
import org.nwn.ts.simulation.data.EdgeData;
import org.nwn.ts.simulation.data.HubData;
import org.nwn.ts.stats.SimulationDay;
import org.nwn.ts.util.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RootController implements Initializable {

    public static URL FXML_LOCATION = Launcher.class.getResource("/fxml/root.fxml");
    public static URL FXML_SIMULATION_DAY_FORMAT_LOCATION = Launcher.class.getResource("/fxml/simulation-day.fxml");

    @FXML
    private Parent container;
    /*
     * Menu Items
     * */
    @FXML
    private MenuItem fileReset;
    @FXML
    private MenuItem editTrains;
    @FXML
    private MenuItem editRails;

    @FXML
    private MenuItem editStations;

    @FXML
    private MenuItem viewRecommendations;

    @FXML
    private VBox metrics;

    @FXML
    private ScrollPane metricsContainer;

    @FXML
    private ListView<SimulationDay> simulationDaysListView;

    @FXML
    private SimulationDayController simulationDayController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileReset.disableProperty().bind(Model.getInstance().metricsExistsProperty().not());
        editTrains.disableProperty().bind(Model.getInstance().metricsExistsProperty().not());
        editRails.disableProperty().bind(Model.getInstance().metricsExistsProperty().not());
        editStations.disableProperty().bind(Model.getInstance().metricsExistsProperty().not());
        viewRecommendations.disableProperty().bind(Model.getInstance().metricsExistsProperty().not());
        simulationDaysListView.itemsProperty().bind(Model.getInstance().metricsProperty());
        simulationDaysListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        simulationDaysListView.setOnMouseClicked(x -> {
            SimulationDay selected = simulationDaysListView.getSelectionModel().getSelectedItem();
            simulationDayController.setTargetDay(selected);

        });


    }


    /*
     * Menu Items
     * */
    @FXML
    public void menuFileStartSimulation(ActionEvent event) throws IOException {


        Parent root = FXMLLoader.load(ConfigController.FXML_LOCATION);
        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(container.getScene().getWindow());
        newStage.setScene(newScene);
        newStage.setResizable(false);
        newStage.showAndWait();
        System.out.println("HANDLED");

    }

    @FXML
    public void menuFileResetSimulation(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you would like to reset the simulation?");

        ButtonType result = alert.showAndWait().orElse(null);
        if (result == ButtonType.OK) {
            Model.getInstance().getMetrics().clear();
            simulationDayController.setTargetDay(null);
            Model.getInstance().setSimulation(new TrainSimulation());
        }
    }

    @FXML
    public void menuEditTrains(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(EditorTrainController.FXML_LOCATION);
        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(container.getScene().getWindow());
        newStage.setScene(newScene);
        newStage.setResizable(false);
        newStage.showAndWait();
        System.out.println("HANDLED");
    }

    @FXML
    public void menuEditRails(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(EditorRailwayController.FXML_LOCATION);
        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(container.getScene().getWindow());
        newStage.setScene(newScene);
        newStage.setResizable(false);
        newStage.showAndWait();
    }

    @FXML
    public void menuEditStations(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(EditorStationController.FXML_LOCATION);
        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(container.getScene().getWindow());
        newStage.setScene(newScene);
        newStage.setResizable(false);
        newStage.showAndWait();
    }

    @FXML
    public void viewRecommendations(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Some changes I don't know");
        alert.showAndWait();
    }


    @FXML
    public void viewTotalCost(ActionEvent event) {

        double totalPrice = (Model.getInstance().getSimulation().getHubs().size()*30.0+Model.getInstance().getSimulation().getTrains().size()*1.5);
        for (EdgeData d : Model.getInstance().getSimulation().getRails()) {

            totalPrice += d.getDistance()*1.5;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estimated Cost: " + NumberFormat.getCurrencyInstance().format(totalPrice*1000000));
        alert.showAndWait();
    }


    @FXML
    public void menuFileExportCSV(ActionEvent event) {
        //TODO: Export CSV
    }


}
