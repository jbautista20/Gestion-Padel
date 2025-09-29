package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Atributo estático que guarda la única instancia de la conexión
    private static Connection conexion;

    // Constructor privado para evitar instanciación directa
    private Conexion() { }

    // Metodo que devuelve la conexión
    public static Connection getConexion() {
        if (conexion == null) {
            try {
                String url = "jdbc:sqlite:basededatos.db"; // tu archivo de base de datos
                conexion = DriverManager.getConnection(url);
                System.out.println("Conexión establecida correctamente a SQLite.");
            } catch (SQLException e) {
                System.out.println("Error al conectar a SQLite: " + e.getMessage());
            }
        }
        return conexion;
    }
}