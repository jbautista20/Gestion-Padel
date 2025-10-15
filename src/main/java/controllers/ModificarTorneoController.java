package controllers;

import DAO.GenericDAO;
import DAO.impl.CanchaDAOImpl;
import DAO.impl.TorneoDAOImpl;
import DAO.impl.TurnoDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.*;

import utilities.NavigationHelper;
import utilities.Paths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ModificarTorneoController {

    @FXML private TextField primerPremio;
    @FXML private TextField segundoPremio;
    @FXML private DatePicker fecha;
    @FXML private TextField valorDeInscripcion;
    @FXML private ComboBox<String> comboBoxCategoria;
    @FXML private ComboBox<String> comboBoxTipoTorneo;
    @FXML private ImageView botonBack;

    private final GenericDAO<Torneo> torneoDAO = new TorneoDAOImpl();
    private final TurnoDAOImpl turnoDAO = new TurnoDAOImpl();
    private Torneo torneoActual;

    private LocalDate fechaAnterior;

    @FXML
    public void initialize() {
        comboBoxCategoria.getItems().addAll("1°","2°","3°","4°","5°","6°","7°","8°","9°","10°");
        comboBoxTipoTorneo.getItems().addAll("damas","caballeros","mixto");
        cargarDatosTorneo();

        // Configurar DatePicker para seleccionar solo viernes, sábados y domingos en los próximos 3 meses
        fecha.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                LocalDate hoy = LocalDate.now();
                LocalDate tresMeses = hoy.plusMonths(3);

                if (item.isBefore(hoy) || item.isAfter(tresMeses)
                        || (item.getDayOfWeek() != java.time.DayOfWeek.FRIDAY
                        && item.getDayOfWeek() != java.time.DayOfWeek.SATURDAY
                        && item.getDayOfWeek() != java.time.DayOfWeek.SUNDAY)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // opcional: colorear los días deshabilitados
                }
            }
        });

        // Listener para generar turnos cuando se cambia la fecha
        fecha.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                // Liberar turnos de la fecha anterior
                if (fechaAnterior != null && !fechaAnterior.equals(newDate)) {
                    liberarTurnos(fechaAnterior);
                }
                fechaAnterior = newDate;

                // Generar turnos para la nueva fecha si no existen
                generarTurnosDelDia(newDate);
            }
        });
    }

    private void cargarDatosTorneo() {
        Object datos = NavigationHelper.getDatos();
        if (datos instanceof Torneo) {
            torneoActual = (Torneo) datos;
            NavigationHelper.clearDatos();

            primerPremio.setText(torneoActual.getPremio1());
            segundoPremio.setText(torneoActual.getPremio2());
            valorDeInscripcion.setText(String.valueOf(torneoActual.getValor_Inscripcion()));
            comboBoxCategoria.setValue(torneoActual.getCategoria() + "°");
            comboBoxTipoTorneo.setValue(convertirEnumATexto(torneoActual.getTipo()));
            fecha.setValue(torneoActual.getFecha());

            // Guardar fecha inicial para liberar turnos si se cambia
            fechaAnterior = torneoActual.getFecha();
        } else {
            System.err.println("No se recibieron datos válidos del torneo en NavigationHelper.");
        }
    }

    private void generarTurnosDelDia(LocalDate fecha) {
        LocalTime[] horarios = { LocalTime.of(12,0), LocalTime.of(14,0), LocalTime.of(16,0),
                LocalTime.of(18,0), LocalTime.of(20,0), LocalTime.of(22,0) };

        CanchaDAOImpl canchaDAO = new CanchaDAOImpl();
        List<Cancha> canchas = canchaDAO.findAll();

        for (Cancha cancha : canchas) {
            for (LocalTime hora : horarios) {
                if (!turnoDAO.existeTurno(cancha.getNumero(), fecha, hora)) {
                    Turno turno = new Turno();
                    turno.setFecha(fecha);
                    turno.setHora(hora);
                    turno.setEstado(E.Libre);
                    turno.setPago(0);
                    turno.setPersona(null);
                    turno.setCancha(cancha);
                    turno.setFecha_Pago(null);
                    turno.setFecha_Cancelacion(null);
                    turno.setReintegro_Cancelacion(null);
                    turnoDAO.create(turno);
                    System.out.println("Turno generado: " + fecha + " - " + hora + " - Cancha " + cancha.getNumero());
                }
            }
        }
    }

    private void liberarTurnos(LocalDate fecha) {
        List<Turno> turnos = turnoDAO.findAll();
        for (Turno t : turnos) {
            if (t.getFecha().equals(fecha)) {
                t.setEstado(E.Libre);
                t.setPersona(null);
                t.setPago(0);
                t.setFecha_Pago(null);
                t.setFecha_Cancelacion(null);
                t.setReintegro_Cancelacion(null);
                turnoDAO.update(t);
            }
        }
        System.out.println("Turnos liberados para: " + fecha);
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
        turnoDAO.ocuparTurnosPorFecha(torneoActual.getFecha()); // Ocupa todos los turnos de esa fecha
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