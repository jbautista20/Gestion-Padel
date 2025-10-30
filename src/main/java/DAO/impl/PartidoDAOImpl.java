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
        this.conn = Conexion.getInstance().getConnection();
    }

    @Override
    public void create(Partido partido) {
        String sql = "INSERT INTO Partidos (hora, instancia, puntos, set1, num_cancha, id_equipo1, id_equipo2, id_ganador, id_torneo, set2, set3) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Campos simples
            stmt.setString(1, partido.getHora() != null ? partido.getHora().toString() : "00:00");
            stmt.setInt(2, partido.getInstancia());
            stmt.setInt(3, partido.getPuntos());
            stmt.setString(4, partido.getSet1());

            // Cancha (puede ser nulo)
            if (partido.getCancha() != null) {
                stmt.setInt(5, partido.getCancha().getNumero());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            // Equipo 1
            if (partido.getEquipo1() != null && partido.getEquipo1().getId() != 0) {
                stmt.setInt(6, partido.getEquipo1().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            // Equipo 2
            if (partido.getEquipo2() != null && partido.getEquipo2().getId() != 0) {
                stmt.setInt(7, partido.getEquipo2().getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            // Ganador
            if (partido.getGanador() != null && partido.getGanador().getId() != 0) {
                stmt.setInt(8, partido.getGanador().getId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            // Torneo
            if (partido.getTorneo() != null && partido.getTorneo().getId() != 0) {
                stmt.setInt(9, partido.getTorneo().getId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.setString(10, partido.getSet2());
            stmt.setString(11, partido.getSet3());

            stmt.executeUpdate();

            // Obtener último ID insertado
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

    public List<Partido> obtenerPartidosPorTorneo(int idTorneo) {
        List<Partido> partidos = new ArrayList<>();

        String sql = "SELECT * FROM Partidos WHERE id_torneo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTorneo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Partido p = new Partido();
                p.setId(rs.getInt("id_partido"));
                p.setInstancia(rs.getInt("instancia"));

                // Si tenés relaciones con Torneo y Equipos:
                Torneo torneo = new Torneo();
                torneo.setId(rs.getInt("id_torneo"));
                p.setTorneo(torneo);

                Equipo equipo1 = new Equipo();
                equipo1.setId(rs.getInt("id_equipo1"));
                p.setEquipo1(equipo1);

                Equipo equipo2 = new Equipo();
                equipo2.setId(rs.getInt("id_equipo2"));
                p.setEquipo2(equipo2);

                partidos.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partidos;
    }

    public List<Partido> obtenerCrucesPorTorneo(int idTorneo) {
        List<Partido> partidos = new ArrayList<>();

        String sql = """
        SELECT 
            p.id_partido,
            p.instancia,
            e1.nombre AS nombre_equipo1,
            e2.nombre AS nombre_equipo2
        FROM Partidos p
        LEFT JOIN Equipos e1 ON p.id_equipo1 = e1.id_equipo
        LEFT JOIN Equipos e2 ON p.id_equipo2 = e2.id_equipo
        WHERE p.id_torneo = ?
        ORDER BY p.id_partido
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTorneo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Partido partido = new Partido();
                partido.setId(rs.getInt("id_partido"));
                partido.setInstancia(rs.getInt("instancia"));

                // Creamos los equipos con sus nombres
                Equipo equipo1 = new Equipo();
                equipo1.setNombre(rs.getString("nombre_equipo1"));

                Equipo equipo2 = new Equipo();
                equipo2.setNombre(rs.getString("nombre_equipo2"));

                partido.setEquipo1(equipo1);
                partido.setEquipo2(equipo2);

                partidos.add(partido);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partidos;
    }



}