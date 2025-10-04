package controllers;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utilities.NavigationHelper;
import utilities.Paths;
public class MenuPrincipalController {
    @FXML
    private Pane listarJugadoresView;
    @FXML
    private Pane listarTorneosView;
    @FXML
    private ImageView botonBack;

    //----------------------------Abrir Scene Listar Torneo------------------------------//
    @FXML
    private void abrirListarTorneos(MouseEvent event) {
        Stage stage = (Stage) listarTorneosView.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "Listar Torneos");
    }


    //----------------------------Abrir Scene Historial de Reservas------------------------------//
    @FXML
    void abrirHistorialReservas(MouseEvent event) {
        Stage stage = (Stage) listarTorneosView.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaHistorialReservas, "Historial de Reservas");
    }

    //----------------------------Funcionalidad Boton Back-------------------------------//
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaUno, "ListarTorneos");
        System.out.println("Volviendo al menú principal");

    }
}
