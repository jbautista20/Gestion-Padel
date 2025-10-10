package controllers;

import javafx.fxml.FXML;
import DAO.impl.TurnoDAOImpl;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.Turno;
import models.E;
import utilities.NavigationHelper;
import utilities.Paths;
import utilities.TurnoObserver;
import utilities.TurnoSubject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ListarTurnosController implements TurnoObserver {
    @FXML
    private TableView<Turno> tablaTurnos;
    @FXML
    private TableColumn<Turno, LocalTime> columnaHora;
    @FXML
    private TableColumn<Turno, String> columnaEstado;
    @FXML
    private TableColumn<Turno, String> columnaCancha;
    @FXML
    private ImageView botonBack;
    @FXML
    private Pane botonCrearReserva;
    @FXML
    private DatePicker DatePickerFecha;
    @FXML
    private Label labelTurnoSeleccionado;

    private TurnoDAOImpl turnoDAO = new TurnoDAOImpl();
    private TurnoSubject turnoSubject = new TurnoSubject();
    private List<Turno> todosLosTurnos;
    private boolean mostrandoAlerta = false;

    @FXML
    public void initialize() {
        configurarTextoenTabla();
        configurarColumnas();
        configurarListeners();
        turnoSubject.addObserver(this);
        cargarTodosLosTurnos();
        tablaTurnos.getItems().clear();

        tablaTurnos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarTurnoSeleccionado(newSelection);
            } else {
                labelTurnoSeleccionado.setText("No hay turno seleccionado");
            }
        });
    }

    //-----------------------MÉTODOS PARA TABLA TURNOS-----------------------------
    private void configurarColumnas() {
        columnaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Mostrar número de cancha en la columna
        columnaCancha.setCellValueFactory(cellData -> {
            Turno turno = cellData.getValue();
            if (turno.getCancha() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(turno.getCancha().getNumero())
                );
            } else {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
        });
    }

    private void configurarTextoenTabla() {
        Text placeholderText = new Text("Por favor, seleccione una fecha para poder ver turnos");
        placeholderText.setTextAlignment(TextAlignment.CENTER);
        placeholderText.setStyle("-fx-font-size: 14px; -fx-fill: #666;");

        VBox placeholderBox = new VBox(placeholderText);
        placeholderBox.setStyle("-fx-alignment: center; -fx-padding: 40px;");

        tablaTurnos.setPlaceholder(placeholderBox);
    }

    private void configurarListeners() {
        DatePickerFecha.valueProperty().addListener((observable, oldValue, newValue) -> {
            turnoSubject.setFecha(newValue);
        });
    }

    private void cargarTodosLosTurnos() {
        try {
            todosLosTurnos = turnoDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            todosLosTurnos = List.of();
        }
    }
    //-----------------------MÉTODOS PARA TABLA TURNOS-----------------------------

    //------------------------------FILTROS--------------------------
    @Override
    public void onFiltrosCambiados(String cancha, LocalDate fecha) {
        aplicarFiltros(fecha);
    }

    private void aplicarFiltros(LocalDate fecha) {
        boolean fechaSeleccionada = fecha != null;

        if (fechaSeleccionada) {
            List<Turno> turnosFiltrados = todosLosTurnos.stream()
                    .filter(turno -> filtrarPorFecha(turno, fecha))
                    .collect(Collectors.toList());

            tablaTurnos.getItems().setAll(turnosFiltrados);

            if (turnosFiltrados.isEmpty() && !mostrandoAlerta) {
                mostrandoAlerta = true;
                mostrarAlertaSinResultados();
                mostrandoAlerta = false;
            }
        } else {
            tablaTurnos.getItems().clear();
        }
    }

    private boolean filtrarPorFecha(Turno turno, LocalDate fecha) {
        if (turno.getFecha() == null) return false;
        return fecha.equals(turno.getFecha());
    }
    //------------------------------FILTROS--------------------------

    //-------------------BOTON PARA VOLVER--------------------------
    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) botonBack.getScene().getWindow();
            NavigationHelper.cambiarVista(stage, Paths.pantallaMenuPrincipal, "ListarTurnos");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------BOTON PARA VOLVER--------------------------

    //-------------------------BOTON CREAR RESERVA----------------------------------
    @FXML
    private void handleCrearReserva() {
        try {
            Turno turnoSeleccionado = tablaTurnos.getSelectionModel().getSelectedItem();
            if (turnoSeleccionado == null) {
                mostrarAlertaError("Selección requerida", "Por favor, seleccione un turno de la tabla para crear una reserva.");
                return;
            }
            if (turnoSeleccionado.getEstado() != E.Libre) {
                mostrarAlertaError("Turno no disponible", "El turno seleccionado no está disponible.\n" + "Estado actual: " + turnoSeleccionado.getEstado() + "\n\n" + "Por favor, seleccione un turno con estado 'LIBRE'.");
                return;
            }
            // Guardar turno seleccionado en el contexto global
            utilities.TurnoContext.setTurnoSeleccionado(turnoSeleccionado);
            // Ir a la pantalla CrearReserva
            Stage stage = (Stage) botonCrearReserva.getScene().getWindow();
            NavigationHelper.cambiarVista(stage, Paths.pantallaCrearReserva, "CrearReserva");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "Ocurrió un error al procesar la reserva: " + e.getMessage());
        }
    }
    //-------------------------BOTON CREAR RESERVA----------------------------------

    //----------------------PARA VER EL TURNO QUE SELECCIONO--------------------------------
    private void mostrarTurnoSeleccionado(Turno turno) {
        String fecha = turno.getFecha() != null ? turno.getFecha().toString() : "-";
        String hora = turno.getHora() != null ? turno.getHora().toString() : "-";
        String cancha = (turno.getCancha() != null) ? String.valueOf(turno.getCancha().getNumero()) : "-";

        labelTurnoSeleccionado.setText("Turno seleccionado: \nFecha: " + fecha + " \nHora: " + hora + " \nCancha: " + cancha);
    }
    //----------------------PARA VER EL TURNO QUE SELECCIONO--------------------------------

    //--------------------------ALERTAS-----------------------------------
    private void mostrarAlertaSinResultados() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sin resultados");
        alert.setHeaderText(null);
        alert.setContentText("No se encontraron turnos en la fecha " + DatePickerFecha.getValue());
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
//--------------------------ALERTAS-----------------------------------