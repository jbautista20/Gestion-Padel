package DAO.impl;

import DAO.GenericDAO;
import db.Conexion;
import models.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAOImpl implements GenericDAO<Jugador> {

    private final Connection conn;

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
            stmt.setInt(4, jugador.getAnioNac()); // ahora es int, no LocalDate
            stmt.setInt(5, jugador.getId()); // id_persona (de la clase Persona)

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    jugador.setIdJugador(rs.getInt(1));
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
            stmt.setInt(4, jugador.getAnioNac()); // int en lugar de String/LocalDate
            stmt.setInt(5, jugador.getId()); // id_persona
            stmt.setInt(6, jugador.getIdJugador());

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
        String sql = """
            SELECT j.id_jugador, j.Categoria, j.Sexo, j.Puntos, j.Anio_Nac,
                   p.id_persona, p.nombre, p.apellido, p.telefono, p.direccion
            FROM Jugadores j
            JOIN Personas p ON j.id_persona = p.id_persona
            WHERE j.id_jugador = ?
        """;

        Jugador jugador = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                jugador = new Jugador(
                        rs.getInt("id_persona"),
                        rs.getInt("id_jugador"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        null, // lista de turnos
                        rs.getInt("Categoria"),
                        rs.getInt("Sexo"),
                        rs.getInt("Puntos"),
                        rs.getInt("Anio_Nac"), // ahora directamente int
                        null // lista de equipos
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jugador;
    }

    @Override
    public List<Jugador> findAll() {
        String sql = """
        SELECT j.id_jugador, j.Categoria, j.Sexo, j.Puntos, j.Anio_Nac,
               p.id_persona, p.nombre, p.apellido, p.telefono, p.direccion
        FROM Jugadores j
        JOIN Personas p ON j.id_persona = p.id_persona
        """;

        List<Jugador> jugadores = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Jugador jugador = new Jugador(
                        rs.getInt("id_persona"),
                        rs.getInt("id_jugador"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        null, // lista de turnos
                        rs.getInt("Categoria"),
                        rs.getInt("Sexo"),
                        rs.getInt("Puntos"),
                        rs.getInt("Anio_Nac"), // directamente int
                        null // lista de equipos
                );
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jugadores;
    }
}