package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static Conexion instancia;
    private Connection conexion; // no estática

    private String URL = "jdbc:sqlite:basededatos.db";

    // Constructor privado
    private Conexion() {
        try {
            conexion = DriverManager.getConnection(URL);
            System.out.println("Conexión establecida correctamente a SQLite.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a SQLite: " + e.getMessage());
        }
    }

    // Singleton thread-safe
    public static synchronized Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConnection() {
        return conexion;
    }

    // Metodo para cerrar la conexión
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                instancia = null;
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    //public getinstance: si la conexión es nulla llamo al const privado y la creo -> retrn conexión (este método es el unico que llama al constructor privado)
}