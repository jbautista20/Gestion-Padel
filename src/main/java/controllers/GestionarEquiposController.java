package controllers;

import DAO.impl.EquipoDAOImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import models.Equipo;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestionarEquiposController {

    @FXML
    private ImageView botonBack;

    @FXML
    private Pane btnDescalificarEquipo;

    @FXML
    private TableColumn<Equipo, String> colNombre;

    @FXML
    private TableColumn<Equipo, String> colFechaInscripcion;

    @FXML
    private TableColumn<Equipo, String> colFechaDesc;

    @FXML
    private TableColumn<Equipo, String> colMotivo;

    @FXML
    private TableColumn<Equipo, Integer> colPuntos;

    @FXML
    private TableColumn<Equipo, String> colDescalificado;

    @FXML
    private TableView<Equipo> tableEquipos;

    private Torneo torneoActual;
    private final EquipoDAOImpl equipoDAO = new EquipoDAOImpl();
    private final ObservableList<Equipo> listaEquipos = FXCollections.observableArrayList();

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

        configurarColumnas();
        cargarEquiposDesdeBD();
        tableEquipos.setItems(listaEquipos);
    }

    private void configurarColumnas() {
        // Usar nombres exactos de atributos del modelo (en minúscula)
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        colMotivo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("motivo_desc"));
        colPuntos.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("ptos_T_Obt"));

        // Mostrar "Sí" o "No" según si está descalificado
        colDescalificado.setCellValueFactory(equipo -> {
            boolean descalificado = equipo.getValue().getMotivo_desc() != null && !equipo.getValue().getMotivo_desc().isEmpty();
            return new SimpleStringProperty(descalificado ? "Sí" : "No");
        });

        // Formatear fecha de inscripción
        colFechaInscripcion.setCellValueFactory(equipo -> {
            LocalDate fecha = equipo.getValue().getFecha_Insc();
            String fechaStr = (fecha != null)
                    ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "";
            return new SimpleStringProperty(fechaStr);
        });

        // Formatear fecha de descalificación (si existe)
        colFechaDesc.setCellValueFactory(equipo -> {
            LocalDate fechaDesc = equipo.getValue().getFecha_desc();
            String fechaStr = (fechaDesc != null)
                    ? fechaDesc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "";
            return new SimpleStringProperty(fechaStr);
        });
    }

    private void cargarEquiposDesdeBD() {
        listaEquipos.clear();
        listaEquipos.addAll(equipoDAO.findByTorneoId(torneoActual.getId()));
    }

    @FXML
    private void botonInscribirEquipo(MouseEvent event) {
        event.consume();
        Stage stage = (Stage) btnDescalificarEquipo.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaInscribirEquipo, "Inscribir Equipo", torneoActual);
        System.out.println("Cambiando la ventana a inscripción de equipo");
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) btnDescalificarEquipo.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaGestionarTorneos, "Gestionar Torneo", torneoActual);
        System.out.println("Volviendo a la gestión de torneos");
    }
}