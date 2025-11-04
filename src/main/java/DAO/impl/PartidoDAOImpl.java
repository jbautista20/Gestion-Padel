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

    public List<Partido> findByTorneo(int idTorneo) {
        String sql = "SELECT * FROM Partidos WHERE id_torneo = ? ORDER BY instancia ASC";
        List<Partido> partidos = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Partido p = new Partido();
                p.setId(rs.getInt("id_partido"));
                p.setHora(rs.getString("hora") != null ? LocalTime.parse(rs.getString("hora")) : null);
                p.setInstancia(rs.getInt("instancia"));
                p.setPuntos(rs.getInt("puntos"));
                p.setSet1(rs.getString("set1"));
                p.setSet2(rs.getString("set2"));
                p.setSet3(rs.getString("set3"));
                p.setJugado(rs.getInt("jugado") == 1);

                // --- Relacionar objetos ---
                p.setTorneo(new Torneo());
                p.getTorneo().setId(idTorneo);

                // Equipos
                if (rs.getInt("id_equipo1") != 0) {
                    Equipo eq1 = new Equipo();
                    eq1.setId(rs.getInt("id_equipo1"));
                    p.setEquipo1(eq1);
                }

                if (rs.getInt("id_equipo2") != 0) {
                    Equipo eq2 = new Equipo();
                    eq2.setId(rs.getInt("id_equipo2"));
                    p.setEquipo2(eq2);
                }

                if (rs.getInt("id_ganador") != 0) {
                    Equipo ganador = new Equipo();
                    ganador.setId(rs.getInt("id_ganador"));
                    p.setGanador(ganador);
                }

                // Cancha (opcional)
                if (rs.getInt("num_cancha") != 0) {
                    Cancha cancha = new Cancha();
                    cancha.setNumero(rs.getInt("num_cancha"));
                    p.setCancha(cancha);
                }

                partidos.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partidos;
    }


}
