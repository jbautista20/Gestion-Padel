package controllers;

import DAO.GenericDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Es;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import DAO.impl.TorneoDAOImpl;
import models.T;

public class ModificarTorneoController {
    @FXML
    private TextField primerPremio;
    @FXML
    private TextField segundoPremio;
    @FXML
    private DatePicker fecha;
    @FXML
    private TextField valorDeInscripcion;
    @FXML
    private ComboBox<String> comboBoxCategoria;
    @FXML
    private ComboBox<String> comboBoxTipoTorneo;
    @FXML
    private ImageView botonBack;

    private GenericDAO<Torneo> torneoDAO = new TorneoDAOImpl();

    public void initialize() {
        comboBoxCategoria.getItems().addAll("1", "2°", "3°","4°","5°","6°","7°","8°","9°","10°");
        comboBoxTipoTorneo.getItems().addAll("damas", "caballeros", "mixto");
        cargarDatosTorneo();
        //configurarEventos();
    }
    private void cargarDatosTorneo() {
        Torneo torneo = DataManager.getInstance().getTorneoSeleccionado();

        if (torneo != null) {
            primerPremio.setText(torneo.getPremio1());
            segundoPremio.setText(torneo.getPremio2());
            valorDeInscripcion.setText(String.valueOf(torneo.getValor_Inscripcion()));
            comboBoxCategoria.setValue(String.valueOf(torneo.getCategoria()));    // setValue, no getValue
            comboBoxTipoTorneo.setValue(torneo.getTipo().name());        // setValue, no getValue
            fecha.setValue(torneo.getFecha());                    // setValue, no getValue
        }
    }
    //----------------------------Funcionalidad Boton Back----------------------------------//
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }
    //----------------------------Funcionalidad Boton Back----------------------------------//
}
