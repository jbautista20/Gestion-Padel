package controllers;
import DAO.impl.TorneoDAOImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Equipo;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;
import java.time.LocalDate;
import java.util.List;

public class ListarTorneoController {
    @FXML
    private Pane gestionarTorneo;

    @FXML
    private ImageView botonBack;

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



    //----------------------------Funcionalidad Boton Back----------------------------------//
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaMenuPrincipal, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }
    //----------------------------Funcionalidad Boton Back----------------------------------//

}