package controllers;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utilities.NavigationHelper;
import utilities.Paths;
public class ListarTorneoController {
    @FXML
    private Pane gestionarTorneo;
    @FXML
    private void abrirGestionarTorneo(MouseEvent event) {
        Stage stage = (Stage) gestionarTorneo.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaGestionarTorneos, "GestionarTorneo");
        System.out.println("cambiando la ventana");
    }
}
