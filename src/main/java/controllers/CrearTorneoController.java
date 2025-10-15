package controllers;

import DAO.GenericDAO;
import DAO.impl.CanchaDAOImpl;
import DAO.impl.TurnoDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.stage.Stage;
import models.*;
import utilities.NavigationHelper;
import utilities.Paths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import DAO.impl.TorneoDAOImpl;

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

    TurnoDAOImpl turnoDAO = new TurnoDAOImpl();

    private GenericDAO<Torneo> torneoDAO = new TorneoDAOImpl();
    //---------------------------------Variables------------------------------------------
    //---------------------------------Inicializar comboBox-------------------------------
    @FXML
    public void initialize(){
        comboBoxCategoria.getItems().addAll("1", "2°", "3°", "4°", "5°", "6°", "7°", "8°", "9°", "10°");
        comboBoxTipoTorneo.getItems().addAll("damas", "caballeros", "mixto");
        fecha.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        LocalDate hoy = LocalDate.now();
                        LocalDate tresMeses = hoy.plusMonths(3);

                        // Deshabilitar días fuera del rango o que no sean sábados/domingo
                        if (item.isBefore(hoy) || item.isAfter(tresMeses)
                                || (item.getDayOfWeek() != java.time.DayOfWeek.FRIDAY
                                && item.getDayOfWeek() != java.time.DayOfWeek.SATURDAY
                                && item.getDayOfWeek() != java.time.DayOfWeek.SUNDAY)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // opcional
                        }
                    }
                };
            }
        });
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

    private void generarTurnosDelDia(LocalDate fecha) {
        LocalTime[] horarios = { LocalTime.of(12,0), LocalTime.of(14,0), LocalTime.of(16,0),
                LocalTime.of(18,0), LocalTime.of(20,0), LocalTime.of(22,0) };

        CanchaDAOImpl canchaDAO = new CanchaDAOImpl();
        List<Cancha> canchas = canchaDAO.findAll();

        for (Cancha cancha : canchas) {
            for (LocalTime hora : horarios) {
                // Verificar si ya existe el turno
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

        System.out.println("Turnos generados para " + fecha);
    }

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
        generarTurnosDelDia(fechaTorneo);
        if (!validarCampos(categoria, tipoTorneo, premio1, premio2, fechaTorneo, inscripcion)) {//validamos quetodo este bien cargado
            mostrarAlerta("Error", "Por favor complete todos los campos");//en caso de algo mal cargado
            return;
        } else {
            Torneo torneo = crearTorneo(categoria, tipoDeTorneo, premio1, premio2, fechaTorneo, inscripcion);
            torneoDAO.create(torneo);//enviamos el torneo a la bd

            turnoDAO.ocuparTurnosPorFecha(torneo.getFecha()); // Ocupa todos los turnos de esa fecha

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

