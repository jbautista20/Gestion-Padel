package controllers;

import DAO.impl.PartidoDAOImpl;
import DAO.impl.TorneoDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import models.Partido;
import models.Equipo;
import models.Torneo;
import models.Es;
import models.Cancha;
import utilities.NavigationHelper;
import javafx.scene.control.ComboBox;

import java.time.LocalTime;

public class CargarPartidoController {

    @FXML
    private ComboBox<Integer> comboCancha;

    @FXML
    private ComboBox<Integer> comboHora;

    @FXML
    private Label lblEquipo1;

    @FXML
    private Label lblEquipo2;

    @FXML
    private TextField txtSet1, txtSet1b, txtSet2, txtSet2b, txtSet3, txtSet3b;

    @FXML
    private javafx.scene.layout.Pane botonCrear;

    private PartidoDAOImpl partidoDAO = new PartidoDAOImpl();
    private TorneoDAOImpl torneoDAO = new TorneoDAOImpl();

    @FXML
    public void initialize() {
        Partido partido = (Partido) NavigationHelper.getDatos();

        if (partido != null) {
            lblEquipo1.setText(partido.getEquipo1().getNombre());
            lblEquipo2.setText(partido.getEquipo2().getNombre());
        }

        // --- Inicializar combos ---
        comboCancha.getItems().addAll(1, 2);
        comboHora.getItems().addAll(12, 14, 16, 18, 20, 22);

        // Si el partido ya tenía cancha u hora cargadas, mostrarlas
        if (partido != null) {
            if (partido.getCancha() != null && partido.getCancha().getNumero() != 0) {
                comboCancha.setValue(partido.getCancha().getNumero());
            }
            if (partido.getHora() != null) {
                comboHora.setValue(partido.getHora().getHour());
            }
        }
    }

