package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class holaMundoController {

    @FXML
    private Label labelMensaje;


    @FXML
    void click(ActionEvent event) {
        labelMensaje.setText("Hola Mundo");
    }

}
