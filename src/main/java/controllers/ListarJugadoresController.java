package controllers;

import DAO.impl.JugadorDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Jugador;
import utilities.NavigationHelper;
import utilities.Paths;

public class ListarJugadoresController {

    @FXML
    private ImageView botonBack;

    @FXML
    private TableView<Jugador> tableJugadores;

    @FXML private TableColumn<Jugador, String> colNombre;
    @FXML private TableColumn<Jugador, String> colApellido;
    @FXML private TableColumn<Jugador, Integer> colCategoria;
    @FXML private TableColumn<Jugador, String> colTelefono;
    @FXML private TableColumn<Jugador, String> colDireccion;
    @FXML private TableColumn<Jugador, Integer> colSexo;
    @FXML private TableColumn<Jugador, Integer> colAnioNacimiento; // ✅ cambiado de LocalDate a Integer
    @FXML private TableColumn<Jugador, Integer> colPuntos;

    private ObservableList<Jugador> listaJugadores = FXCollections.observableArrayList();
    private JugadorDAOImpl jugadorDAO = new JugadorDAOImpl();

    @FXML
    public void initialize() {
        // Mapeo con los getters de Jugador
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colAnioNacimiento.setCellValueFactory(new PropertyValueFactory<>("anioNac")); // getter getAnioNac()
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos"));

        // Cargar jugadores desde la base de datos
        listaJugadores.addAll(jugadorDAO.findAll());
        tableJugadores.setItems(listaJugadores);
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
}