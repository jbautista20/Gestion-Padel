package controllers;

import DAO.GenericDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.E;
import models.Persona;
import models.Turno;
import DAO.impl.PersonaDAOImpl;
import DAO.impl.TurnoDAOImpl;
import utilities.NavigationHelper;
import utilities.Paths;

import java.time.LocalDate;
import java.util.List;
public class CrearReservaController {
    private GenericDAO<Turno> turnoDAO = new TurnoDAOImpl();
    private GenericDAO<Persona> personaDAO = new PersonaDAOImpl();
    private Turno turnoSeleccionado;

    @FXML
    private Label LabelTurnoSeleccionado;
    @FXML
    private ImageView botonBack;
    @FXML
    private Pane botonCrearReserva;
    @FXML
    private TableView<Persona> tablaPersonas;
    @FXML
    private TableColumn<Persona, String> columnaNombre;
    @FXML
    private TableColumn<Persona, String> columnaApellido;
    @FXML
    private TableColumn<Persona, String> columnaTelefono;
    @FXML
    private TableColumn<Persona, String> columnaDireccion;
    @FXML
    private Persona personaSeleccionada;
    @FXML
    private Label labelPersonaSeleccionada;
    @FXML
    private TextField textMonto;
    @FXML
    private DatePicker datePickerFechaPago;

    // ----- Botones ABM Persona -----
    @FXML private Label botonAltaPersona;
    @FXML private Label botonBajaPersona;
    @FXML private Label botonModificarPersona;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarPersonas();
        configurarSeleccionTabla();

        // Recuperamos el turno seleccionado desde NavigationHelper
        Object datos = NavigationHelper.getDatos();
        if (datos instanceof Turno) {
            turnoSeleccionado = (Turno) datos;
            NavigationHelper.clearDatos(); // limpiar después de usarlo
        }

        if (turnoSeleccionado != null) {
            LabelTurnoSeleccionado.setText(
                    "Datos de reserva:\n" +
                            "Fecha: " + turnoSeleccionado.getFecha() +
                            "\nHora: " + turnoSeleccionado.getHora() +
                            "\nCancha: " + (turnoSeleccionado.getCancha() != null
                            ? turnoSeleccionado.getCancha().getNumero()
                            : "-")
            );
        } else {
            LabelTurnoSeleccionado.setText("No se ha seleccionado un turno.");
        }

        // Listener para mostrar persona seleccionada
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            personaSeleccionada = newSel;
            if (newSel != null) {
                labelPersonaSeleccionada.setText(
                        "Persona seleccionada:\n" +
                                "Nombre: " + newSel.getNombre() + " " + newSel.getApellido() +
                                "\nTeléfono: " + newSel.getTelefono()
                );
            } else {
                labelPersonaSeleccionada.setText("No hay persona seleccionada");
            }
        });

        // Restringir selección de fecha de pago (no permitir fechas futuras)
        LocalDate hoy = LocalDate.now();
        datePickerFechaPago.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setDisable(true);
                } else {
                    // Deshabilitar fechas futuras
                    if (item.isAfter(hoy)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;"); // opcional: colorear las fechas inválidas
                    }
                }
            }
        });

    }

    private void configurarColumnas() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columnaDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
    }

    private void cargarPersonas() {
        try {
            List<Persona> personas = personaDAO.findAll();
            tablaPersonas.getItems().setAll(personas);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudieron cargar las personas: " + e.getMessage());
        }
    }

    private void configurarSeleccionTabla() {
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            personaSeleccionada = newSel;
            if (newSel != null) {
                System.out.println("Persona seleccionada: " + newSel.getNombre() + " " + newSel.getApellido());
            }
        });
    }

    // -------------------- BOTÓN BACK --------------------
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTurnos, "ListarTurnos");
        System.out.println("Volviendo a la pantalla de turnos");
    }

    // -------------------- CREAR RESERVA --------------------
    @FXML
    private void handleCrearReserva(MouseEvent event) {
        if (personaSeleccionada == null) {
            mostrarAlertaError("Selección requerida", "Por favor, seleccione una persona para crear la reserva.");
            return;
        }

        if (turnoSeleccionado == null) {
            mostrarAlertaError("Error", "No se ha seleccionado ningún turno.");
            return;
        }

        // Validar monto
        String montoTexto = textMonto.getText();
        if (montoTexto == null || montoTexto.trim().isEmpty()) {
            mostrarAlertaError("Error", "Por favor, ingrese el monto pagado.");
            return;
        }

        if (datePickerFechaPago.getValue() == null) {
            mostrarAlertaError("Error", "Por favor, ingrese la fecha de pago.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoTexto.trim());
            if (monto < 0) {
                mostrarAlertaError("Monto inválido", "Por favor, ingrese un número válido en el campo de monto.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlertaError("Monto inválido", "Por favor, ingrese un número válido en el campo de monto.");
            return;
        }

        try {
            // Seteamos los datos en el turno
            turnoSeleccionado.setPersona(personaSeleccionada);
            turnoSeleccionado.setPago((int) monto);
            turnoSeleccionado.setFecha_Pago(datePickerFechaPago.getValue());
            turnoSeleccionado.setEstado(E.Ocupado);

            crearReserva(turnoSeleccionado);

            mostrarAlertaExito("Reserva creada", "Reserva creada exitosamente para " +
                    personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellido());

            // Volver a pantalla de turnos
            Stage stage = (Stage) botonCrearReserva.getScene().getWindow();
            NavigationHelper.cambiarVista(stage, Paths.pantallaTurnos, "ListarTurnos");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo crear la reserva: " + e.getMessage());
        }
    }

    // -------------------- GUARDAR EN BASE --------------------
    private void crearReserva(Turno turno) {
        try {
            turnoDAO.update(turno);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la reserva: " + e.getMessage());
        }
    }

    // -------------------- MÉTODOS AUXILIARES --------------------
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleModificarPersona(MouseEvent event) {
        mostrarAlertaExito("Modificar Persona", "Esta funcionalidad aún no está disponible.");
    }
    @FXML
    private void handleAltaPersona(MouseEvent event) {
        mostrarAlertaExito("Alta Persona", "Esta funcionalidad aún no está disponible.");
    }
    @FXML
    private void handleBajaPersona(MouseEvent event) {
        mostrarAlertaExito("Baja Persona", "Esta funcionalidad aún no está disponible.");
    }

}
