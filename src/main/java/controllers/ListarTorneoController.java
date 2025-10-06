package controllers;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utilities.NavigationHelper;
import utilities.Paths;
public class ListarTorneoController {
    @FXML
    private Pane gestionarTorneo;
    @FXML
    private ImageView botonBack;
    @FXML
    private Pane crearTorneoView;

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




    //----------------------------Funcionalidad Boton Back----------------------------------//
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaMenuPrincipal, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }
    //----------------------------Funcionalidad Boton Back----------------------------------//

}
