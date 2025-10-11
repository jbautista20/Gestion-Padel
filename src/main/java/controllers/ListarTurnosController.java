package controllers;

import DAO.impl.CanchaDAOImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import models.Cancha;
import models.Turno;
import models.E;
import utilities.NavigationHelper;
import utilities.Paths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ListarTurnosController {

    @FXML private TableView<Turno> tablaTurnos;
    @FXML private TableColumn<Turno, LocalTime> columnaHora;
    @FXML private TableColumn<Turno, String> columnaEstado;
    @FXML private TableColumn<Turno, String> columnaCancha;
    @FXML private ImageView botonBack;
    @FXML private Pane botonCrearReserva;
    @FXML private DatePicker DatePickerFecha;
    @FXML private Label labelTurnoSeleccionado;

    private TurnoDAOImpl turnoDAO = new TurnoDAOImpl();
    private ObservableList<Turno> listaTurnos = FXCollections.observableArrayList();
    private ObservableList<Turno> todosLosTurnos = FXCollections.observableArrayList();
    private boolean mostrandoAlerta = false;

    @FXML
    public void initialize() {
        configurarTextoenTabla();
        configurarColumnas();
        configurarListeners();

        tablaTurnos.setItems(listaTurnos);
        cargarTodosLosTurnos();

        // Mostrar turno seleccionado en el label
        tablaTurnos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null)
                mostrarTurnoSeleccionado(newSel);
            else
                labelTurnoSeleccionado.setText("No hay turno seleccionado");
        });

        DatePickerFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate today = LocalDate.now();
                LocalDate maxDate = today.plusMonths(2);

                // Deshabilita fechas fuera del rango permitido
                setDisable(empty || date.isBefore(today) || date.isAfter(maxDate));

                // Estilo visual para las fechas no disponibles
                if (isDisable()) {
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });

// Valor inicial en hoy
        DatePickerFecha.setValue(LocalDate.now());

    }

    // Configura columnas de la tabla
    private void configurarColumnas() {
        columnaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        columnaCancha.setCellValueFactory(cellData -> {
            Turno turno = cellData.getValue();
            String numeroCancha = (turno.getCancha() != null)
                    ? String.valueOf(turno.getCancha().getNumero())
                    : "-";
            return new SimpleStringProperty(numeroCancha);
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
        // Cuando cambia la fecha del DatePicker
        DatePickerFecha.valueProperty().addListener((obs, oldDate, newDate) -> aplicarFiltros(newDate));
    }

    private void cargarTodosLosTurnos() {
        try {
            todosLosTurnos.setAll(turnoDAO.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aplicarFiltros(LocalDate fecha) {
        if (fecha == null) {
            listaTurnos.clear();
            return;
        }

        // Filtrar turnos existentes para esa fecha
        List<Turno> filtrados = todosLosTurnos.stream()
                .filter(t -> fecha.equals(t.getFecha()))
                .collect(Collectors.toList());

        // Si no hay turnos, generarlos automáticamente
        if (filtrados.isEmpty()) {
            generarTurnosDelDia(fecha);
            // Volver a cargar los turnos desde BD
            cargarTodosLosTurnos();
            // Filtrar nuevamente
            filtrados = todosLosTurnos.stream()
                    .filter(t -> fecha.equals(t.getFecha()))
                    .collect(Collectors.toList());
        }

        listaTurnos.setAll(filtrados);
    }

    private void generarTurnosDelDia(LocalDate fecha) {
        LocalTime horaInicio = LocalTime.of(12, 0);
        LocalTime horaFin = LocalTime.of(22, 0);

        // Obtener todas las canchas de la base de datos
        CanchaDAOImpl canchaDAO = new CanchaDAOImpl();
        List<Cancha> canchas = canchaDAO.findAll();

        for (Cancha cancha : canchas) {
            for (LocalTime hora = horaInicio; hora.isBefore(horaFin); hora = hora.plusHours(2)) {
                Turno turno = new Turno();
                turno.setFecha(fecha);
                turno.setHora(hora);
                turno.setEstado(E.Libre);
                turno.setCancha(cancha);
                turno.setPago(0);
                turno.setPersona(null); // null hasta que se reserve

                turnoDAO.create(turno);
            }
        }

        System.out.println("Turnos generados para " + fecha);
    }

    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) botonBack.getScene().getWindow();
            NavigationHelper.cambiarVista(stage, Paths.pantallaMenuPrincipal, "Menú Principal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCrearReserva() {
        Turno turnoSeleccionado = tablaTurnos.getSelectionModel().getSelectedItem();

        if (turnoSeleccionado == null) {
            mostrarAlertaError("Selección requerida", "Seleccione un turno para crear una reserva.");
            return;
        }

        if (turnoSeleccionado.getEstado() != E.Libre) {
            mostrarAlertaError("Turno no disponible",
                    "El turno seleccionado no está disponible.\nEstado actual: " + turnoSeleccionado.getEstado());
            return;
        }

        try {
            Stage stage = (Stage) botonCrearReserva.getScene().getWindow();
            NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaCrearReserva, "Crear Reserva", turnoSeleccionado);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "Ocurrió un error al abrir la pantalla de reserva.");
        }
    }

    private void mostrarTurnoSeleccionado(Turno turno) {
        String fecha = (turno.getFecha() != null) ? turno.getFecha().toString() : "-";
        String hora = (turno.getHora() != null) ? turno.getHora().toString() : "-";
        String cancha = (turno.getCancha() != null) ? String.valueOf(turno.getCancha().getNumero()) : "-";
        labelTurnoSeleccionado.setText("Turno seleccionado:\nFecha: " + fecha + "\nHora: " + hora + "\nCancha: " + cancha);
    }

    // ----- Alertas -----
    private void mostrarAlertaSinResultados(LocalDate fecha) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sin resultados");
        alert.setHeaderText(null);
        alert.setContentText("No se encontraron turnos en la fecha " + fecha);
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