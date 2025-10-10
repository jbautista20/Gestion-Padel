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
import utilities.TurnoContext;

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
    //-----ABM PERSONA-------
    @FXML
    private  Label botonAltaPersona;
    @FXML
    private  Label botonBajaPersona;
    @FXML
    private Label botonModificarPersona;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarPersonas();
        configurarSeleccionTabla();

        // Recuperamos el turno seleccionado en listar turnos
        turnoSeleccionado = TurnoContext.getTurnoSeleccionado();
        if (turnoSeleccionado != null) {
            LabelTurnoSeleccionado.setText("Datos de reserva:\nFecha: " + turnoSeleccionado.getFecha() + "\nHora: " + turnoSeleccionado.getHora() + "\nCancha: " + (turnoSeleccionado.getCancha() != null ? turnoSeleccionado.getCancha().getNumero() : "-")
            );
        }

        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            personaSeleccionada = newSelection;
            if (newSelection != null) {
                labelPersonaSeleccionada.setText("Persona seleccionada:\nNombre: " + newSelection.getNombre() + " " + newSelection.getApellido() + "\nTeléfono: " + newSelection.getTelefono());
            } else {
                labelPersonaSeleccionada.setText("No hay persona seleccionada");
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
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                personaSeleccionada = newSelection;
                System.out.println("Persona seleccionada: " + newSelection.getNombre() + " " + newSelection.getApellido());
            }
        });
    }

    //---------------------------------BOTON DE BACK--------------------------------------
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTurnos, "ListarTorneos");
        System.out.println("Volviendo a la pantalla turnos");
    }
    //---------------------------------BOTON DE BACK--------------------------------------

    //--------------------------------CREAR RESERVA---------------------------------------
    @FXML
    private void handleCrearReserva(MouseEvent event) {
        if (personaSeleccionada == null) {
            mostrarAlertaError("Selección requerida", "Por favor, seleccione una persona de la tabla para crear la reserva.");
            return;
        }
        if (turnoSeleccionado == null) {
            mostrarAlertaError("Error", "No se ha seleccionado ningún turno.");
            return;
        }

        // pars validar contenido de pago
        String montoTexto = textMonto.getText();
        if (montoTexto == null || montoTexto.trim().isEmpty()) {
            mostrarAlertaError("Error", "Por favor, ingrese monto pagado para poder cargar la reserva.");
            return;
        }
        if (datePickerFechaPago.getValue() == null) {
            mostrarAlertaError("Error", "Por favor, ingrese fecha de pago para poder cargar la reserva.");
            return;
        }
        double monto;
        try {
            monto = Double.parseDouble(montoTexto.trim());
            if(monto<0){
                mostrarAlertaError("Monto inválido","Por favor, ingrese un número válido en el campo de monto.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlertaError("Monto inválido", "Por favor, ingrese un número válido en el campo de monto.");
            return;
        }

        try {
            turnoSeleccionado.setPersona(personaSeleccionada);
            int montoEntero = (int) monto; // Convertir a int para el set
            turnoSeleccionado.setPago(montoEntero);
            turnoSeleccionado.setFecha_Pago(datePickerFechaPago.getValue());
            turnoSeleccionado.setEstado(E.Ocupado);

            crearReserva(turnoSeleccionado, personaSeleccionada);
            mostrarAlertaExito("Reserva creada", "Reserva creada exitosamente para " + personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellido());

            TurnoContext.limpiar();

            Stage stage = (Stage) botonCrearReserva.getScene().getWindow();
            NavigationHelper.cambiarVista(stage, Paths.pantallaTurnos, "ListarTurnos");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo crear la reserva: " + e.getMessage());
        }
    }

    //inserta en la BD
    private void crearReserva(Turno turno, Persona persona) {
        try {
            int monto = (int) Double.parseDouble(textMonto.getText());
            LocalDate fechaPago = datePickerFechaPago.getValue();

            turno.setEstado(E.Ocupado);
            turno.setPersona(persona);
            turno.setPago(monto);
            turno.setFecha_Pago(fechaPago);

            turnoDAO.update(turno);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la reserva: " + e.getMessage());
        }
    }
    //--------------------------------CREAR RESERVA---------------------------------------


    // ----------------Métodos para los botones de gestión de personas----------------------------------
    @FXML
    private void handleAltaPersona(MouseEvent event) {
        Stage stage = (Stage) botonAltaPersona.getScene().getWindow();
        mostrarAlertaInfo("Alta Persona", "Funcionalidad de Alta en desarrollo.");

    }

    @FXML
    private void handleBajaPersona(MouseEvent event) {
        Stage stage = (Stage) botonBajaPersona.getScene().getWindow();
        mostrarAlertaInfo("Baja Persona", "Funcionalidad de Baja en desarrollo.");
    }

    @FXML
    private void handleModificarPersona(MouseEvent event) {
        Stage stage = (Stage) botonModificarPersona.getScene().getWindow();
        mostrarAlertaInfo("Modificar Persona", "Funcionalidad de modificación en desarrollo.");

    }
    // ----------------Métodos para los botones de gestión de personas----------------------------------

    // ---------------------Métodos auxiliares para alertas---------------------------------
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

    private void mostrarAlertaInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

