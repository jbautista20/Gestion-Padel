package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class inicioController {

    @FXML
    private Label labelMensaje;

    @FXML
    void clickInicio(ActionEvent event) {
        labelMensaje.setText("full carita humilde");
    }
}
