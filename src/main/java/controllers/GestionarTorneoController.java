package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

public class GestionarTorneoController {

    @FXML
    private ImageView botonBack;

    @FXML
    private Text txtEquipoCampeon;

    @FXML
    private Text txtEquipoCuartos1;

    @FXML
    private Text txtEquipoCuartos2;

    @FXML
    private Text txtEquipoCuartos3;

    @FXML
    private Text txtEquipoCuartos4;

    @FXML
    private Text txtEquipoCuartos5;

    @FXML
    private Text txtEquipoCuartos6;

    @FXML
    private Text txtEquipoCuartos7;

    @FXML
    private Text txtEquipoCuartos8;

    @FXML
    private Text txtEquipoFinal1;

    @FXML
    private Text txtEquipoFinal2;

    @FXML
    private Text txtEquipoSemi1;

    @FXML
    private Text txtEquipoSemi2;

    @FXML
    private Text txtEquipoSemi3;

    @FXML
    private Text txtEquipoSemi4;

    @FXML
    private Text txtMarcadorCuartos1;

    @FXML
    private Text txtMarcadorCuartos2;

    @FXML
    private Text txtMarcadorCuartos3;

    @FXML
    private Text txtMarcadorCuartos4;

    @FXML
    private Text txtMarcadorCuartos5;

    @FXML
    private Text txtMarcadorCuartos6;

    @FXML
    private Text txtMarcadorCuartos7;

    @FXML
    private Text txtMarcadorCuartos8;

    @FXML
    private Text txtMarcadorFinal1;

    @FXML
    private Text txtMarcadorFinal2;

    @FXML
    private Text txtMarcadorSemi1;

    @FXML
    private Text txtMarcadorSemi2;

    @FXML
    private Text txtMarcadorSemi3;

    @FXML
    private Text txtMarcadorSemi4;

    private Torneo torneoActual;

    @FXML
    private void initialize() {
        Object datos = NavigationHelper.getDatos();
        if (datos instanceof Torneo) {
            torneoActual = (Torneo) datos;
            NavigationHelper.clearDatos();
        } else {
            System.err.println("No se recibieron datos validos del torneo en NavigationHelper.");
        }
    }

    @FXML
    private void botonArmarCruces(MouseEvent event) {
        txtEquipoCuartos1.setText("RiffoMontenegro");
        txtEquipoCuartos2.setText("RioRiffo");
        txtEquipoCuartos3.setText("MontenegroRio");
        txtEquipoCuartos4.setText("SosaRio");
    }

    @FXML
    private void botonGestionarEquipos(MouseEvent event) {
        txtMarcadorCuartos4.setText("1");
        txtMarcadorCuartos3.setText("3");
        txtMarcadorCuartos2.setText("2");
        txtMarcadorCuartos1.setText("0");
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }
}
