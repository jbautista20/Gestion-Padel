package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

public class GestionarEquiposController {

    @FXML
    private ImageView botonBack;

    @FXML
    private Pane btnDescalificarEquipo;

    @FXML
    private TableColumn<?, ?> colDescalificado;

    @FXML
    private TableColumn<?, ?> colFechaInscripcion;

    @FXML
    private TableColumn<?, ?> colMotivo;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colPuntos;

    @FXML
    private TableView<?> tableEquipos;

    private Torneo torneoActual;

    @FXML
    private void initialize() {
        Object datos = NavigationHelper.getDatos();
        if (datos instanceof Torneo) {
            torneoActual = (Torneo) datos;
            NavigationHelper.clearDatos();
        } else {
            System.err.println("No se recibieron datos válidos del torneo en NavigationHelper.");
            return;
        }
    }

    @FXML
    void botonInscribirEquipo(MouseEvent event) {
        event.consume();
        Stage stage = (Stage) btnDescalificarEquipo.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaInscribirEquipo, "Inscribir Equipo", torneoActual);
        System.out.println("cambiando la ventana");
    }

    @FXML
    void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) btnDescalificarEquipo.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaGestionarTorneos, "Gestionar Torneo", torneoActual);
        System.out.println("cambiando la ventana");
    }
}
