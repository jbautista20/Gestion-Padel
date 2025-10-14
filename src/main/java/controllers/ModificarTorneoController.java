package controllers;

import DAO.GenericDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import controllers.DataManager;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

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
    private  Torneo torneoActual;

    public void initialize() {

        comboBoxCategoria.getItems().addAll("1", "2°", "3°","4°","5°","6°","7°","8°","9°","10°");
        comboBoxTipoTorneo.getItems().addAll("damas", "caballeros", "mixto");
        cargarDatosTorneo();

        fecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate today = LocalDate.now();
                LocalDate maxDate = today.plusMonths(2);

                // Deshabilita fechas fuera del rango permitido
                setDisable(empty || date.isBefore(today) || date.isAfter(maxDate));

                // Estilo visual para las fechas no disponibles
                if (isDisable()) {
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });

// Valor inicial en hoy
        fecha.setValue(LocalDate.now());

        //configurarEventos();
    }
    private void cargarDatosTorneo() {
        torneoActual = DataManager.getInstance().getTorneoSeleccionado();

        if (torneoActual != null) {
            primerPremio.setText(torneoActual.getPremio1());
            segundoPremio.setText(torneoActual.getPremio2());
            valorDeInscripcion.setText(String.valueOf(torneoActual.getValor_Inscripcion()));
            comboBoxCategoria.setValue(torneoActual.getCategoria() + "°");

            String tipoTexto = convertirEnumATexto(torneoActual.getTipo());
            comboBoxTipoTorneo.setValue(tipoTexto);

            fecha.setValue(torneoActual.getFecha());
        } else {
            System.err.println("❌ ERROR: No hay torneo en DataManager");
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
    //----------------------------Guarda las modificaciones del torneo------------------------------
        @FXML
        private void handleGuardarModificacion(MouseEvent event) {
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
            try {
                String textoInscripcion = valorDeInscripcion.getText().trim();
                inscripcion = validarYConvertirPrecio(textoInscripcion);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ SALIENDO: Error en inscripción - " + e.getMessage());
                mostrarAlerta("Error", e.getMessage());
                return;
            }
            LocalDate fechaTorneo = fecha.getValue();
            boolean camposValidos = validarCampos(categoria, tipoTorneo, premio1, premio2, fechaTorneo, inscripcion);
            if (!camposValidos) {
                mostrarAlerta("Error", "Por favor complete todos los campos");
                return;
            } else {
                if (torneoActual != null) {
                    actualizarTorneo(categoria, tipoDeTorneo, premio1, premio2, fechaTorneo, inscripcion);
                    torneoDAO.update(torneoActual);
                    mostrarAlerta("Éxito", "Torneo modificado correctamente");
                    limpiarFormulario();
                }
            }
        }

        //----------------------------Actualiza el torneo con los nuevos datos------------------------------
        private void actualizarTorneo(int categoria, T tipoTorneo, String premio1, String premio2,
                                      LocalDate fechaI, int inscripcion) {
            torneoActual.setCategoria(categoria);
            torneoActual.setTipo(tipoTorneo);
            torneoActual.setPremio1(premio1);
            torneoActual.setPremio2(premio2);
            torneoActual.setFecha(fechaI);
            torneoActual.setValor_Inscripcion(inscripcion);
            // El estado se mantiene como estaba (Abierto, Cerrado, etc.)
        }

        //----------------------------Convierte el TextFiel de tipo de torneo a Enum------------------------------
        private T convertirTipoTorneo(String tipoTorneo) {
            if (tipoTorneo == null) {
                return null;
            }

            switch (tipoTorneo.toLowerCase()) {
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

        //----------------------------Convierte el Enum a texto para mostrar en el ComboBox------------------------------
        private String convertirEnumATexto(T tipo) {
            if (tipo == null) return "";

            switch (tipo) {
                case Damas: return "damas";
                case Caballeros: return "caballeros";
                case Mixto: return "mixto";
                default: return "";
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
            if (inscripcion <= 0) { // Cambié == 0 por <= 0 para que no acepte negativos
                mostrarAlerta("Error", "La inscripción debe ser mayor a 0");
                return false;
            }
            if (fechaInicio.isBefore(LocalDate.now())) {
                mostrarAlerta("Error", "La fecha no puede ser en el pasado");
                return false;
            }
            return true;
        }

        //-------------------------------Convertir Texto a numero ------------------------------
        public Integer obtenerCategoriaNumerica() {
            String categoriaElegida = comboBoxCategoria.getValue();
            if (categoriaElegida != null && !categoriaElegida.isEmpty()) {
                try {
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
            valorDeInscripcion.clear();
            fecha.setValue(null);
        }

        private Stage obtenerStageActual() {
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage && window.isShowing()) {
                    return (Stage) window;
                }
            }
            return null;
        }

        //--------------------------------------- Muestra carteles de errores----------------------------
        private void mostrarAlerta(String titulo, String mensaje) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
}

