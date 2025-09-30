package controllers;
import javafx.fxml.FXML;
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
    private void abrirListarTorneos(MouseEvent event) {
        Stage stage = (Stage) listarTorneosView.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "Listar Torneos");
    }
}
