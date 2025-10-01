package DAO.impl;

import DAO.JugadorDAO;
import db.Conexion;
import models.Jugador;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAOImpl implements JugadorDAO {

    private Connection conn;

    public JugadorDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Jugador jugador) {
        String sql = "INSERT INTO Jugadores (Categoria, Sexo, Puntos, Anio_Nac, id_persona) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, jugador.getCategoria());
            stmt.setInt(2, jugador.getSexo());
            stmt.setInt(3, jugador.getPuntos());
            stmt.setString(4, jugador.getAnio_Nac().toString()); // LocalDate → String
            stmt.setInt(5, jugador.getId()); // viene de Persona

            stmt.executeUpdate();

            // obtener el id generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    jugador.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Jugador jugador) {
        String sql = "UPDATE Jugadores SET Categoria = ?, Sexo = ?, Puntos = ?, Anio_Nac = ?, id_persona = ? WHERE id_jugador = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jugador.getCategoria());
            stmt.setInt(2, jugador.getSexo());
            stmt.setInt(3, jugador.getPuntos());
            stmt.setString(4, jugador.getAnio_Nac().toString());
            stmt.setInt(5, jugador.getId());
            stmt.setInt(6, jugador.getId());

            stmt.executeUpdate();
            System.out.println("Jugador actualizado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Jugadores WHERE id_jugador = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Jugador eliminado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Jugador findById(int id) {
        String sql = "SELECT * FROM Jugadores WHERE id_jugador = ?";
        Jugador jugador = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                jugador = new Jugador(
                        rs.getInt("id_persona"),
                        rs.getInt("id_jugador"),
                        null, null, null, null, null,  // datos de Persona → podrían traerse con un JOIN si querés
                        rs.getInt("id_jugador"),
                        rs.getInt("Categoria"),
                        rs.getInt("Sexo"),
                        rs.getInt("Puntos"),
                        LocalDate.parse(rs.getString("Anio_Nac")),
                        null // lista de equipos aparte
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jugador;
    }

    @Override
    public List<Jugador> findAll() {
        String sql = "SELECT * FROM Jugadores";
        List<Jugador> jugadores = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Jugador jugador = new Jugador(
                        rs.getInt("id_persona"),
                        rs.getInt("id_jugador"),
                        null, null, null, null, null,
                        rs.getInt("id_jugador"),
                        rs.getInt("Categoria"),
                        rs.getInt("Sexo"),
                        rs.getInt("Puntos"),
                        LocalDate.parse(rs.getString("Anio_Nac")),
                        null
                );
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jugadores;
    }
}