package controllers;

import DAO.impl.EquipoDAOImpl;
import DAO.impl.PartidoDAOImpl;
import DAO.impl.TorneoDAOImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import java.util.stream.Collectors;

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
    private ObservableList<Partido> partidosObservable = FXCollections.observableArrayList();


    // --- Variables ---
    private boolean crucesArmados = false;
    private Torneo torneoActual;

    // --- DAOs ---
    private final TorneoDAOImpl torneoDAO = new TorneoDAOImpl();
    private final EquipoDAOImpl equipoDAO = new EquipoDAOImpl();
    private final PartidoDAOImpl partidoDAO = new PartidoDAOImpl();

    @FXML
    private void initialize() {
        NavigationHelper.registrarControlador(this);
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
        txtCantidadInscriptos.setText(String.valueOf(equipoDAO.contarEquiposPorTorneo(torneoActual.getId())));

        // Añadir listener antes de poblar
        partidosObservable.addListener((ListChangeListener<Partido>) change -> {
            Platform.runLater(this::actualizarVistaCruces);
        });

        // Reconstruir el torneo (esta llamada carga equipos y partidos desde BD)
        reconstruirTorneoDesdeBD();

        // Si no hay partidos, limpiar la UI (actualizarVistaCruces ya maneja limpieza)
        if (partidosObservable.isEmpty()) {
            limpiarVistaCruces();
        } else {
            // actualizarVistaCruces se ejecutará vía listener, pero forzamos una vez
            Platform.runLater(this::actualizarVistaCruces);
            crucesArmados = true;
        }
    }


    private void reconstruirTorneoDesdeBD() {
        if (torneoActual == null) return;

        // 1) Cargar equipos desde BD y setear en torneoActual
        List<Equipo> equiposBD = equipoDAO.findByTorneoId(torneoActual.getId());
        Equipo[] arrayEquipos = new Equipo[8];
        if (equiposBD != null) {
            for (int i = 0; i < equiposBD.size() && i < 8; i++) {
                arrayEquipos[i] = equiposBD.get(i);
            }
        }
        torneoActual.setEquipos(arrayEquipos);

        // 2) Cargar partidos desde BD (ordenados por instancia)
        List<Partido> partidosDesdeBD = partidoDAO.findByTorneo(torneoActual.getId());
        if (partidosDesdeBD == null) partidosDesdeBD = new ArrayList<>();

        // 3) Para cada partido, resolver equipos completos y ganador desde BD
        for (Partido p : partidosDesdeBD) {
            if (p.getEquipo1() != null && p.getEquipo1().getId() != 0) {
                Equipo e1 = equipoDAO.findById(p.getEquipo1().getId());
                p.setEquipo1(e1);
            }
            if (p.getEquipo2() != null && p.getEquipo2().getId() != 0) {
                Equipo e2 = equipoDAO.findById(p.getEquipo2().getId());
                p.setEquipo2(e2);
            }
            if (p.getGanador() != null && p.getGanador().getId() != 0) {
                Equipo g = equipoDAO.findById(p.getGanador().getId());
                p.setGanador(g);
            }
            // relacionar el torneo (opcional)
            p.setTorneo(torneoActual);
        }

        // 4) Guardar en torneoActual.partidos como arreglo (tamaño 7)
        Partido[] arrayPartidos = new Partido[7];
        for (Partido p : partidosDesdeBD) {
            int idx = p.getInstancia(); // asumimos instancia: 0..6
            if (idx >= 0 && idx < arrayPartidos.length) {
                arrayPartidos[idx] = p;
            }
        }
        torneoActual.setPartidos(arrayPartidos);

        // 5) Actualizar la ObservableList para que la UI reaccione
        partidosObservable.setAll(partidosDesdeBD);
    }

    private void limpiarVistaCruces() {
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
    }


    private void actualizarVistaCruces() {
        List<Partido> partidos = new ArrayList<>(partidosObservable);

        // --- Limpiamos todo primero ---
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

        // --- Mostrar cuartos (partidos 0..3) ---
        for (int i = 0; i < partidos.size() && i < 4; i++) {
            Partido p = partidos.get(i);

            // asegurar que equipo1/equipo2 estén completos (leer desde BD por id si es necesario)
            Equipo e1 = null, e2 = null;
            if (p.getEquipo1() != null && p.getEquipo1().getId() != 0) {
                e1 = equipoDAO.findById(p.getEquipo1().getId());
            }
            if (p.getEquipo2() != null && p.getEquipo2().getId() != 0) {
                e2 = equipoDAO.findById(p.getEquipo2().getId());
            }

            switch (i) {
                case 0:
                    txtEquipoCuartos1.setText(e1 != null ? e1.getNombre() : "");
                    txtEquipoCuartos2.setText(e2 != null ? e2.getNombre() : "");
                    break;
                case 1:
                    txtEquipoCuartos3.setText(e1 != null ? e1.getNombre() : "");
                    txtEquipoCuartos4.setText(e2 != null ? e2.getNombre() : "");
                    break;
                case 2:
                    txtEquipoCuartos5.setText(e1 != null ? e1.getNombre() : "");
                    txtEquipoCuartos6.setText(e2 != null ? e2.getNombre() : "");
                    break;
                case 3:
                    txtEquipoCuartos7.setText(e1 != null ? e1.getNombre() : "");
                    txtEquipoCuartos8.setText(e2 != null ? e2.getNombre() : "");
                    break;
            }
        }

        // --- Semifinalistas: ganadores de cuartos (partidos 0..3 -> ganadores) ---
        if (partidos.size() >= 4) {
            for (int i = 0; i < 4; i++) {
                Partido p = partidos.get(i);
                if (p.getGanador() != null && p.getGanador().getId() != 0) {
                    Equipo ganador = equipoDAO.findById(p.getGanador().getId());
                    switch (i) {
                        case 0: txtEquipoSemi1.setText(ganador != null ? ganador.getNombre() : ""); break;
                        case 1: txtEquipoSemi2.setText(ganador != null ? ganador.getNombre() : ""); break;
                        case 2: txtEquipoSemi3.setText(ganador != null ? ganador.getNombre() : ""); break;
                        case 3: txtEquipoSemi4.setText(ganador != null ? ganador.getNombre() : ""); break;
                    }
                }
            }
        }

        // --- Finalistas: ganadores de semis (partidos 4,5) ---
        if (partidos.size() >= 6) {
            if (partidos.get(4).getGanador() != null)
                txtEquipoFinal1.setText(equipoDAO.findById(partidos.get(4).getGanador().getId()).getNombre());
            if (partidos.get(5).getGanador() != null)
                txtEquipoFinal2.setText(equipoDAO.findById(partidos.get(5).getGanador().getId()).getNombre());
        }

        // --- Campeón: ganador del partido 6 ---
        if (partidos.size() >= 7) {
            Partido finalPartido = partidos.get(6);
            if (finalPartido.getGanador() != null) {
                Equipo campeon = equipoDAO.findById(finalPartido.getGanador().getId());
                if (campeon != null)
                    txtEquipoCampeon.setText(campeon.getNombre());
            }
        }
    }

    // =================== BOTÓN: ARMAR CRUCES =================== //
    @FXML
    void botonArmarCruces(MouseEvent event) {
        // Verificar que haya un torneo activo
        if (torneoActual == null) {
            mostrarAlerta("Error", "No hay un torneo seleccionado.", Alert.AlertType.ERROR);
            return;
        }

        // Verificar que haya al menos 8 equipos válidos (no nulos)
        Equipo[] equipos = torneoActual.getEquipos();
        if (equipos == null) {
            mostrarAlerta("Error", "No hay equipos cargados en el torneo.", Alert.AlertType.ERROR);
            return;
        }

        long equiposValidos = Arrays.stream(equipos)
                .filter(Objects::nonNull)
                .count();

        if (equiposValidos < 8) {
            mostrarAlerta("Error", "El torneo debe tener al menos 8 equipos cargados para armar los cruces.", Alert.AlertType.ERROR);
            return;
        }

        if (crucesArmados) {
            mostrarAlerta("Aviso", "Los cruces ya fueron armados.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            // --- Copiamos solo los equipos válidos ---
            List<Equipo> listaEquipos = Arrays.stream(equipos)
                    .filter(Objects::nonNull)
                    .limit(8) // tomar solo los primeros 8
                    .collect(Collectors.toList());

            Collections.shuffle(listaEquipos); // mezcla aleatoria

            // --- Resto del código igual ---
            Partido[] partidos = new Partido[7];

            // --- CUARTOS DE FINAL ---
            for (int i = 0; i < 4; i++) {
                Partido p = new Partido();
                p.setInstancia(i);
                p.setTorneo(torneoActual);
                p.setEquipo1(listaEquipos.get(i * 2));
                p.setEquipo2(listaEquipos.get(i * 2 + 1));
                p.setJugado(false);
                p.setPuntos(0);
                p.setHora(LocalTime.of(0, 0));

                partidoDAO.create(p);
                partidos[i] = p;
            }

            // --- SEMIS ---
            for (int i = 0; i < 2; i++) {
                Partido p = new Partido();
                p.setInstancia(4 + i);
                p.setTorneo(torneoActual);
                p.setEquipo1(null);
                p.setEquipo2(null);
                p.setJugado(false);
                p.setPuntos(0);
                p.setHora(LocalTime.of(0, 0));

                partidoDAO.create(p);
                partidos[4 + i] = p;
            }

            // --- FINAL ---
            Partido finalPartido = new Partido();
            finalPartido.setInstancia(6);
            finalPartido.setTorneo(torneoActual);
            finalPartido.setEquipo1(null);
            finalPartido.setEquipo2(null);
            finalPartido.setJugado(false);
            finalPartido.setPuntos(0);
            finalPartido.setHora(LocalTime.of(0, 0));

            partidoDAO.create(finalPartido);
            partidos[6] = finalPartido;

            torneoActual.setPartidos(partidos);
            torneoDAO.update(torneoActual);
            reconstruirTorneoDesdeBD();

            btnArmarCruces.setDisable(true);
            crucesArmados = true;

            mostrarAlerta("Éxito", "Cruces armados correctamente.", Alert.AlertType.INFORMATION);

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
    @FXML void handlepartidoC1(MouseEvent event) { abrirVentanaCargarPartido(0);  }
    @FXML void handlepartidoC2(MouseEvent event) { abrirVentanaCargarPartido(1); }
    @FXML void handlepartidoC3(MouseEvent event) { abrirVentanaCargarPartido(2); }
    @FXML void handlepartidoC4(MouseEvent event) { abrirVentanaCargarPartido(3); }
    @FXML void handlepartidoS1(MouseEvent event) { abrirVentanaCargarPartido(4); }
    @FXML void handlepartidoS2(MouseEvent event) {abrirVentanaCargarPartido(5);  }
    @FXML void handlepartidoF(MouseEvent event)  { abrirVentanaCargarPartido(6); }

    private void abrirVentanaCargarPartido(int indicePartido) {
        Partido[] partidos = torneoActual.getPartidos();
        System.out.println("Partidos cargados: " + Arrays.toString(partidos));

        //  Verificar si el torneo está en curso
        if (torneoActual.getEstados() != Es.En_Curso) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Torneo no disponible");
            alert.setHeaderText("El torneo no está en curso");
            alert.setContentText("Solo puedes cargar resultados cuando el torneo está en curso.");
            alert.showAndWait();
            return;
        }

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

    /**
     * Reemplaza/actualiza un partido en la lista observable para que la vista se refresque.
     * Publico para ser invocado desde CargarPartidoController via NavigationHelper.getController(...)
     */
    public void actualizarPartido(Partido partidoActualizado) {
        if (partidoActualizado == null) return;

        // 1) Actualizar BD/localmente el arreglo de torneoActual.partidos si corresponde
        // Intentamos ubicar el partido por instancia (si la instancia es confiable)
        try {
            Partido[] arr = torneoActual.getPartidos();
            if (arr != null) {
                int idx = partidoActualizado.getInstancia();
                if (idx >= 0 && idx < arr.length) {
                    arr[idx] = partidoActualizado;
                    torneoActual.setPartidos(arr);
                } else {
                    // Si no tenemos instancia confiable, intentamos por id
                    for (int i = 0; i < arr.length; i++) {
                        Partido p = arr[i];
                        if (p != null && p.getId() == partidoActualizado.getId()) {
                            arr[i] = partidoActualizado;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 2) Actualizar ObservableList para disparar el listener y refrescar la UI
        Platform.runLater(() -> {
            // si partido ya está en la lista, hacemos set para disparar evento; si no, lo añadimos
            for (int i = 0; i < partidosObservable.size(); i++) {
                Partido p = partidosObservable.get(i);
                if (p != null && p.getId() == partidoActualizado.getId()) {
                    partidosObservable.set(i, partidoActualizado);
                    return;
                }
            }
            partidosObservable.add(partidoActualizado);
        });
    }
}
