package org.nwn.ts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.nwn.ts.controller.RootController;

import java.net.URL;

public class Launcher extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(RootController.FXML_LOCATION);
        Scene scene = new Scene(root);
        DataProvider.generateHubs(100);
        DataProvider.generateStations(100, 50, 10);
        DataProvider.generateTrains(200);
        DataProvider.generateRails(300);
        DataProvider.generateSimulationDays(14);
        primaryStage.setScene(scene);

        primaryStage.show();

    }

    public static void main(String[] args) {
       launch(args);


    }


}

