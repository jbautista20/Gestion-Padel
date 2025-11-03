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

    // =================== INICIALIZACIÓN =================== //
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

        cargarDatosTorneo();

        // Verificar si ya existen cruces
        List<Partido> partidos = partidoDAO.obtenerCrucesPorTorneo(torneoActual.getId());
        if (partidos != null && !partidos.isEmpty()) {
            crucesArmados = true;
        }


        btnArmarCruces.setOnMouseClicked(e -> botonArmarCruces(e));
        btnComenzarTorneo.setOnMouseClicked(e -> botonComenzarTorneo(e));
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
        List<Equipo> equipos = equipoDAO.findByTorneoId(torneoActual.getId());
        totalInscriptos = equipos.size();

        if (totalInscriptos < 8) {
            mostrarAlerta("No se pueden armar los cruces", "Se necesitan 8 equipos inscriptos.");
            return;
        }
        if (crucesArmados) {
            mostrarAlerta("Armar cruces", "Los cruces ya fueron generados.");
            return;
        }

        crucesArmados = true;
        Collections.shuffle(equipos);

        Partido[] partidos = new Partido[7];

        // --- Cuartos de final (4 partidos con equipos) ---
        for (int i = 0; i < 8; i += 2) {
            Partido p = new Partido();
            p.setHora(LocalTime.of(0, 0));
            p.setInstancia(1); // 1 = Cuartos
            p.setEquipo1(equipos.get(i));
            p.setEquipo2(equipos.get(i + 1));
            p.setTorneo(torneoActual);
            partidoDAO.create(p);
            partidos[i / 2] = p;
        }

        // --- Semifinales (2 partidos vacíos) ---
        for (int i = 4; i < 6; i++) {
            Partido s = new Partido();
            s.setHora(LocalTime.of(0, 0));
            s.setInstancia(2); // 2 = Semis
            s.setEquipo1(null);
            s.setEquipo2(null);
            s.setTorneo(torneoActual);
            partidoDAO.create(s);
            partidos[i] = s;
        }

        // --- Final (vacío) ---
        Partido f = new Partido();
        f.setHora(LocalTime.of(0, 0));
        f.setInstancia(3); // 3 = Final
        f.setEquipo1(null);
        f.setEquipo2(null);
        f.setTorneo(torneoActual);
        partidoDAO.create(f);
        partidos[6] = f;

        torneoActual.setPartidos(partidos);

        mostrarInfo("Cruces armados", "Los cruces fueron generados correctamente.");
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
            mostrarAlerta("No se puede comenzar", "Primero debes armar los cruces.");
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
    @FXML void handlepartidoC1(MouseEvent event) {abrirVentanaCargarPartido(0);
    }
    @FXML void handlepartidoC2(MouseEvent event) { abrirVentanaCargarPartido(1); }
    @FXML void handlepartidoC3(MouseEvent event) { abrirVentanaCargarPartido(2); }
    @FXML void handlepartidoC4(MouseEvent event) { abrirVentanaCargarPartido(3); }
    @FXML void handlepartidoS1(MouseEvent event) { abrirVentanaCargarPartido(4); }
    @FXML void handlepartidoS2(MouseEvent event) {abrirVentanaCargarPartido(5);
    }
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
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
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
