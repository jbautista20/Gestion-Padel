package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class inicioController {


    @FXML
    private ImageView botonIngresar;
    @FXML
    private void abrirMenuPrincipal() {
        try {
            //obtiene la ventana actual
            Stage stageActual = (Stage) botonIngresar.getScene().getWindow();
            //carga el fxml de menuPrincipal.xml
            FXMLLoader cargarfxml = new FXMLLoader(getClass().getResource("/Views/menuPrincipal.fxml"));
            //carga el fxml y le da la raiz
            Parent raiz = cargarfxml.load();
            //crea una nueva escena con la raiz de menuPrincipal.
            Scene scene = new Scene(raiz);
            //crea una nueva venrana vacia
            Stage stage = new Stage();
            stage.setTitle("Menú Principal");
            stage.setScene(scene);
        //hace visible la ventana
            stage.show();
            //cierra la ventana de inicio
            stageActual.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar el menú principal");
        }
    }
}
