package controllers;

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
import DAO.TorneoDAO;
import DAO.impl.TorneoDAOImpl;
import models.T;

public class CrearTorneoController {
    //---------------------------------Variables------------------------------------------
    @FXML
    private ComboBox<String> comboBoxCategoria;
    @FXML
    private ComboBox<String> comboBoxTipoTorneo;
    @FXML
    private ImageView botonBack;
    @FXML
    private TextField primerPremio;
    @FXML
    private TextField segundoPremio;
    @FXML
    private DatePicker fecha;
    @FXML
    private TextField valorDeInscripcion;


    private TorneoDAO torneoDAO = new TorneoDAOImpl();
    //---------------------------------Variables------------------------------------------
    //---------------------------------Inicializar comboBox-------------------------------
    @FXML
    public void initialize(){
        comboBoxCategoria.getItems().addAll("1", "2°", "3°","4°","5°","6°","7°","8°","9°","10°");
        comboBoxTipoTorneo.getItems().addAll("damas", "caballeros", "mixto");
    }
    //---------------------------------Inicializar comboBox-------------------------------

    //---------------------------------Boton de back--------------------------------------
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "ListarTorneos");
        System.out.println("Volviendo al menú principal");
    }
    //---------------------------------Boton de back--------------------------------------
    //------------------------Recibiendo los datos de la creacion de torneo---------------
    @FXML
    private void recibirDatosDeTorneo() {
        int categoria = obtenerCategoriaNumerica();
        String tipoTorneo = comboBoxTipoTorneo.getValue();
        T tipoDeTorneo = convertirTipoTorneo(tipoTorneo);
        if (tipoDeTorneo == null) {
            mostrarAlerta("Error", "Tipo de torneo no válido");
            return;
        }
        String premio1 = primerPremio.getText().trim();
        String premio2 = segundoPremio.getText().trim();
        int inscripcion = 0;
        //validamos que haya ingresado algo correcto en la inscripcion
        try {
            inscripcion = validarYConvertirPrecio(valorDeInscripcion.getText().trim());
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage());
            return;
        }
        LocalDate fechaTorneo = fecha.getValue();
        if (!validarCampos(categoria, tipoTorneo, premio1, premio2, fechaTorneo, inscripcion)) {//validamos que todo este bien cargado
            mostrarAlerta("Error", "Por favor complete todos los campos");//en caso de algo mal cargado
            return;
        } else {
            Torneo torneo = crearTorneo(categoria, tipoDeTorneo, premio1, premio2, fechaTorneo, inscripcion);
            torneoDAO.create(torneo);//enviamos el torneo a la bd
            limpiarFormulario();//limpiamos para que el usuario entienda que se cargo o que hubo un error
        }
    }
    //----------------------------Convierte el TextFiel de tipo de torneo a Enum------------------------------
    private T convertirTipoTorneo(String tipoTorneo) {
        if (tipoTorneo == null) {
            return null;
        }

        switch (tipoTorneo.toLowerCase()) {//
            case "damas":
                return T.Damas;
            case "caballeros":
                return T.Caballeros;
            case "mixto":
                return T.Mixto;
            default:
                return null;
        }
    }
    //-------------------------------La validamos que la inscripcion este bien----------------------------
    private int validarYConvertirPrecio(String precioTexto) {
        try {
            return Integer.parseInt(precioTexto);//castea a int
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido");
        }
    }
    //-------------------------------Validar que los campos no esten vacios-----------------------------------------
    private boolean validarCampos(Integer categoria, String tipoTorneo, String premio1,
                                  String premio2, LocalDate fechaInicio, int inscripcion) {
        if (categoria == null) {
            mostrarAlerta("Error", "Seleccione una categoría");
            return false;
        }
        if (tipoTorneo == null) {
            mostrarAlerta("Error", "Seleccione un tipo de torneo");
            return false;
        }
        if (premio1.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el primer premio");
            return false;
        }
        if (premio2.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el segundo premio");
            return false;
        }
        if (fechaInicio == null) {
            mostrarAlerta("Error", "Seleccione la fecha de inicio");
            return false;
        }
        if (inscripcion==0) {
            mostrarAlerta("Error", "inscripcion");
            return false;
        }
        if (fechaInicio.isBefore(LocalDate.now())) {
            mostrarAlerta("Error", "La fecha no puede ser en el pasado");
            return false;
        }
        return true;
    }
    //----------------------Cargo todos los atriburos del torneo para luego subirlo a la BD---------------------
    private Torneo crearTorneo(int categoria, T tipoTorneo, String premio1, String premio2,LocalDate fechaI, int inscripcion){
        Torneo torneoNuevo = new Torneo();
        torneoNuevo.setCategoria(categoria);
        torneoNuevo.setEstados(Es.Abierto);
        torneoNuevo.setTipo(tipoTorneo);
        torneoNuevo.setPremio1(premio1);
        torneoNuevo.setPremio2(premio2);
        torneoNuevo.setFecha(fechaI);
        torneoNuevo.setValor_Inscripcion(inscripcion);
        return torneoNuevo;
    }
    //--------------------------------------- Muestra carteles de errores----------------------------
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    //-------------------------------Convertir Texto a numero ------------------------------
    public Integer obtenerCategoriaNumerica() {
        String categoriaElegida = comboBoxCategoria.getValue();
        if (categoriaElegida != null && !categoriaElegida.isEmpty()) {
            try {
            // replace cambia el string ° a "" para que pueda almacenar el numero de la categoria
                String numeroTexto = categoriaElegida.replace("°", "");
                return Integer.parseInt(numeroTexto);//castea a int el string
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir categoría: " + categoriaElegida);
                return null;
            }
        }
        return null;
    }
    //----------------------------------------Limpiar Todo una vez que lo cargo y preciono confirmar-------------------------------------
    private void limpiarFormulario() {
        comboBoxCategoria.setValue(null);
        comboBoxTipoTorneo.setValue(null);
        primerPremio.clear();
        segundoPremio.clear();
        valorDeInscripcion.clear(); // Limpiar el campo de inscripción
        fecha.setValue(null);
    }
}

