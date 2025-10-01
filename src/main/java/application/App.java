package application;

import DAO.TorneoDAO;
import DAO.impl.TorneoDAOImpl;
import db.Conexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.Es;
import models.T;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.List;

public class App extends Application {

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) {
        NavigationHelper.cambiarVista(stage, Paths.pantallaUno, "INICIO");
        stage.show();
    }
}