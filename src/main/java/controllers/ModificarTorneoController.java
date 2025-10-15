package controllers;

import DAO.GenericDAO;
import DAO.impl.TorneoDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.T;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;

import java.time.LocalDate;

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

    private final GenericDAO<Torneo> torneoDAO = new TorneoDAOImpl();
    private Torneo torneoActual;

    @FXML
    public void initialize() {
        comboBoxCategoria.getItems().addAll("1°", "2°", "3°", "4°", "5°", "6°", "7°", "8°", "9°", "10°");
        comboBoxTipoTorneo.getItems().addAll("damas", "caballeros", "mixto");
        cargarDatosTorneo();
    }

    private void cargarDatosTorneo() {
        Object datos = NavigationHelper.getDatos();

        if (datos instanceof Torneo) {
            torneoActual = (Torneo) datos;

            // Limpiar datos almacenados para evitar fugas entre vistas
            NavigationHelper.clearDatos();

            primerPremio.setText(torneoActual.getPremio1());
            segundoPremio.setText(torneoActual.getPremio2());
            valorDeInscripcion.setText(String.valueOf(torneoActual.getValor_Inscripcion()));
            comboBoxCategoria.setValue(torneoActual.getCategoria() + "°");
            comboBoxTipoTorneo.setValue(convertirEnumATexto(torneoActual.getTipo()));
            fecha.setValue(torneoActual.getFecha());
        } else {
            System.err.println("⚠️ No se recibieron datos válidos del torneo en NavigationHelper.");
        }
    }

    // Botón para volver atrás
    @FXML
    private void handleBackButton(MouseEvent event) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "Listar Torneos");
        System.out.println("Volviendo al menú principal");
    }

    // Botón para guardar los cambios
    @FXML
    private void handleGuardarModificacion(MouseEvent event) {
        if (torneoActual == null) {
            mostrarAlerta("Error", "No hay torneo cargado para modificar.");
            return;
        }

        Integer categoria = obtenerCategoriaNumerica();
        String tipoTorneo = comboBoxTipoTorneo.getValue();
        T tipoEnum = convertirTipoTorneo(tipoTorneo);

        if (tipoEnum == null) {
            mostrarAlerta("Error", "Seleccione un tipo de torneo válido.");
            return;
        }

        String premio1 = primerPremio.getText().trim();
        String premio2 = segundoPremio.getText().trim();

        int inscripcion;
        try {
            inscripcion = validarYConvertirPrecio(valorDeInscripcion.getText().trim());
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage());
            return;
        }

        LocalDate fechaTorneo = fecha.getValue();

        if (!validarCampos(categoria, tipoTorneo, premio1, premio2, fechaTorneo, inscripcion))
            return;

        // Actualizamos los datos
        actualizarTorneo(categoria, tipoEnum, premio1, premio2, fechaTorneo, inscripcion);

        // Guardamos en BD
        torneoDAO.update(torneoActual);
        mostrarAlerta("Éxito", "Torneo modificado correctamente.");

        limpiarFormulario();
    }

    private void actualizarTorneo(int categoria, T tipo, String premio1, String premio2,
                                  LocalDate fechaI, int inscripcion) {
        torneoActual.setCategoria(categoria);
        torneoActual.setTipo(tipo);
        torneoActual.setPremio1(premio1);
        torneoActual.setPremio2(premio2);
        torneoActual.setFecha(fechaI);
        torneoActual.setValor_Inscripcion(inscripcion);
    }

    private T convertirTipoTorneo(String tipoTorneo) {
        if (tipoTorneo == null) return null;

        switch (tipoTorneo.toLowerCase()) {
            case "damas": return T.Damas;
            case "caballeros": return T.Caballeros;
            case "mixto": return T.Mixto;
            default: return null;
        }
    }

    private String convertirEnumATexto(T tipo) {
        if (tipo == null) return "";
        switch (tipo) {
            case Damas: return "damas";
            case Caballeros: return "caballeros";
            case Mixto: return "mixto";
            default: return "";
        }
    }

    private int validarYConvertirPrecio(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido.");
        }
    }

    private boolean validarCampos(Integer categoria, String tipoTorneo, String premio1,
                                  String premio2, LocalDate fechaInicio, int inscripcion) {
        if (categoria == null) {
            mostrarAlerta("Error", "Seleccione una categoría.");
            return false;
        }
        if (tipoTorneo == null) {
            mostrarAlerta("Error", "Seleccione un tipo de torneo.");
            return false;
        }
        if (premio1.isEmpty() || premio2.isEmpty()) {
            mostrarAlerta("Error", "Ingrese ambos premios.");
            return false;
        }
        if (fechaInicio == null) {
            mostrarAlerta("Error", "Seleccione una fecha válida.");
            return false;
        }
        if (inscripcion <= 0) {
            mostrarAlerta("Error", "La inscripción debe ser mayor a 0.");
            return false;
        }
        if (fechaInicio.isBefore(LocalDate.now())) {
            mostrarAlerta("Error", "La fecha no puede ser en el pasado.");
            return false;
        }
        return true;
    }

    public Integer obtenerCategoriaNumerica() {
        String categoriaElegida = comboBoxCategoria.getValue();
        if (categoriaElegida != null && !categoriaElegida.isEmpty()) {
            try {
                return Integer.parseInt(categoriaElegida.replace("°", ""));
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir categoría: " + categoriaElegida);
            }
        }
        return null;
    }

    private void limpiarFormulario() {
        comboBoxCategoria.setValue(null);
        comboBoxTipoTorneo.setValue(null);
        primerPremio.clear();
        segundoPremio.clear();
        valorDeInscripcion.clear();
        fecha.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}