    // =================== GUARDAR RESULTADO =================== //
    @FXML
    void handleCrear(MouseEvent event) {
        Partido partido = (Partido) NavigationHelper.getDatos();

        if (partido == null) {
            mostrarAlerta("Error", "No se recibió partido válido.", Alert.AlertType.ERROR);
            return;
        }

        Torneo torneo = partido.getTorneo();
        if (torneo == null) {
            mostrarAlerta("Error", "El partido no está asociado a un torneo.", Alert.AlertType.ERROR);
            return;
        }

        // Control: solo se pueden cargar partidos si el torneo está en curso
        if (torneo.getEstados() != Es.En_Curso) {
            mostrarAlerta("Torneo finalizado",
                    "No se pueden cargar resultados. El torneo ya finalizó o no está en curso.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        // --- Validar cancha y hora ---
        Integer canchaSeleccionada = comboCancha.getValue();
        Integer horaSeleccionada = comboHora.getValue();

        if (canchaSeleccionada == null || horaSeleccionada == null) {
            mostrarAlerta("Datos incompletos", "Debes seleccionar la cancha y la hora.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // --- Lectura de sets ---
            int set1a = Integer.parseInt(txtSet1.getText());
            int set1b = Integer.parseInt(txtSet1b.getText());
            int set2a = Integer.parseInt(txtSet2.getText());
            int set2b = Integer.parseInt(txtSet2b.getText());

            boolean hayTercerSet = !txtSet3.getText().isEmpty() && !txtSet3b.getText().isEmpty();
            int set3a = hayTercerSet ? Integer.parseInt(txtSet3.getText()) : 0;
            int set3b = hayTercerSet ? Integer.parseInt(txtSet3b.getText()) : 0;

            // --- Calcular sets ganados ---
            int setsGanados1 = 0, setsGanados2 = 0;
            if (set1a > set1b) setsGanados1++;
            else if (set1b > set1a) setsGanados2++;
            else {
                mostrarAlerta("Error", "No puede haber empate en un set.", Alert.AlertType.ERROR);
                return;
            }

            if (set2a > set2b) setsGanados1++;
            else if (set2b > set2a) setsGanados2++;
            else {
                mostrarAlerta("Error", "No puede haber empate en un set.", Alert.AlertType.ERROR);
                return;
            }

            // --- Validación tercer set ---
            if (setsGanados1 == 1 && setsGanados2 == 1) {
                if (!hayTercerSet) {
                    mostrarAlerta("Resultado incompleto",
                            "Empate en los dos primeros sets. Debes ingresar el tercer set.",
                            Alert.AlertType.WARNING);
                    return;
                }
            } else if ((setsGanados1 == 2 || setsGanados2 == 2) && hayTercerSet) {
                mostrarAlerta("Datos inconsistentes",
                        "Un equipo ya ganó 2 sets. No debe ingresarse un tercer set.",
                        Alert.AlertType.WARNING);
                return;
            }

            // --- Contar tercer set si corresponde ---
            if (hayTercerSet) {
                if (set3a > set3b) setsGanados1++;
                else if (set3b > set3a) setsGanados2++;
                else {
                    mostrarAlerta("Error", "No puede haber empate en un set.", Alert.AlertType.ERROR);
                    return;
                }
            }

            Equipo ganador = (setsGanados1 > setsGanados2) ? partido.getEquipo1() : partido.getEquipo2();

            // --- Actualizar objeto partido ---
            partido.setSet1(set1a + "-" + set1b);
            partido.setSet2(set2a + "-" + set2b);
            partido.setSet3((hayTercerSet ? set3a + "-" + set3b : ""));
            partido.setGanador(ganador);
            partido.setJugado(true);

            // ✅ Crear y asignar objeto Cancha
            Cancha canchaObj = new Cancha();
            canchaObj.setNumero(canchaSeleccionada);
            partido.setCancha(canchaObj);

            partido.setHora(LocalTime.of(horaSeleccionada, 0));

            partidoDAO.update(partido);

            // --- Actualizar siguientes instancias ---
            int instancia = partido.getInstancia();
            Partido[] partidos = torneo.getPartidos();
            if (partidos == null) {
                partidos = new Partido[7];
                torneo.setPartidos(partidos);
            }

            if (instancia >= 0 && instancia <= 3) {
                int semiIndex = 4 + (instancia / 2);
                if (partidos[semiIndex] == null) {
                    Partido semi = new Partido();
                    semi.setInstancia(semiIndex);
                    semi.setTorneo(torneo);
                    partidos[semiIndex] = semi;
                }
                if (instancia % 2 == 0)
                    partidos[semiIndex].setEquipo1(ganador);
                else
                    partidos[semiIndex].setEquipo2(ganador);
                partidoDAO.update(partidos[semiIndex]);
            }

            if (instancia == 4 || instancia == 5) {
                if (partidos[6] == null) {
                    Partido fin = new Partido();
                    fin.setInstancia(6);
                    fin.setTorneo(torneo);
                    partidos[6] = fin;
                }
                if (instancia == 4)
                    partidos[6].setEquipo1(ganador);
                else
                    partidos[6].setEquipo2(ganador);
                partidoDAO.update(partidos[6]);
            }

            // Si fue la final (instancia == 6), marcar torneo como finalizado
            if (instancia == 6) {
                torneo.setEstados(Es.Finalizado);
                torneoDAO.update(torneo);
                mostrarAlerta("Torneo finalizado",
                        "¡" + ganador.getNombre() + " es el campeón del torneo!",
                        Alert.AlertType.INFORMATION);
            }

            // --- Notificar controlador principal ---
            GestionarTorneoController controladorPrincipal =
                    NavigationHelper.getController(GestionarTorneoController.class);
            if (controladorPrincipal != null) {
                controladorPrincipal.actualizarPartido(partido);
            }

            // --- Cerrar popup ---
            NavigationHelper.clearDatos();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (NumberFormatException ex) {
            mostrarAlerta("Error de formato", "Debes ingresar números válidos en todos los sets.", Alert.AlertType.ERROR);
        }
    }

    // =================== MÉTODOS AUXILIARES =================== //
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
