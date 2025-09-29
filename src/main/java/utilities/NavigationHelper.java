package utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationHelper {

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
}
