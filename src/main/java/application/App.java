package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utilities.Paths;

import java.sql.SQLOutput;

public class App extends Application {


    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        AnchorPane load = FXMLLoader.load(getClass().getResource(Paths.pantallaUno));
        Scene scene = new Scene(load);
        stage.setScene(scene);
        stage.show();
        System.out.println("EL PATA");
        System.out.println("ESTE PRINT DEBERIA GENERAR CONFLICTO");

        System.out.println("probando");

        //Comentario nuevo para verificar que git ande bien
    }

}
