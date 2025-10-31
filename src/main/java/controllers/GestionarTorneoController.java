package controllers;

import DAO.impl.EquipoDAOImpl;
import DAO.impl.PartidoDAOImpl;
import DAO.impl.TorneoDAOImpl;
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
import models.Partido;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;
import javafx.scene.control.ButtonType;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestionarTorneoController {

    @FXML private ImageView botonBack;
    @FXML private Text txtEquipoCampeon;
    @FXML private Text txtEquipoCuartos1, txtEquipoCuartos2, txtEquipoCuartos3, txtEquipoCuartos4,
            txtEquipoCuartos5, txtEquipoCuartos6, txtEquipoCuartos7, txtEquipoCuartos8;
    @FXML private Text txtEquipoSemi1, txtEquipoSemi2, txtEquipoSemi3, txtEquipoSemi4;
    @FXML private Text txtEquipoFinal1, txtEquipoFinal2;
    @FXML private Label txtFechaInicio, txtGenero, txtCantidadInscriptos, txtCategoria;
    @FXML private Pane btnArmarCruces, btnComenzarTorneo;

    private int totalInscriptos;
    private boolean crucesArmados = false;
    private Torneo torneoActual;

    // DAOs
    private final TorneoDAOImpl torneoDAO = new TorneoDAOImpl();
    private final EquipoDAOImpl equipoDAO = new EquipoDAOImpl();
    private final PartidoDAOImpl partidoDAO = new PartidoDAOImpl();

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

        // Cargar datos del torneo
        cargarDatosTorneo();

        // Ver si ya existen partidos (cruces armados previamente)
        List<Partido> partidos = partidoDAO.obtenerCrucesPorTorneo(torneoActual.getId());
        if (partidos != null && !partidos.isEmpty()) {
            crucesArmados = true;
            // Ahora llamar a la versión que acepta el id del torneo (que internamente
            // obtendrá los partidos desde el DAO y los mostrará)
            mostrarCrucesDesdeBD(torneoActual.getId());
        } else {
            // No hay partidos: la vista queda en estado "sin cruces"
            mostrarCrucesDesdeBD(torneoActual.getId());
        }

        // Eventos
        btnArmarCruces.setOnMouseClicked(event -> botonArmarCruces());
        btnComenzarTorneo.setOnMouseClicked(event -> botonComenzarTorneo());
    }


    /** Cargar la información general del torneo en pantalla */
    private void cargarDatosTorneo() {
        txtFechaInicio.setText(torneoActual.getFecha().toString());
        txtCategoria.setText("Cat: "+String.valueOf(torneoActual.getCategoria()) + "°");
        txtGenero.setText(torneoActual.getTipo().toString());

        totalInscriptos = equipoDAO.contarEquiposPorTorneo(torneoActual.getId());
        txtCantidadInscriptos.setText(String.valueOf(totalInscriptos));
    }


    /** Manejo de botón Comenzar Torneo */
    @FXML
    private void botonComenzarTorneo() {
        if (!crucesArmados) {
            mostrarAlerta("No se puede comenzar el torneo",
                    "Primero debes armar los cruces antes de comenzar el torneo.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar inicio de torneo");
        alert.setHeaderText("Comenzar Torneo");
        alert.setContentText("Una vez comenzado el torneo quedará inhabilitado el armado de cruces. ¿Desea continuar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            torneoActual.setEstados(Es.En_Curso);
            torneoDAO.update(torneoActual);
            mostrarInfo("Torneo iniciado", "El torneo ha comenzado correctamente.");
        }
    }

    /** Manejo de botón Armar Cruces */
    @FXML
    private void botonArmarCruces() {
        List<Equipo> equipos = equipoDAO.findByTorneoId(torneoActual.getId());

        totalInscriptos = equipos.size();

        if (totalInscriptos < 8) {
            mostrarAlerta("No se pueden armar los cruces",
                    "Se necesitan 8 equipos inscriptos para armar los cruces.");
            return;
        }

        crucesArmados = true;

        // Mezclar aleatoriamente los equipos
        Collections.shuffle(equipos);

        // Crear los 4 partidos de cuartos de final y guardarlos
        for (int i = 0; i < 8; i += 2) {
            Equipo eq1 = equipos.get(i);
            Equipo eq2 = equipos.get(i + 1);

            Partido partido = new Partido();
            partido.setHora(LocalTime.of(0, 0));
            partido.setInstancia(1); // 1 = Cuartos
            partido.setPuntos(0);
            partido.setEquipo1(eq1);
            partido.setEquipo2(eq2);
            partido.setTorneo(torneoActual);

            partidoDAO.create(partido);
        }

        // Refrescar visualmente
        mostrarCrucesDesdeBD(torneoActual.getId());

        mostrarInfo("Cruces armados", "Los cruces fueron generados y guardados correctamente.");
    }

    /** Muestra los cruces (nombres) obtenidos desde la BD */
    private void mostrarCrucesDesdeBD(int idTorneo) {
        List<Partido> partidos = partidoDAO.obtenerCrucesPorTorneo(idTorneo);

        List<Text> textosCuartos = Arrays.asList(
                txtEquipoCuartos1, txtEquipoCuartos2, txtEquipoCuartos3, txtEquipoCuartos4,
                txtEquipoCuartos5, txtEquipoCuartos6, txtEquipoCuartos7, txtEquipoCuartos8
        );

        int index = 0;
        for (Partido p : partidos.stream().filter(x -> x.getInstancia() == 1).toList()) {
            if (index + 1 < textosCuartos.size()) {
                textosCuartos.get(index).setText(
                        p.getEquipo1() != null && p.getEquipo1().getNombre() != null
                                ? p.getEquipo1().getNombre() : "-"
                );
                textosCuartos.get(index + 1).setText(
                        p.getEquipo2() != null && p.getEquipo2().getNombre() != null
                                ? p.getEquipo2().getNombre() : "-"
                );
            }
            index += 2;
        }

        // Mostrar estructura base
        txtEquipoSemi1.setText("Ganador C1");
        txtEquipoSemi2.setText("Ganador C2");
        txtEquipoSemi3.setText("Ganador C3");
        txtEquipoSemi4.setText("Ganador C4");
        txtEquipoFinal1.setText("Ganador S1");
        txtEquipoFinal2.setText("Ganador S2");
    }


    /** Devuelve los partidos asociados al torneo */
    private List<Partido> obtenerPartidosDelTorneo(int idTorneo) {
        return partidoDAO.obtenerPartidosPorTorneo(idTorneo);
    }


    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }

    // ==== Métodos de ayuda ====

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

    @FXML
    private void botonGestionarEquipos() {
        Stage stage = (Stage) btnComenzarTorneo.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaGestionarEquipos, "Gestionar Equipos", torneoActual);
        System.out.println("cambiando la ventana");
    }

}