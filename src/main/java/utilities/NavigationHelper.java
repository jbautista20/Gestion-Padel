package utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class NavigationHelper {
    private  static  Object datos;
    private static NavigationHelper instance;
    public static NavigationHelper getInstance() {
        if (instance == null) {
            instance = new NavigationHelper();
        }
        return instance;
    }

    /**
     * Cambia la vista dentro de la misma ventana (Stage).
     *
     * @param stage   la ventana principal
     * @param fxml    ruta del archivo FXML (ejemplo: "/Views/menuPrincipal.fxml")
     * @param titulo  título opcional para la ventana
     */

    public static void cambiarVista(Stage stage, String fxml, String titulo) {
        try {

            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxml));
            Parent root = loader.load();

            Scene scene = stage.getScene();

            if (scene == null) {
                // Si no había escena (ej: al inicio), la creamos
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                // Si ya existe, solo reemplazamos el root
                scene.setRoot(root);
            }

            if (titulo != null && !titulo.isEmpty()) {
                stage.setTitle(titulo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar vista: " + fxml);
        }
    }


    public static void cambiarVistaConDatos(Stage stage, String fxml, String titulo, Object datos) {
        try {
            // Guardar datos temporalmente
            NavigationHelper.datos = datos;

            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxml));
            Parent root = loader.load();

            Scene scene = stage.getScene();

            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            if (titulo != null && !titulo.isEmpty()) {
                stage.setTitle(titulo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar vista: " + fxml);
        }
    }

    // Obtener datos
    public static Object getDatos() {
        return datos;
    }

    // Limpiar datos
    public static void clearDatos() {
        datos = null;
    }

    public static void abrirPopupConDatos(String fxml, String titulo, Object datos, double ancho, double alto) {
        try {
            NavigationHelper.datos = datos;

            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxml));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle(titulo);
            popupStage.setScene(new Scene(root, ancho, alto));

            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(getVentanaPrincipal());
            popupStage.centerOnScreen();
            popupStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al abrir popup: " + fxml);
        }
    }

    // Método auxiliar para obtener la ventana principal actual
    private static Stage getVentanaPrincipal() {
        for (Stage stage : Stage.getWindows().stream()
                .filter(w -> w instanceof Stage)
                .map(w -> (Stage) w)
                .toList()) {
            if (stage.isFocused()) return stage;
        }
        return null;
    }

    private static final Map<Class<?>, Object> controladores = new HashMap<>();

    /** Registra un controlador para que otros puedan accederlo más tarde. */
    public static void registrarControlador(Object controller) {
        if (controller != null)
            controladores.put(controller.getClass(), controller);
    }

    /** Devuelve una instancia registrada del controlador pedido (o null si no está). */
    @SuppressWarnings("unchecked")
    public static <T> T getController(Class<T> tipo) {
        Object controlador = controladores.get(tipo);
        if (tipo.isInstance(controlador)) {
            return (T) controlador;
        }
        return null;
    }


}
