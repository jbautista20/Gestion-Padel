package DAO.impl;

import DAO.GenericDAO;
import db.Conexion;
import models.Jugador;
import models.T;
import models.Torneo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAOImpl implements GenericDAO<Jugador> {

    private final Connection conn;

    public JugadorDAOImpl() {
        this.conn = Conexion.getInstance().getConnection();
    }

    @Override
    public void create(Jugador jugador) {
        String sql = "INSERT INTO Jugadores (Categoria, Sexo, Puntos, Anio_Nac, id_persona) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, jugador.getCategoria());
            stmt.setInt(2, jugador.getSexo());
            stmt.setInt(3, jugador.getPuntos());
            stmt.setInt(4, jugador.getAnioNac());
            stmt.setInt(5, jugador.getId()); // id_persona de la superclase Persona

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
            stmt.setInt(4, jugador.getAnioNac());
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
                        null,
                        rs.getInt("Categoria"),
                        rs.getInt("Sexo"),
                        rs.getInt("Puntos"),
                        rs.getInt("Anio_Nac"),
                        null
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
                        null,
                        rs.getInt("Categoria"),
                        rs.getInt("Sexo"),
                        rs.getInt("Puntos"),
                        rs.getInt("Anio_Nac"),
                        null
                );
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jugadores;
    }

    /**
     * Devuelve los jugadores disponibles para inscribirse en un torneo,
     * según su categoría, tipo de torneo (Damas, Caballeros, Mixto),
     * y evitando los que ya están en algún equipo de ese torneo.
     */
    public List<Jugador> findDisponiblesPorTorneo(Torneo torneo) {
        // Base con NOT EXISTS (más segura que NOT IN)
        String sqlBase = """
        SELECT j.id_jugador, j.Categoria, j.Sexo, j.Puntos, j.Anio_Nac,
               p.id_persona, p.nombre, p.apellido, p.telefono, p.direccion
        FROM Jugadores j
        JOIN Personas p ON j.id_persona = p.id_persona
        WHERE j.Categoria = ?
          AND NOT EXISTS (
              SELECT 1 FROM Equipos e
              WHERE e.id_torneo = ?
                AND (e.id_jugador1 = j.id_jugador OR e.id_jugador2 = j.id_jugador)
          )
        """;

        // Filtro adicional según el tipo de torneo
        String filtroSexo = "";
        if (torneo.getTipo() == T.Damas) {
            filtroSexo = " AND j.Sexo = 2"; // 2 = femenino
        } else if (torneo.getTipo() == T.Caballeros) {
            filtroSexo = " AND j.Sexo = 1"; // 1 = masculino
        }
        String sql = sqlBase + filtroSexo;

        List<Jugador> jugadores = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, torneo.getCategoria());
            stmt.setInt(2, torneo.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Jugador jugador = new Jugador(
                        rs.getInt("id_persona"),
                        rs.getInt("id_jugador"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        null,
                        rs.getInt("Categoria"),
                        rs.getInt("Sexo"),
                        rs.getInt("Puntos"),
                        rs.getInt("Anio_Nac"),
                        null
                );
                jugadores.add(jugador);
            }

            // Debug: ver cuántos jugadores quedan disponibles
            System.out.println("Jugadores disponibles para torneo " + torneo.getId() + ": " + jugadores.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jugadores;
    }
}
