package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

public class InscribirEquipoController {

    @FXML
    private ImageView botonBack;

    @FXML
    private Pane btnConfirmar;

    @FXML
    private TableColumn<?, ?> colAnioNacimiento;

    @FXML
    private TableColumn<?, ?> colApellido;

    @FXML
    private TableColumn<?, ?> colCategoria;

    @FXML
    private TableColumn<?, ?> colDireccion;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colPuntos;

    @FXML
    private TableColumn<?, ?> colSexo;

    @FXML
    private TableColumn<?, ?> colTelefono;

    @FXML
    private TableView<?> tableJugadores;

    @FXML
    private Text txtCategoria;

    @FXML
    private Text txtJugador1;

    @FXML
    private Text txtJugador11;

    @FXML
    private Text txtJugador2;

    @FXML
    private Text txtNombreEquipo;

    @FXML
    private Text txtValorInscripcion;

    Torneo torneoActual;

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
    void botonConfirmarEquipo(MouseEvent event) {

    }

    @FXML
    void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaGestionarEquipos, "Gestionar Equipos", torneoActual);
        System.out.println("cambiando la ventana");
    }

}
