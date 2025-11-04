package controllers;

import DAO.impl.EquipoDAOImpl;
import DAO.impl.PartidoDAOImpl;
import DAO.impl.TorneoDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Equipo;
import models.Es;
import models.Partido;
import models.Torneo;
import utilities.NavigationHelper;
import utilities.Paths;
import javafx.scene.control.ButtonType;

import java.time.LocalTime;
import java.util.*;

public class GestionarTorneoController {

    // --- Vistas FXML ---
    @FXML private ImageView botonBack;
    @FXML private Text txtEquipoCampeon;
    @FXML private Text txtEquipoCuartos1, txtEquipoCuartos2, txtEquipoCuartos3, txtEquipoCuartos4,
            txtEquipoCuartos5, txtEquipoCuartos6, txtEquipoCuartos7, txtEquipoCuartos8;
    @FXML private Text txtEquipoSemi1, txtEquipoSemi2, txtEquipoSemi3, txtEquipoSemi4;
    @FXML private Text txtEquipoFinal1, txtEquipoFinal2;
    @FXML private Label txtFechaInicio, txtGenero, txtCantidadInscriptos, txtCategoria;
    @FXML private Pane btnArmarCruces, btnComenzarTorneo, partidoC1, partidoC2, partidoC3, partidoC4, partidoF, partidoS1, partidoS2;

    // --- Variables ---
    private int totalInscriptos;
    private boolean crucesArmados = false;
    private Torneo torneoActual;

    // --- DAOs ---
    private final TorneoDAOImpl torneoDAO = new TorneoDAOImpl();
    private final EquipoDAOImpl equipoDAO = new EquipoDAOImpl();
    private final PartidoDAOImpl partidoDAO = new PartidoDAOImpl();
    @FXML
    private void initialize() {
        Object datos = NavigationHelper.getDatos();
        if (datos instanceof Torneo) {
            torneoActual = (Torneo) datos;
            NavigationHelper.clearDatos();
        } else {
            System.err.println("No se recibieron datos válidos del torneo.");
            return;
        }

        // --- Mostrar información general del torneo ---
        txtCategoria.setText(String.valueOf(torneoActual.getCategoria()));
        txtFechaInicio.setText(torneoActual.getFecha() != null ? torneoActual.getFecha().toString() : "Sin fecha");
        txtGenero.setText(torneoActual.getTipo() != null ? torneoActual.getTipo().toString() : "N/A");

        // =================== EQUIPOS =================== //
        List<Equipo> equiposBD = equipoDAO.findByTorneoId(torneoActual.getId());
        if (equiposBD == null || equiposBD.isEmpty()) {
            System.out.println("El torneo no tiene equipos inscriptos todavía.");
            txtCantidadInscriptos.setText("0");
        } else {
            txtCantidadInscriptos.setText(String.valueOf(equiposBD.size()));
            Equipo[] arrayEquipos = new Equipo[8];
            for (int i = 0; i < equiposBD.size() && i < 8; i++) {
                arrayEquipos[i] = equiposBD.get(i);
            }
            torneoActual.setEquipos(arrayEquipos);
        }

        // =================== PARTIDOS =================== //
        List<Partido> partidos = partidoDAO.findByTorneo(torneoActual.getId());
        if (partidos == null || partidos.isEmpty()) {
            System.out.println("No hay partidos cargados para este torneo (aún no se generaron los cruces).");

            // Limpiar todos los cuadros de texto de cruces
            txtEquipoCuartos1.setText("");
            txtEquipoCuartos2.setText("");
            txtEquipoCuartos3.setText("");
            txtEquipoCuartos4.setText("");
            txtEquipoCuartos5.setText("");
            txtEquipoCuartos6.setText("");
            txtEquipoCuartos7.setText("");
            txtEquipoCuartos8.setText("");
            txtEquipoSemi1.setText("");
            txtEquipoSemi2.setText("");
            txtEquipoSemi3.setText("");
            txtEquipoSemi4.setText("");
            txtEquipoFinal1.setText("");
            txtEquipoFinal2.setText("");
            txtEquipoCampeon.setText("");
            return;
        }
        crucesArmados = true;

        // =================== MOSTRAR CRUCES (CUARTOS) =================== //
        for (int i = 0; i < partidos.size() && i < 4; i++) {
            Partido p = partidos.get(i);
            if (p.getEquipo1() != null)
                p.setEquipo1(equipoDAO.findById(p.getEquipo1().getId()));
            if (p.getEquipo2() != null)
                p.setEquipo2(equipoDAO.findById(p.getEquipo2().getId()));

            switch (i) {
                case 0:
                    txtEquipoCuartos1.setText(p.getEquipo1() != null ? p.getEquipo1().getNombre() : "");
                    txtEquipoCuartos2.setText(p.getEquipo2() != null ? p.getEquipo2().getNombre() : "");
                    break;
                case 1:
                    txtEquipoCuartos3.setText(p.getEquipo1() != null ? p.getEquipo1().getNombre() : "");
                    txtEquipoCuartos4.setText(p.getEquipo2() != null ? p.getEquipo2().getNombre() : "");
                    break;
                case 2:
                    txtEquipoCuartos5.setText(p.getEquipo1() != null ? p.getEquipo1().getNombre() : "");
                    txtEquipoCuartos6.setText(p.getEquipo2() != null ? p.getEquipo2().getNombre() : "");
                    break;
                case 3:
                    txtEquipoCuartos7.setText(p.getEquipo1() != null ? p.getEquipo1().getNombre() : "");
                    txtEquipoCuartos8.setText(p.getEquipo2() != null ? p.getEquipo2().getNombre() : "");
                    break;
            }
        }

        // =================== GANADORES SEMIS =================== //
        if (partidos.size() >= 4) {
            if (partidos.get(0).getGanador() != null)
                txtEquipoSemi1.setText(equipoDAO.findById(partidos.get(0).getGanador().getId()).getNombre());
            if (partidos.get(1).getGanador() != null)
                txtEquipoSemi2.setText(equipoDAO.findById(partidos.get(1).getGanador().getId()).getNombre());
            if (partidos.get(2).getGanador() != null)
                txtEquipoSemi3.setText(equipoDAO.findById(partidos.get(2).getGanador().getId()).getNombre());
            if (partidos.get(3).getGanador() != null)
                txtEquipoSemi4.setText(equipoDAO.findById(partidos.get(3).getGanador().getId()).getNombre());
        }

        // =================== GANADORES FINAL =================== //
        if (partidos.size() >= 6) {
            if (partidos.get(4).getGanador() != null)
                txtEquipoFinal1.setText(equipoDAO.findById(partidos.get(4).getGanador().getId()).getNombre());
            if (partidos.get(5).getGanador() != null)
                txtEquipoFinal2.setText(equipoDAO.findById(partidos.get(5).getGanador().getId()).getNombre());
        }

        // =================== CAMPEÓN =================== //
        if (partidos.size() >= 7) {
            Partido finalPartido = partidos.get(6);
            if (finalPartido.getGanador() != null) {
                Equipo campeon = equipoDAO.findById(finalPartido.getGanador().getId());
                if (campeon != null)
                    txtEquipoCampeon.setText(campeon.getNombre());
            }
        }
    }

