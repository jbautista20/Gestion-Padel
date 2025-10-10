package utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.Torneo;

import java.net.URL;

public class NavigationHelper {
    private static Object datos;
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
            System.out.println("🔍 Buscando archivo FXML: " + fxml);

            // Método mejorado para encontrar el recurso
            URL resource = findResource(fxml);

            if (resource == null) {
                System.err.println("❌ NO SE ENCONTRÓ EL ARCHIVO: " + fxml);
                System.err.println("❌ Verifica que el archivo exista en: src/main/resources/Views/");
                return;
            }

            System.out.println("✅ Archivo encontrado: " + resource.getPath());

            FXMLLoader loader = new FXMLLoader(resource);
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

            System.out.println("✅ Vista cambiada exitosamente a: " + titulo);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error al cargar vista: " + fxml);
        }
    }

    /**
     * Método mejorado para encontrar recursos FXML
     */
    private static URL findResource(String fxmlPath) {
        // Eliminar slash inicial si existe
        String cleanPath = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;

        System.out.println("🔍 Buscando: " + cleanPath);

        // Intentar diferentes métodos de búsqueda
        URL resource = null;

        // Método 1: Usar ClassLoader del sistema
        resource = ClassLoader.getSystemResource(cleanPath);
        if (resource != null) {
            System.out.println("✅ Encontrado con ClassLoader");
            return resource;
        }

        // Método 2: Usar ClassLoader del thread actual
        resource = Thread.currentThread().getContextClassLoader().getResource(cleanPath);
        if (resource != null) {
            System.out.println("✅ Encontrado con ContextClassLoader");
            return resource;
        }

        // Método 3: Usar la clase NavigationHelper
        resource = NavigationHelper.class.getResource("/" + cleanPath);
        if (resource != null) {
            System.out.println("✅ Encontrado con NavigationHelper.class");
            return resource;
        }

        // Método 4: Buscar sin el prefijo "Views/"
        if (cleanPath.startsWith("Views/")) {
            String alternativePath = cleanPath.substring(6); // Remover "Views/"
            resource = ClassLoader.getSystemResource(alternativePath);
            if (resource != null) {
                System.out.println("✅ Encontrado sin prefijo Views/");
                return resource;
            }
        }

        return null;
    }

    public static void cambiarVistaConDatos(Stage stage, String fxml, String titulo, Object datos) {
        try {
            // Guardar datos temporalmente
            NavigationHelper.datos = datos;

            System.out.println("🔍 Buscando archivo FXML: " + fxml);
            URL resource = findResource(fxml);

            if (resource == null) {
                System.err.println("❌ NO SE ENCONTRÓ EL ARCHIVO: " + fxml);
                return;
            }

            System.out.println("✅ Archivo encontrado: " + resource.getPath());

            FXMLLoader loader = new FXMLLoader(resource);
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
            System.err.println("❌ Error al cargar vista: " + fxml);
        }
    }

    // ✅ Obtener datos
    public static Object getDatos() {
        return datos;
    }

    // ✅ Limpiar datos
    public static void clearDatos() {
        datos = null;
    }

    //--------------------------BotonBack----------------------------------------------------------------------------------
    /**
     * Configura un ImageView como botón "Back" con animación y navegación.
     * @param botonBack ImageView que actúa como botón
     * @param fxmlPath Ruta del FXML al que se quiere volver
     */
    public void setupBackButton(ImageView botonBack, String fxmlPath, String titulo) {
        botonBack.setOnMouseClicked(event -> {
            Stage stage = (Stage) botonBack.getScene().getWindow();
            navigateTo(stage, fxmlPath, titulo);
        });
    }

    // Método específico para navegación que evita ciclos
    private void navigateTo(Stage stage, String fxml, String titulo) {
        // Verificar si ya estamos en la vista destino para evitar ciclos
        if (stage.getTitle() != null && stage.getTitle().equals(titulo)) {
            System.out.println("Ya estamos en: " + titulo + " - Evitando navegación cíclica");
            return;
        }

        System.out.println("Navegando a: " + titulo);
        cambiarVista(stage, fxml, titulo);
    }
}

