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
        // leo valores del formulario
        String categoriaStr = comboBoxCategoria.getValue();
        String tipoTorneo = comboBoxTipoTorneo.getValue();
        String premio1 = primerPremio.getText() == null ? "" : primerPremio.getText().trim();
        String premio2 = segundoPremio.getText() == null ? "" : segundoPremio.getText().trim();
        String inscripcionText = valorDeInscripcion.getText() == null ? "" : valorDeInscripcion.getText().trim();
        LocalDate fechaTorneo = fecha.getValue();

        if (categoriaStr == null || categoriaStr.isBlank() || tipoTorneo == null || tipoTorneo.isBlank() || premio1.isEmpty() || premio2.isEmpty() || inscripcionText.isEmpty() || fechaTorneo == null) {
            mostrarAlerta("Error", "Por favor, complete todos los campos");
            return;
        }

        if (turnoDAO.hayTurnosOcupadosEnFecha(fechaTorneo)) {
            mostrarAlerta("Error", "No se puede crear el torneo: hay turnos ocupados para esa fecha.");
            return;
        }

        int categoria = obtenerCategoriaNumerica();
        T tipoDeTorneo = convertirTipoTorneo(tipoTorneo);

        final int inscripcion;
        try {
            inscripcion = validarYConvertirPrecio(inscripcionText); // tu función que lanza IllegalArgumentException si no es válido
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage()); // por ejemplo: "Ingrese un valor numérico para la inscripción"
            return;
        }

        generarTurnosDelDia(fechaTorneo);

        String mensaje = String.format("¿Seguro que deseas crear el torneo con los siguientes datos?\n\n" + "Categoría: %d\n" + "Fecha: %s\n" + "Tipo: %s\n" + "Premio 1: %s\n" + "Premio 2: %s\n" + "Inscripción: %d", categoria, fechaTorneo != null ? fechaTorneo.toString() : "—", tipoTorneo, premio1, premio2, inscripcion);

        // Alerta de confirmación con dos botones
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar creación");
        alertaConfirmacion.setHeaderText("Confirmación requerida");
        alertaConfirmacion.setContentText(mensaje);

        ButtonType botonConfirmar = new ButtonType("Confirmar");
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertaConfirmacion.getButtonTypes().setAll(botonConfirmar, botonCancelar);

        alertaConfirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == botonConfirmar) {
                try {

                    Torneo torneo = crearTorneo(categoria, tipoDeTorneo, premio1, premio2, fechaTorneo, inscripcion);
                    torneoDAO.create(torneo);
                    turnoDAO.ocuparTurnosPorFecha(torneo.getFecha());

                    mostrarAlerta("Éxito", "Torneo creado correctamente.");

                    // Navegar a la pantalla de listar torneos
                    Stage stage = (Stage) comboBoxTipoTorneo.getScene().getWindow();
                    NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "Torneos");

                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "Ocurrió un error al crear el torneo.");
                }
            } else {
                // Si cancela no hacemos nada y el formulario queda con los mismos datos
            }
        });
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
            int precio = Integer.parseInt(precioTexto); // intenta convertir a número

            if (precio < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo");
            }

            return precio;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido");
        }
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

}

