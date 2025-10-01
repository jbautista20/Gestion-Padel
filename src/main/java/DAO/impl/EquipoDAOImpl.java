package DAO.impl;

import DAO.EquipoDAO;
import db.Conexion;
import models.Equipo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAOImpl implements EquipoDAO {

    private Connection conn;

    public EquipoDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Equipo equipo) {
        String sql = "INSERT INTO Equipos (id_jugador1, id_jugador2, id_torneo, nombre, ptos_T_Obt, fecha_Insc) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, equipo.getJugador1() != null ? equipo.getJugador1().getId() : 0);
            stmt.setInt(2, equipo.getJugador2() != null ? equipo.getJugador2().getId() : 0);
            stmt.setInt(3, equipo.getTorneo() != null ? equipo.getTorneo().getId() : 0);
            stmt.setString(4, equipo.getNombre());
            stmt.setInt(5, equipo.getPtos_T_Obt());
            stmt.setString(6, equipo.getFecha_Insc().toString()); // LocalDate → String

            stmt.executeUpdate();

            // obtener el id generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipo.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Equipo equipo) {
        String sql = "UPDATE Equipos SET id_jugador1 = ?, id_jugador2 = ?, id_torneo = ?, nombre = ?, ptos_T_Obt = ?, fecha_Insc = ? WHERE id_equipo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, equipo.getJugador1() != null ? equipo.getJugador1().getId() : 0);
            stmt.setInt(2, equipo.getJugador2() != null ? equipo.getJugador2().getId() : 0);
            stmt.setInt(3, equipo.getTorneo() != null ? equipo.getTorneo().getId() : 0);
            stmt.setString(4, equipo.getNombre());
            stmt.setInt(5, equipo.getPtos_T_Obt());
            stmt.setString(6, equipo.getFecha_Insc().toString());
            stmt.setInt(7, equipo.getId());

            stmt.executeUpdate();
            System.out.println("Equipo actualizado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Equipos WHERE id_equipo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Equipo eliminado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Equipo findById(int id) {
        String sql = "SELECT * FROM Equipos WHERE id_equipo = ?";
        Equipo equipo = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                equipo = new Equipo(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getInt("ptos_T_Obt"),
                        LocalDate.parse(rs.getString("fecha_Insc")),
                        null, // Jugador1 → cargar con JugadorDAO.findById(rs.getInt("id_jugador1"))
                        null, // Jugador2 → idem
                        null, // Torneo → cargar con TorneoDAO.findById(rs.getInt("id_torneo"))
                        null, // PartidosGanados → otro DAO
                        null  // PartidosJugados → otro DAO
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipo;
    }

    @Override
    public List<Equipo> findAll() {
        String sql = "SELECT * FROM Equipos";
        List<Equipo> equipos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Equipo equipo = new Equipo(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getInt("ptos_T_Obt"),
                        LocalDate.parse(rs.getString("fecha_Insc")),
                        null,
                        null,
                        null,
                        null,
                        null
                );
                equipos.add(equipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipos;
    }
}