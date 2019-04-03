package org.nwn.ts.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nwn.ts.Launcher;
import org.nwn.ts.Model;
import org.nwn.ts.util.Configuration;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {

    public static URL FXML_LOCATION = Launcher.class.getResource("/fxml/root.fxml");
    @FXML
    private Parent container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        newStage.show();


    }

    @FXML
    public void menuFileResetSimulation(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you would like to reset the simulation?");

        ButtonType result = alert.showAndWait().get();
        if (result == ButtonType.OK) {
            System.out.println("PASSED");
            //TODO Other reset stuff
            Model.getInstance().setBaselineSet(false);
            Model.getInstance().setConfiguration(new Configuration());
        }
    }

    @FXML
    public void menuFileExportCSV(ActionEvent event) {
        //TODO: Export CSV
    }
}
