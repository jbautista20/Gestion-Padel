package application;

import javafx.application.Application;
import javafx.stage.Stage;
import utilities.NavigationHelper;
import utilities.Paths;

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