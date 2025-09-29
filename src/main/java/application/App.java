package application;

import db.Conexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utilities.NavigationHelper;
import utilities.Paths;

import java.sql.SQLOutput;

public class App extends Application {


    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) {
        Conexion.getConexion();
        // Pantalla inicial
        NavigationHelper.cambiarVista(stage, Paths.pantallaUno, "INICIO");
        stage.show();
    }
}