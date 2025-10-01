package utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class NavigationHelper {
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
//    public void setupBackButton(ImageView botonBack, String fxmlPath, String titulo) {
//        botonBack.setOnMousePressed(mouseEvent -> {
//            TranslateTransition press = new TranslateTransition(Duration.millis(50), botonBack);
//            press.setToX(10);
//            press.setToY(10);
//            press.play();
//        });
//
//        botonBack.setOnMouseReleased(mouseEvent -> {
//            Stage stage = (Stage) botonBack.getScene().getWindow();
//            TranslateTransition release = new TranslateTransition(Duration.millis(50), botonBack);
//            release.setToX(0);
//            release.setToY(0);
//            release.setOnFinished(event -> cambiarVista(stage,fxmlPath,titulo));
//            release.play();
//        });
    }

