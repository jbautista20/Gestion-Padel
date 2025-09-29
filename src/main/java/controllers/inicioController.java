package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utilities.NavigationHelper;
import utilities.Paths;
import javafx.scene.input.MouseEvent;
public class inicioController {

    @FXML
    private ImageView botonIngresar;
//----------------------------------------CAMBIO DE ESCENA A MENU PRINCIPAL---------------------------------------
    @FXML
    private void abrirMenuPrincipal() {
        Stage stage = (Stage) botonIngresar.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaMenuPrincipal, "Menú Principal");
    }


}
