package controllers;

import DAO.impl.PartidoDAOImpl;
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
import utilities.NavigationHelper;

import java.sql.SQLException;

public class CargarPartidoController {

    @FXML
    private Label lblEquipo1;

    @FXML
    private Label lblEquipo2;

    @FXML
    private TextField txtSet1, txtSet1b, txtSet2, txtSet2b, txtSet3, txtSet3b;

    @FXML
    private javafx.scene.layout.Pane botonCrear;

    private Partido partidoActual;
    private PartidoDAOImpl partidoDAO = new PartidoDAOImpl();

    @FXML
    public void initialize() {
        Partido partido = (Partido) NavigationHelper.getDatos();
        if (partido != null) {
            lblEquipo1.setText(partido.getEquipo1().getNombre());
            lblEquipo2.setText(partido.getEquipo2().getNombre());
        }
    }

    // =================== GUARDAR RESULTADO =================== //
    @FXML
    void handleCrear(MouseEvent event) {
        Partido partido = (Partido) NavigationHelper.getDatos();

        if (partido == null) {
            System.out.println("⚠️ No se recibió partido.");
            return;
        }

        try {
            int set1a = Integer.parseInt(txtSet1.getText());
            int set1b = Integer.parseInt(txtSet1b.getText());
            int set2a = Integer.parseInt(txtSet2.getText());
            int set2b = Integer.parseInt(txtSet2b.getText());
            int set3a = txtSet3.getText().isEmpty() ? 0 : Integer.parseInt(txtSet3.getText());
            int set3b = txtSet3b.getText().isEmpty() ? 0 : Integer.parseInt(txtSet3b.getText());

            // Calcular ganador por sets
            int setsGanados1 = 0, setsGanados2 = 0;
            if (set1a > set1b) setsGanados1++;
            else setsGanados2++;
            if (set2a > set2b) setsGanados1++;
            else setsGanados2++;
            if (set3a != 0 || set3b != 0) {
                if (set3a > set3b) setsGanados1++;
                else setsGanados2++;
            }

            Equipo ganador = (setsGanados1 > setsGanados2) ? partido.getEquipo1() : partido.getEquipo2();

            // Actualizar el objeto partido con sets y ganador
            partido.setSet1(txtSet1.getText() + "-" + txtSet1b.getText());
            partido.setSet2(txtSet2.getText() + "-" + txtSet2b.getText());
            partido.setSet3(txtSet3.getText() + "-" + txtSet3b.getText());
            partido.setGanador(ganador);
            partido.setJugado(true);

            // Persistir en BD (asegurate de tener el DAO con update)
            PartidoDAOImpl dao = new PartidoDAOImpl();
            dao.update(partido);

            // Actualizar siguiente instancia en memoria (para que la UI y el torneoActual lo tengan)
            Torneo torneo = partido.getTorneo();
            if (torneo != null) {
                int instancia = partido.getInstancia();
                Partido[] partidos = torneo.getPartidos();
                if (partidos == null) {
                    partidos = new Partido[7];
                    torneo.setPartidos(partidos);
                }
                // si fue un cuarto (0..3) -> semis: 4 + (instancia/2)
                if (instancia >= 0 && instancia <= 3) {
                    int semiIndex = 4 + (instancia / 2);
                    if (partidos[semiIndex] == null) {
                        Partido semi = new Partido();
                        semi.setInstancia(semiIndex);
                        semi.setTorneo(torneo);
                        partidos[semiIndex] = semi;
                    }
                    if (instancia % 2 == 0) partidos[semiIndex].setEquipo1(ganador);
                    else partidos[semiIndex].setEquipo2(ganador);
                }
                // si fue una semi (4,5) -> final (6)
                if (instancia == 4 || instancia == 5) {
                    if (partidos[6] == null) {
                        Partido fin = new Partido();
                        fin.setInstancia(6);
                        fin.setTorneo(torneo);
                        partidos[6] = fin;
                    }
                    if (instancia == 4) partidos[6].setEquipo1(ganador);
                    else partidos[6].setEquipo2(ganador);
                }

                // opcional: persistir cambio de la instancia siguiente en BD
                // si creaste/actualizaste un partido sem/final debes usar partidoDAO.update(...) para el partido afectado
                // ejemplo:
                if (instancia >= 0 && instancia <= 3) {
                    Partido semi = partidos[4 + (instancia / 2)];
                    if (semi != null) {
                        dao.update(semi); // actualiza id_equipo1/id_equipo2 si corresponde
                    }
                } else if (instancia == 4 || instancia == 5) {
                    if (partidos[6] != null) dao.update(partidos[6]);
                }
            }

            // Notificar al controlador principal (si está registrado)
            GestionarTorneoController controladorPrincipal =
                    NavigationHelper.getController(GestionarTorneoController.class);

            if (controladorPrincipal != null) {
                // mandamos el partido que acabamos de actualizar (el mismo que se guardó en BD)
                controladorPrincipal.actualizarPartido(partido);
            }

            // limpiar datos y cerrar popup
            NavigationHelper.clearDatos();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (NumberFormatException ex) {
            System.out.println("⚠️ Error en formato de número.");
        }
    }



    // =================== MÉTODOS AUXILIARES =================== //
    private int ganadorDeSet(String s1, String s2) {
        try {
            int p1 = Integer.parseInt(s1.trim());
            int p2 = Integer.parseInt(s2.trim());
            if (p1 > p2) return 1;
        } catch (Exception ignored) {}
        return 0;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
