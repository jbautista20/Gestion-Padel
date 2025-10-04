package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utilities.NavigationHelper;
import utilities.Paths;

public class HistorialReservasController {

    @FXML
    private ImageView botonBack;

    @FXML
    private Pane listarJugadoresView;

    @FXML
    private Pane listarTorneosView;

    @FXML
    private TableView<?> tableReservas;

    @FXML
    void abrirListarTorneos(MouseEvent event) {

    }

    @FXML
    void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaMenuPrincipal, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }

}
