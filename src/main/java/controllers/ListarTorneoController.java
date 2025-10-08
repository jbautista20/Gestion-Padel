package controllers;
import DAO.TorneoDAO;
import DAO.impl.TorneoDAOImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Equipo;
import models.Es;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ListarTorneoController {
    @FXML
    private Pane gestionarTorneo;
    @FXML
    private ImageView botonBack;
    @FXML
    private Pane crearTorneoView;


    @FXML
    private TableView<Torneo> tableTorneos;

    private ObservableList<Torneo> listaTorneos = FXCollections.observableArrayList();


    @FXML private TableColumn<Torneo, String> colEstado;
    @FXML private TableColumn<Torneo, LocalDate> colFecha;
    @FXML private TableColumn<Torneo, Integer> colCategoria;
    @FXML private TableColumn<Torneo, String> colTipo;
    @FXML private TableColumn<Torneo, Integer> colValorInscripcion;
    @FXML private TableColumn<Torneo, String> colPremioCampeon;
    @FXML private TableColumn<Torneo, String> colPremioSubcampeon;
    @FXML private TableColumn<Torneo, String> colInscriptos;
    private TorneoDAO torneoDAO = new TorneoDAOImpl();

    @FXML
    public void initialize() {
        // Configurar las columnas
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstados().name()));

        colFecha.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getFecha()));

        colCategoria.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCategoria()).asObject());

        colTipo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTipo().name()));

        colValorInscripcion.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getValor_Inscripcion()).asObject());

        colPremioCampeon.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPremio1()));

        colPremioSubcampeon.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPremio2()));

        colInscriptos.setCellValueFactory(cellData -> {
            int cantidad = 0;
            if (cellData.getValue().getEquipos() != null)
                for (Equipo e : cellData.getValue().getEquipos())
                    if (e != null) cantidad++;
            return new SimpleStringProperty(String.valueOf(cantidad));
        });

        // Enlazar la lista observable con la tabla
        tableTorneos.setItems(listaTorneos);

        // Cargar torneos desde la base
        cargarTorneos();
    }

    private void cargarTorneos() {
        TorneoDAOImpl dao = new TorneoDAOImpl();
        List<Torneo> torneosBD = dao.findAll();
        listaTorneos.setAll(torneosBD);
    }


    //----------------------------Abrir scene gestionar torneo------------------------------//
    @FXML
    private void abrirGestionarTorneo(MouseEvent event) {
        Stage stage = (Stage) gestionarTorneo.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaGestionarTorneos, "GestionarTorneo");
        System.out.println("cambiando la ventana");
    }

    //----------------------------Abrir scene gestionar torneo------------------------------//

    //----------------------------Abrir scene crear torneo----------------------------------//
    @FXML
    private void abrirCrearTorneo(MouseEvent event) {
        Stage stage = (Stage) crearTorneoView.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantellaCrearTorneo, "CrearTorneo");
        System.out.println("cambiando la ventana");
    }
    //----------------------------Abrir scene crear torneo----------------------------------//
    //----------------------------Abrir scene crear torneo----------------------------------//
    @FXML
    private void handleEliminarTorneo() {
        Torneo torneoSeleccionado = tableTorneos.getSelectionModel().getSelectedItem();

        if (torneoSeleccionado != null) {//verifica que haya pulsado algun torneo
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Está seguro de eliminar el torneo?");
            confirmacion.setContentText("Torneo: " + "\nEsta acción no se puede deshacer.");

            Es estadoTorneo = torneoSeleccionado.getEstados();

            Optional<ButtonType> resultado = confirmacion.showAndWait();//esera la confimacion del boton del cartel de alerta
            if(estadoTorneo == Es.Abierto) {
                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {//si presiono ok elimina el torneo
                    try {
                        torneoDAO.delete(torneoSeleccionado.getId());//elimina de la bd
                        listaTorneos.remove(torneoSeleccionado);//elimina de la tableView
                        tableTorneos.refresh();//refresca la tableView
                        mostrarAlerta("Éxito", "Torneo eliminado correctamente");

                    } catch (Exception e) {
                        mostrarAlerta("Error", "No se pudo eliminar el torneo: " + e.getMessage());
                    }
                }
            }
        } else {
            mostrarAlerta("Error", "Seleccione un torneo para eliminar");
        }
    }
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    //----------------------------Funcionalidad Boton Back----------------------------------//
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaMenuPrincipal, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }
    //----------------------------Funcionalidad Boton Back----------------------------------//

}