package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Equipo;
import models.Es;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;
import javafx.scene.control.ButtonType;

import java.util.*;

public class GestionarTorneoController {

    @FXML
    private ImageView botonBack;
    @FXML
    private Text txtEquipoCampeon;
    @FXML
    private Text txtEquipoCuartos1;
    @FXML
    private Text txtEquipoCuartos2;
    @FXML
    private Text txtEquipoCuartos3;
    @FXML
    private Text txtEquipoCuartos4;
    @FXML
    private Text txtEquipoCuartos5;
    @FXML
    private Text txtEquipoCuartos6;
    @FXML
    private Text txtEquipoCuartos7;
    @FXML
    private Text txtEquipoCuartos8;
    @FXML
    private Text txtEquipoFinal1;
    @FXML
    private Text txtEquipoFinal2;
    @FXML
    private Text txtEquipoSemi1;
    @FXML
    private Text txtEquipoSemi2;
    @FXML
    private Text txtEquipoSemi3;
    @FXML
    private Text txtEquipoSemi4;
    @FXML
    private Text txtMarcadorCuartos1;
    @FXML
    private Text txtMarcadorCuartos2;
    @FXML
    private Text txtMarcadorCuartos3;
    @FXML
    private Text txtMarcadorCuartos4;
    @FXML
    private Text txtMarcadorCuartos5;
    @FXML
    private Text txtMarcadorCuartos6;
    @FXML
    private Text txtMarcadorCuartos7;
    @FXML
    private Text txtMarcadorCuartos8;
    @FXML
    private Text txtMarcadorFinal1;
    @FXML
    private Text txtMarcadorFinal2;
    @FXML
    private Text txtMarcadorSemi1;
    @FXML
    private Text txtMarcadorSemi2;
    @FXML
    private Text txtMarcadorSemi3;
    @FXML
    private Text txtMarcadorSemi4;
    @FXML
    private Label txtFechaInicio;
    @FXML
    private Label txtGenero;
    @FXML
    private Label txtCantidadInscriptos;
    @FXML
    private Label txtCategoria;
    @FXML
    private Pane btnArmarCruces;
    @FXML
    private Pane btnComenzarTorneo;

    private int totalInscriptos;
    private boolean crucesArmados = false; // variable local para el control

    private Torneo torneoActual;

    @FXML
    private void initialize() {
        Object datos = NavigationHelper.getDatos();
        if (datos instanceof Torneo) {
            torneoActual = (Torneo) datos;
            NavigationHelper.clearDatos();

            // Si el torneo ya está en curso, no se pueden armar cruces nuevamente
            if (torneoActual.getEstados() == Es.En_Curso) {
                crucesArmados = true; // ya está implícito
            }
        } else {
            System.err.println("No se recibieron datos válidos del torneo en NavigationHelper.");
        }

        // Asignamos eventos a los Pane
        btnArmarCruces.setOnMouseClicked(event -> botonArmarCruces());
        btnComenzarTorneo.setOnMouseClicked(event -> botonComenzarTorneo());
    }

    @FXML
    void botonComenzarTorneo() {
        if (!crucesArmados) {
            mostrarAlerta("No se puede comenzar el torneo",
                    "Primero debes armar los cruces antes de comenzar el torneo.");
            return;
        }

        // Confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar inicio de torneo");
        alert.setHeaderText("Comenzar Torneo");
        alert.setContentText("Una vez comenzado el torneo quedará inhabilitado el armado de cruces. ¿Desea continuar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            torneoActual.setEstados(Es.En_Curso);
        }
    }

    @FXML
    private void botonArmarCruces() {
        totalInscriptos = contarEquipos();

        if (totalInscriptos < 8) {
            mostrarAlerta("No se pueden armar los cruces",
                    "Se necesitan 8 equipos inscriptos para armar los cruces.");
            return;
        }

        crucesArmados = true;

        // Lista base de nombres de equipos (ejemplo)
        List<String> equiposEjemplo = new ArrayList<>(Arrays.asList(
                "RiffoMontenegro",
                "RioRiffo",
                "MontenegroRio",
                "GallardoBenavidez",
                "CavaniBorja",
                "GrossoAlume",
                "GuzmanSosa",
                "BernaldezNodar"
        ));

        // Mezclar al azar
        Collections.shuffle(equiposEjemplo);

        // Asignar en orden a los cuartos
        txtEquipoCuartos1.setText(equiposEjemplo.get(0));
        txtEquipoCuartos2.setText(equiposEjemplo.get(1));
        txtEquipoCuartos3.setText(equiposEjemplo.get(2));
        txtEquipoCuartos4.setText(equiposEjemplo.get(3));
        txtEquipoCuartos5.setText(equiposEjemplo.get(4));
        txtEquipoCuartos6.setText(equiposEjemplo.get(5));
        txtEquipoCuartos7.setText(equiposEjemplo.get(6));
        txtEquipoCuartos8.setText(equiposEjemplo.get(7));

        // Mostrar estructura de rondas
        txtEquipoSemi1.setText("Ganador C1");
        txtEquipoSemi2.setText("Ganador C2");
        txtEquipoSemi3.setText("Ganador C3");
        txtEquipoSemi4.setText("Ganador C4");

        txtEquipoFinal1.setText("Ganador S1");
        txtEquipoFinal2.setText("Ganador S2");

        mostrarInfo("Cruces armados", "Los equipos fueron asignados de forma aleatoria.");
    }


    @FXML
    private void botonGestionarEquipos(MouseEvent event) {
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }

    // Contar equipos no nulos
    private int contarEquipos() {
        int count = 0;
        for (Equipo e : torneoActual.getEquipos()) {
            if (e != null) count++;
        }
        return count+8;
    }

    // Métodos de ayuda para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
