package controllers;

import DAO.impl.EquipoDAOImpl;
import DAO.impl.JugadorDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Equipo;
import models.Jugador;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

import java.time.LocalDate;
import java.util.Arrays;

public class InscribirEquipoController {

    @FXML
    private ImageView botonBack;

    @FXML
    private Pane btnConfirmar;

    @FXML
    private TableView<Jugador> tableJugadores;

    @FXML private TableColumn<Jugador, String> colNombre;
    @FXML private TableColumn<Jugador, String> colApellido;
    @FXML private TableColumn<Jugador, Integer> colCategoria;
    @FXML private TableColumn<Jugador, String> colTelefono;
    @FXML private TableColumn<Jugador, String> colDireccion;
    @FXML private TableColumn<Jugador, Integer> colSexo;
    @FXML private TableColumn<Jugador, Integer> colAnioNacimiento;
    @FXML private TableColumn<Jugador, Integer> colPuntos;

    private ObservableList<Jugador> listaJugadores = FXCollections.observableArrayList();
    private JugadorDAOImpl jugadorDAO = new JugadorDAOImpl();

    @FXML private Text txtCategoria;
    @FXML private Text txtJugador1;
    @FXML private Text txtJugador2;
    @FXML private Text txtNombreEquipo;
    @FXML private Text txtValorInscripcion;

    private Torneo torneoActual;
    private Jugador jugador1Seleccionado = null;
    private Jugador jugador2Seleccionado = null;

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

        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colAnioNacimiento.setCellValueFactory(new PropertyValueFactory<>("anioNac"));
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos"));

        // Cargar jugadores disponibles según categoría, tipo y torneo
        listaJugadores.addAll(jugadorDAO.findDisponiblesPorTorneo(torneoActual));

        tableJugadores.setItems(listaJugadores);

        // Manejar selección de jugadores
        tableJugadores.setOnMouseClicked(event -> manejarSeleccionJugador());

        // Cargar txt
        txtCategoria.setText(String.valueOf(torneoActual.getCategoria()) + "°");
        txtValorInscripcion.setText("$"+String.valueOf(torneoActual.getValor_Inscripcion()));
    }

    private void manejarSeleccionJugador() {
        Jugador jugadorSeleccionado = tableJugadores.getSelectionModel().getSelectedItem();
        if (jugadorSeleccionado == null) return;

        // Si ya está seleccionado como jugador1 o jugador2 → deseleccionar
        if (jugadorSeleccionado.equals(jugador1Seleccionado)) {
            jugador1Seleccionado = null;
            txtJugador1.setText("");
        } else if (jugadorSeleccionado.equals(jugador2Seleccionado)) {
            jugador2Seleccionado = null;
            txtJugador2.setText("");
        }
        // Si hay espacio para seleccionarlo
        else if (jugador1Seleccionado == null) {
            jugador1Seleccionado = jugadorSeleccionado;
            txtJugador1.setText(jugadorSeleccionado.getNombre() + " " + jugadorSeleccionado.getApellido());
        } else if (jugador2Seleccionado == null) {
            jugador2Seleccionado = jugadorSeleccionado;
            txtJugador2.setText(jugadorSeleccionado.getNombre() + " " + jugadorSeleccionado.getApellido());
        } else {
            // Ambos jugadores ya seleccionados → mostrar aviso
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selección completa");
            alerta.setHeaderText(null);
            alerta.setContentText("Ya seleccionaste dos jugadores. Deseleccioná uno antes de elegir otro.");
            alerta.showAndWait();
        }

        // Actualizar nombre del equipo si ambos jugadores están seleccionados
        if (jugador1Seleccionado != null && jugador2Seleccionado != null) {
            String nombreEquipo = jugador1Seleccionado.getApellido() + jugador2Seleccionado.getApellido();
            txtNombreEquipo.setText(nombreEquipo);
        } else {
            txtNombreEquipo.setText(""); // Vaciar si falta alguno
        }
    }

    @FXML
    void botonConfirmarEquipo(MouseEvent event) {
        if (jugador1Seleccionado == null || jugador2Seleccionado == null) {
            System.out.println("Debe seleccionar dos jugadores para crear un equipo.");
            return;
        }

        // Crear el equipo
        Equipo nuevoEquipo = new Equipo();
        nuevoEquipo.setJugador1(jugador1Seleccionado);
        nuevoEquipo.setJugador2(jugador2Seleccionado);
        nuevoEquipo.setTorneo(torneoActual);
        nuevoEquipo.setNombre(txtNombreEquipo.getText());
        nuevoEquipo.setPtos_T_Obt(0);
        nuevoEquipo.setFecha_Insc(LocalDate.now());
        nuevoEquipo.setMotivo_desc(null);
        nuevoEquipo.setMotivo_desc(null);

        // Guardar en BD
        EquipoDAOImpl equipoDAO = new EquipoDAOImpl();
        equipoDAO.create(nuevoEquipo);

        // Agregar al arreglo del torneo
        Equipo[] equiposExistentes = torneoActual.getEquipos();
        if (equiposExistentes == null) {
            torneoActual.setEquipos(new Equipo[]{nuevoEquipo});
        } else {
            Equipo[] nuevos = Arrays.copyOf(equiposExistentes, equiposExistentes.length + 1);
            nuevos[nuevos.length - 1] = nuevoEquipo;
            torneoActual.setEquipos(nuevos);
        }

        System.out.println("Equipo creado: " + nuevoEquipo.getNombre());

        //volver a la pantalla de gestión
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaGestionarEquipos, "Gestionar Equipos", torneoActual);
    }


    @FXML
    void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaGestionarEquipos, "Gestionar Equipos", torneoActual);
        System.out.println("cambiando la ventana");
    }
}
