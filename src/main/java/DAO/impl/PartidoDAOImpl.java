package DAO.impl;

import DAO.GenericDAO;
import db.Conexion;
import models.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAOImpl implements GenericDAO<Partido> {

    private Connection conn;

    public PartidoDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Partido partido) {
        String sql = "INSERT INTO Partidos (hora, instancia, puntos, set1, num_cancha, id_equipo1, id_equipo2, id_ganador, id_torneo, set2, set3) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, partido.getHora().toString()); // LocalTime → String
            stmt.setInt(2, partido.getInstancia());
            stmt.setInt(3, partido.getPuntos());
            stmt.setString(4, partido.getSet1());
            stmt.setInt(5, partido.getCancha() != null ? partido.getCancha().getNumero() : null);
            stmt.setInt(6, partido.getEquipo1() != null ? partido.getEquipo1().getId() : null);
            stmt.setInt(7, partido.getEquipo2() != null ? partido.getEquipo2().getId() : null);
            stmt.setInt(8, partido.getGanador() != null ? partido.getGanador().getId() : null);
            stmt.setInt(9, partido.getTorneo() != null ? partido.getTorneo().getId() : null);
            stmt.setString(10, partido.getSet2());
            stmt.setString(11, partido.getSet3());

            stmt.executeUpdate();

            // obtener último ID insertado
            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs = stmt2.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    partido.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Partido partido) {
        String sql = "UPDATE Partidos SET hora = ?, instancia = ?, puntos = ?, set1 = ?, num_cancha = ?, id_equipo1 = ?, id_equipo2 = ?, id_ganador = ?, id_torneo = ?, set2 = ?, set3 = ? WHERE id_partido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, partido.getHora().toString());
            stmt.setInt(2, partido.getInstancia());
            stmt.setInt(3, partido.getPuntos());
            stmt.setString(4, partido.getSet1());
            stmt.setInt(5, partido.getCancha() != null ? partido.getCancha().getNumero() : null);
            stmt.setInt(6, partido.getEquipo1() != null ? partido.getEquipo1().getId() : null);
            stmt.setInt(7, partido.getEquipo2() != null ? partido.getEquipo2().getId() : null);
            stmt.setInt(8, partido.getGanador() != null ? partido.getGanador().getId() : null);
            stmt.setInt(9, partido.getTorneo() != null ? partido.getTorneo().getId() : null);
            stmt.setString(10, partido.getSet2());
            stmt.setString(11, partido.getSet3());
            stmt.setInt(12, partido.getId());

            stmt.executeUpdate();
            System.out.println("Partido actualizado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Partidos WHERE id_partido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Partido eliminado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Partido findById(int id) {
        String sql = "SELECT * FROM Partidos WHERE id_partido = ?";
        Partido partido = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                partido = new Partido(
                        rs.getInt("id_partido"),
                        LocalTime.parse(rs.getString("hora")),
                        rs.getInt("instancia"),
                        rs.getInt("puntos"),
                        null, // cancha
                        null, // equipo1
                        null, // equipo2
                        null, // ganador
                        null, // torneo
                        rs.getString("set1"),
                        rs.getString("set2"),
                        rs.getString("set3")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partido;
    }


    @Override
    public List<Partido> findAll() {
        String sql = "SELECT * FROM Partidos";
        List<Partido> partidos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Partido partido = new Partido(
                        rs.getInt("id_partido"),
                        LocalTime.parse(rs.getString("hora")),
                        rs.getInt("instancia"),
                        rs.getInt("puntos"),
                        null, // cancha
                        null, // equipo1
                        null, // equipo2
                        null, // ganador
                        null, // torneo
                        rs.getString("set1"),
                        rs.getString("set2"),
                        rs.getString("set3")
                );
                partidos.add(partido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partidos;
    }


}