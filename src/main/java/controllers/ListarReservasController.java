package controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.E;
import models.Turno;
import utilities.NavigationHelper;
import utilities.Paths;
import DAO.impl.TurnoDAOImpl;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ListarReservasController {
    @FXML private TableView<Turno> tablaReservas;
    @FXML private TableColumn<Turno, LocalDate> columnaFechaT;
    @FXML private TableColumn<Turno, String> columnaHora;
    @FXML private TableColumn<Turno, String> columnaCancha;
    @FXML private TableColumn<Turno, String> columnaPersona;
    @FXML private TableColumn<Turno, String> columnaFechaP;
    @FXML private DatePicker DatePickerFecha;
    @FXML private ImageView botonBack;
    @FXML private Pane botonGestionarPagos;
    private TurnoDAOImpl turnoDAO;

    @FXML
    public void initialize() {
        turnoDAO = new TurnoDAOImpl();

        configurarTextoenTabla();
        configurarColumnas();
        cargarTurnosOcupados();

        // Configurar DatePicker para permitir cualquier fecha
        DatePickerFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // No deshabilitar ninguna fecha
                setDisable(false);
            }
        });

        DatePickerFecha.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                cargarTurnosPorFecha(newDate);
            } else {
                cargarTurnosOcupados();
            }
        });
    }

    private void cargarTurnosOcupados(){
        try {
            TurnoDAOImpl turnoDAO = new TurnoDAOImpl();
            List<Turno> turnosOcupados = turnoDAO.obtenerTurnosPorEstado(E.Ocupado);

            tablaReservas.getItems().setAll(turnosOcupados);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las reservas: " + e.getMessage());
        }

    }

    private void cargarTurnosPorFecha(LocalDate fecha) {
        try {
            List<Turno> turnosOcupados = turnoDAO.obtenerTurnosPorEstadoYFecha(E.Ocupado, fecha);
            tablaReservas.getItems().setAll(turnosOcupados);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las reservas para la fecha seleccionada: " + e.getMessage());
        }
    }

    private void configurarTextoenTabla() {
        Text placeholderText = new Text("No hay reservas para esta fecha");
        placeholderText.setTextAlignment(TextAlignment.CENTER);
        placeholderText.setStyle("-fx-font-size: 14px; -fx-fill: #666;");

        VBox placeholderBox = new VBox(placeholderText);
        placeholderBox.setStyle("-fx-alignment: center; -fx-padding: 40px;");
        tablaReservas.setPlaceholder(placeholderBox);
    }

    private void configurarColumnas() {
        columnaFechaT.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getFecha()));
        columnaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

        columnaCancha.setCellValueFactory(cellData -> {
            Turno turno = cellData.getValue();
            String numeroCancha = (turno.getCancha() != null)
                    ? String.valueOf(turno.getCancha().getNumero())
                    : "-";
            return new SimpleStringProperty(numeroCancha);
        });

        columnaPersona.setCellValueFactory(cellData -> {
            Turno turno = cellData.getValue();
            String nombreCompleto = "-";
            if (turno.getPersona() != null) {
                String nombre = turno.getPersona().getNombre();
                String apellido = turno.getPersona().getApellido();
                nombreCompleto = nombre + " " + apellido;
            }
            return new SimpleStringProperty(nombreCompleto);
        });

        columnaFechaP.setCellValueFactory(cellData -> {
            Turno turno = cellData.getValue();
            if (turno.getFecha_Pago() != null) {
                String fechaPagoFormateada = turno.getFecha_Pago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return new SimpleStringProperty(fechaPagoFormateada);
            } else {
                return new SimpleStringProperty("No pagado");
            }
        });
    }


    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) botonBack.getScene().getWindow();
            NavigationHelper.cambiarVista(stage, Paths.pantallaTurnos, "Listar Turnos");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleCancelarReserva(){
        Turno turnoSeleccionado = tablaReservas.getSelectionModel().getSelectedItem();
        if (turnoSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar una Reserva para poder cancelarla.");
            return;
        }
        if (esReservaPasada(turnoSeleccionado)) {
            mostrarAlerta("Error", "No se puede cancelar una reserva que ya ha ocurrido.");
            return;
        }
        boolean confirmacion = mostrarConfirmacion("Confirmar Cancelación",
                "¿Está seguro de que quiere cancelar esta reserva?\n\n" +
                        "Fecha: " + turnoSeleccionado.getFecha() + "\n" +
                        "Hora: " + turnoSeleccionado.getHora() + "\n" +
                        "Cancha: " + (turnoSeleccionado.getCancha() != null ? turnoSeleccionado.getCancha().getNumero() : "\n")+
                        "\nPara: " + turnoSeleccionado.getPersona().getNombre() + " " + turnoSeleccionado.getPersona().getApellido() );

        if (confirmacion) {
            cancelarTurno(turnoSeleccionado);
            mostrarAlerta("Éxito", "Reserva cancelada correctamente.");
            DatePickerFecha.setValue(null);
            cargarTurnosOcupados();// Actualizar la tabla después de cancelar
        }


    }
    // se usa para ver si la reserva ya paso o no
    @FXML
    private boolean esReservaPasada(Turno turno){
        LocalDate fechaHoy = LocalDate.now();
        LocalTime horaAhora = LocalTime.now();
        if (turno.getFecha().isBefore(fechaHoy)) {
            return true;
        }
        // Si es hoy, verifico la hora
        if (turno.getFecha().isEqual(fechaHoy)) {
            if (turno.getHora().isBefore(horaAhora)) {
                return true;
            }
        }
        return false;
    }

    private boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Personalizar los botones
        ButtonType buttonTypeYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    private void cancelarTurno(Turno turno) {
        try {
            // Elimino el turno seleccionado
            turnoDAO.delete(turno.getId());

            // Creo un nuevo turno con los mismos datos pero libre
            Turno nuevoTurno = new Turno(
                    0, // id se genera automáticamente
                    turno.getFecha(),
                    turno.getHora(),
                    E.Libre,
                    0, // pago, si aplica
                    null, // fecha de pago
                    turno.getPersona() != null ? turno.getPersona() : null, // o null si querés que quede sin persona
                    turno.getCancha(),
                    null, // fecha cancelación
                    null  // reintegro
            );

            // Guardo el nuevo turno como libre
            turnoDAO.create(nuevoTurno);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cancelar la reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleGestionarPagos(){
        mostrarAlerta("Gestionar Pagos de Reservas", "Esta funcionalidad aún no está disponible.");
    }

    @FXML
    private void handleModificarReserva(){
        mostrarAlerta("Modificar Reserva", "Esta funcionalidad aún no está disponible.");
    }


}
