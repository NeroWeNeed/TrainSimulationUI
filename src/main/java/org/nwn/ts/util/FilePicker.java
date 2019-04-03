package org.nwn.ts.util;

import javafx.beans.NamedArg;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.io.File;


public class FilePicker extends HBox {


    private TextField textField = new TextField();
    private Button button = new Button("...");
    private ObjectProperty<File> valueProperty = new SimpleObjectProperty<>();

    public FilePicker() {
        super();
        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox.setHgrow(button, Priority.NEVER);

        textField.setStyle("-fx-background-radius:3px 0px 0px 3px;");
        button.setStyle("-fx-background-radius:0px 3px 3px 0px;");

        getChildren().add(textField);
        getChildren().add(button);
        button.setOnAction(event -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            valueProperty.set(fileChooser.showOpenDialog(getScene().getWindow()));
        });

        textField.textProperty().bind(Bindings.when(valueProperty.isNull()).then("").otherwise(valueProperty.asString()));


    }


    public TextField getTextField() {
        return textField;
    }


    public Button getButton() {
        return button;
    }

    public File getValue() {
        return valueProperty.get();
    }

    public ObjectProperty<File> getValueProperty() {
        return valueProperty;
    }

    public String getPromptText() {
        return textField.getPromptText();
    }

    public StringProperty promptTextProperty() {
        return textField.promptTextProperty();
    }

    public void setPromptText(String value) {
        textField.setPromptText(value);
    }


}