    // =================== DATOS DEL TORNEO =================== //
    private void cargarDatosTorneo() {
        txtFechaInicio.setText(torneoActual.getFecha().toString());
        txtCategoria.setText("Cat: " + torneoActual.getCategoria() + "°");
        txtGenero.setText(torneoActual.getTipo().toString());
        totalInscriptos = equipoDAO.contarEquiposPorTorneo(torneoActual.getId());
        txtCantidadInscriptos.setText(String.valueOf(totalInscriptos));
    }

    // =================== BOTÓN: ARMAR CRUCES =================== //
    @FXML
    void botonArmarCruces(MouseEvent event) {
        // Verificamos que el torneo y los equipos existan
        if (torneoActual == null || torneoActual.getEquipos() == null || torneoActual.getEquipos().length < 8) {
            mostrarAlerta("Error", "El torneo no tiene 8 equipos cargados.", Alert.AlertType.ERROR);
            return;
        }

        if (crucesArmados) {
            mostrarAlerta("Aviso", "Los cruces ya fueron armados.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            // Creamos los 7 partidos (4 cuartos, 2 semis, 1 final)
            Partido[] partidos = new Partido[7];
            Equipo[] equipos = torneoActual.getEquipos();

            // --- CUARTOS DE FINAL ---
            for (int i = 0; i < 4; i++) {
                Partido p = new Partido();
                p.setInstancia(i);                // 0,1,2,3 = Cuartos
                p.setTorneo(torneoActual);
                p.setEquipo1(equipos[i * 2]);     // (0,2,4,6)
                p.setEquipo2(equipos[i * 2 + 1]); // (1,3,5,7)
                p.setJugado(false);
                p.setPuntos(0);
                p.setHora(LocalTime.of(0, 0));

                partidoDAO.create(p); // Guarda en la BD
                partidos[i] = p;
            }

            // --- SEMIFINALES ---
            for (int i = 0; i < 2; i++) {
                Partido p = new Partido();
                p.setInstancia(4 + i); // 4,5 = Semis
                p.setTorneo(torneoActual);
                p.setEquipo1(null); // se llenará con ganadores de cuartos
                p.setEquipo2(null);
                p.setJugado(false);
                p.setPuntos(0);
                p.setHora(LocalTime.of(0, 0));

                partidoDAO.create(p);
                partidos[4 + i] = p;
            }

            // --- FINAL ---
            Partido finalPartido = new Partido();
            finalPartido.setInstancia(6); // 6 = Final
            finalPartido.setTorneo(torneoActual);
            finalPartido.setEquipo1(null);
            finalPartido.setEquipo2(null);
            finalPartido.setJugado(false);
            finalPartido.setPuntos(0);
            finalPartido.setHora(LocalTime.of(0, 0));

            partidoDAO.create(finalPartido);
            partidos[6] = finalPartido;

            // Guardamos los partidos en el torneo actual
            torneoActual.setPartidos(partidos);

            // Marcamos que los cruces ya se armaron
            crucesArmados = true;

            mostrarAlerta("Éxito", "Los cruces del torneo se armaron correctamente.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al armar los cruces.", Alert.AlertType.ERROR);
        }
    }


    // =================== BOTÓN: GESTIONAR EQUIPOS =================== //
    @FXML private void botonGestionarEquipos() {
        Stage stage = (Stage) btnComenzarTorneo.getScene().getWindow();
        NavigationHelper.cambiarVistaConDatos(stage, Paths.pantallaGestionarEquipos, "Gestionar Equipos", torneoActual);
        System.out.println("cambiando la ventana");
    }

    // =================== BOTÓN: COMENZAR TORNEO =================== //
    @FXML
    void botonComenzarTorneo(MouseEvent event) {
        if (!crucesArmados) {
            mostrarAlerta("No se puede comenzar", "Primero debes armar los cruces.",Alert.AlertType.INFORMATION);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar inicio");
        alert.setHeaderText("Comenzar torneo");
        alert.setContentText("Una vez iniciado, no podrá eliminarlo. ¿Desea continuar?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            torneoActual.setEstados(Es.En_Curso);
            torneoDAO.update(torneoActual);
            mostrarInfo("Torneo iniciado", "El torneo ha comenzado correctamente.");
        }

    }

    // =================== ABRIR PARTIDO =================== //
    @FXML void handlepartidoC1(MouseEvent event) {abrirVentanaCargarPartido(0);  }
    @FXML void handlepartidoC2(MouseEvent event) { abrirVentanaCargarPartido(1); }
    @FXML void handlepartidoC3(MouseEvent event) { abrirVentanaCargarPartido(2); }
    @FXML void handlepartidoC4(MouseEvent event) { abrirVentanaCargarPartido(3); }
    @FXML void handlepartidoS1(MouseEvent event) { abrirVentanaCargarPartido(4); }
    @FXML void handlepartidoS2(MouseEvent event) {abrirVentanaCargarPartido(5);  }
    @FXML void handlepartidoF(MouseEvent event)  { abrirVentanaCargarPartido(6); }

    private void abrirVentanaCargarPartido(int indicePartido) {
        Partido[] partidos = torneoActual.getPartidos();
        System.out.println("Partidos cargados: " + Arrays.toString(partidos));

        if (partidos == null || indicePartido < 0 || indicePartido >= partidos.length) {
            System.out.println("️ No hay array de partidos válido");
            return;
        }

        Partido partido = partidos[indicePartido];
        if (partido == null) {
            System.out.println("️ Partido en índice " + indicePartido + " es null");
            return;
        }

        System.out.println(" Abriendo popup de partido: " + partido);
        NavigationHelper.abrirPopupConDatos(
                Paths.pantallaCargarPartido,
                "Cargar Partido",
                partido,
                453, 407
        );
    }

    // =================== UTILIDADES =================== //
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    @FXML
    void handleBackButton(MouseEvent e) {
        Stage stage = (Stage) botonBack.getScene().getWindow();
        NavigationHelper.cambiarVista(stage, Paths.pantallaTorneos, "Listar Torneos");
    }
}
