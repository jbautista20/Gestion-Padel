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
        String sql = "INSERT INTO Partidos (hora, instancia, puntos, set1, num_cancha, id_equipo1, id_equipo2, id_ganador, id_torneo, set2, set3, jugado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, partido.getHora() != null ? partido.getHora().toString() : "00:00");
            stmt.setInt(2, partido.getInstancia());
            stmt.setInt(3, partido.getPuntos());
            stmt.setString(4, partido.getSet1());

            // Cancha
            if (partido.getCancha() != null) {
                stmt.setInt(5, partido.getCancha().getNumero());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            // Equipos
            if (partido.getEquipo1() != null && partido.getEquipo1().getId() != 0)
                stmt.setInt(6, partido.getEquipo1().getId());
            else
                stmt.setNull(6, Types.INTEGER);

            if (partido.getEquipo2() != null && partido.getEquipo2().getId() != 0)
                stmt.setInt(7, partido.getEquipo2().getId());
            else
                stmt.setNull(7, Types.INTEGER);

            // Ganador
            if (partido.getGanador() != null && partido.getGanador().getId() != 0)
                stmt.setInt(8, partido.getGanador().getId());
            else
                stmt.setNull(8, Types.INTEGER);

            // Torneo
            if (partido.getTorneo() != null && partido.getTorneo().getId() != 0)
                stmt.setInt(9, partido.getTorneo().getId());
            else
                stmt.setNull(9, Types.INTEGER);

            stmt.setString(10, partido.getSet2());
            stmt.setString(11, partido.getSet3());

            // Jugado (boolean -> entero)
            stmt.setInt(12, partido.isJugado() ? 1 : 0);

            stmt.executeUpdate();

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
        String sql = "UPDATE Partidos SET hora = ?, instancia = ?, puntos = ?, set1 = ?, num_cancha = ?, " +
                "id_equipo1 = ?, id_equipo2 = ?, id_ganador = ?, id_torneo = ?, set2 = ?, set3 = ?, jugado = ? " +
                "WHERE id_partido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, partido.getHora() != null ? partido.getHora().toString() : "00:00");
            stmt.setInt(2, partido.getInstancia());
            stmt.setInt(3, partido.getPuntos());
            stmt.setString(4, partido.getSet1());

            if (partido.getCancha() != null)
                stmt.setInt(5, partido.getCancha().getNumero());
            else
                stmt.setNull(5, Types.INTEGER);

            if (partido.getEquipo1() != null)
                stmt.setInt(6, partido.getEquipo1().getId());
            else
                stmt.setNull(6, Types.INTEGER);

            if (partido.getEquipo2() != null)
                stmt.setInt(7, partido.getEquipo2().getId());
            else
                stmt.setNull(7, Types.INTEGER);

            if (partido.getGanador() != null)
                stmt.setInt(8, partido.getGanador().getId());
            else
                stmt.setNull(8, Types.INTEGER);

            if (partido.getTorneo() != null)
                stmt.setInt(9, partido.getTorneo().getId());
            else
                stmt.setNull(9, Types.INTEGER);

            stmt.setString(10, partido.getSet2());
            stmt.setString(11, partido.getSet3());
            stmt.setInt(12, partido.isJugado() ? 1 : 0);
            stmt.setInt(13, partido.getId());

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
                        null, null, null, null, null,
                        rs.getString("set1"),
                        rs.getString("set2"),
                        rs.getString("set3")
                );
                partido.setJugado(rs.getInt("jugado") == 1);
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
                        null, null, null, null, null,
                        rs.getString("set1"),
                        rs.getString("set2"),
                        rs.getString("set3")
                );
                partido.setJugado(rs.getInt("jugado") == 1);
                partidos.add(partido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partidos;
    }

    public List<Partido> obtenerPartidosPorTorneo(int idTorneo) {
        List<Partido> partidos = new ArrayList<>();

        String sql = """
        SELECT 
            p.id_partido,
            p.hora,
            p.instancia,
            p.puntos,
            p.set1,
            p.set2,
            p.set3,
            p.jugado,
            p.id_torneo,
            e1.id_equipo AS id_equipo1, e1.nombre AS nombre_equipo1,
            e2.id_equipo AS id_equipo2, e2.nombre AS nombre_equipo2,
            e3.id_equipo AS id_ganador, e3.nombre AS nombre_ganador
        FROM Partidos p
        LEFT JOIN Equipos e1 ON p.id_equipo1 = e1.id_equipo
        LEFT JOIN Equipos e2 ON p.id_equipo2 = e2.id_equipo
        LEFT JOIN Equipos e3 ON p.id_ganador = e3.id_equipo
        WHERE p.id_torneo = ?
        ORDER BY p.instancia, p.id_partido
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Partido partido = new Partido();
                partido.setId(rs.getInt("id_partido"));
                partido.setInstancia(rs.getInt("instancia"));
                partido.setJugado(rs.getInt("jugado") == 1);
                partido.setPuntos(rs.getInt("puntos"));
                partido.setSet1(rs.getString("set1"));
                partido.setSet2(rs.getString("set2"));
                partido.setSet3(rs.getString("set3"));

                // Torneo
                Torneo torneo = new Torneo();
                torneo.setId(rs.getInt("id_torneo"));
                partido.setTorneo(torneo);

                // Equipos
                Equipo equipo1 = new Equipo();
                equipo1.setId(rs.getInt("id_equipo1"));
                equipo1.setNombre(rs.getString("nombre_equipo1"));
                partido.setEquipo1(equipo1);

                Equipo equipo2 = new Equipo();
                equipo2.setId(rs.getInt("id_equipo2"));
                equipo2.setNombre(rs.getString("nombre_equipo2"));
                partido.setEquipo2(equipo2);

                Equipo ganador = new Equipo();
                ganador.setId(rs.getInt("id_ganador"));
                ganador.setNombre(rs.getString("nombre_ganador"));
                partido.setGanador(ganador);

                partidos.add(partido);
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
            p.jugado,
            p.id_equipo1,
            p.id_equipo2,
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
                partido.setJugado(rs.getInt("jugado") == 1);

                Equipo equipo1 = null;
                Equipo equipo2 = null;

                if (rs.getInt("id_equipo1") != 0) {
                    equipo1 = new Equipo();
                    equipo1.setId(rs.getInt("id_equipo1"));
                    equipo1.setNombre(rs.getString("nombre_equipo1"));
                }
                if (rs.getInt("id_equipo2") != 0) {
                    equipo2 = new Equipo();
                    equipo2.setId(rs.getInt("id_equipo2"));
                    equipo2.setNombre(rs.getString("nombre_equipo2"));
                }

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
