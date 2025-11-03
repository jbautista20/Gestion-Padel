package DAO.impl;

import DAO.GenericDAO;
import db.Conexion;
import models.Equipo;
import models.Torneo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class EquipoDAOImpl implements GenericDAO<Equipo> {

    private Connection conn;

    public EquipoDAOImpl() {
        this.conn = Conexion.getInstance().getConnection();
    }

    @Override
    public void create(Equipo equipo) {
        // validaciones previas
        Integer idJ1 = equipo.getJugador1() != null ? equipo.getJugador1().getIdJugador() : null;
        Integer idJ2 = equipo.getJugador2() != null ? equipo.getJugador2().getIdJugador() : null;
        Integer idT = equipo.getTorneo() != null ? equipo.getTorneo().getId() : null;

        if (idT == null) {
            System.err.println("No se puede crear equipo: torneo nulo");
            return;
        }
        if (idJ1 == null || idJ2 == null) {
            System.err.println("No se puede crear equipo: faltan jugadores.");
            return;
        }

        // Si alguno ya está en el torneo, abortar
        if (estaJugadorEnTorneo(idJ1, idT) || estaJugadorEnTorneo(idJ2, idT)) {
            System.out.println("Uno de los jugadores ya está inscrito en un equipo de este torneo. No se crea equipo.");
            return;
        }

        // Si ya existe el equipo con estos dos jugadores, abortar
        if (existeEquipoConJugadores(idJ1, idJ2, idT)) {
            System.out.println("Ya existe un equipo con esos jugadores en este torneo. No se crea duplicado.");
            return;
        }

        // normalizar orden para insert (opcional pero útil)
        int a = Math.min(idJ1, idJ2);
        int b = Math.max(idJ1, idJ2);

        String sql = "INSERT INTO Equipos (id_jugador1, id_jugador2, id_torneo, nombre, ptos_T_Obt, fecha_Insc, motivo_desc, fecha_desc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a);
            stmt.setInt(2, b);
            stmt.setInt(3, idT);
            stmt.setString(4, equipo.getNombre());
            stmt.setInt(5, equipo.getPtos_T_Obt());
            stmt.setString(6, equipo.getFecha_Insc() != null ? equipo.getFecha_Insc().toString() : null);
            stmt.setString(7, equipo.getMotivo_desc());
            if (equipo.getFecha_desc() != null)
                stmt.setString(8, equipo.getFecha_desc().toString());
            else
                stmt.setNull(8, Types.VARCHAR);

            // log antes de ejecutar
            System.out.println("Insertando equipo: j1=" + a + " j2=" + b + " torneo=" + idT + " nombre=" + equipo.getNombre());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.err.println("No se insertó el equipo.");
                return;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipo.setId(rs.getInt(1));
                }
            }

            System.out.println("Equipo insertado con id = " + equipo.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Equipo equipo) {
        String sql = "UPDATE Equipos SET id_jugador1 = ?, id_jugador2 = ?, id_torneo = ?, nombre = ?, ptos_T_Obt = ?, fecha_Insc = ?, motivo_desc = ?, fecha_desc = ? WHERE id_equipo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (equipo.getJugador1() != null)
                stmt.setInt(1, equipo.getJugador1().getId());
            else
                stmt.setNull(1, Types.INTEGER);

            if (equipo.getJugador2() != null)
                stmt.setInt(2, equipo.getJugador2().getId());
            else
                stmt.setNull(2, Types.INTEGER);

            if (equipo.getTorneo() != null)
                stmt.setInt(3, equipo.getTorneo().getId());
            else
                stmt.setNull(3, Types.INTEGER);

            stmt.setString(4, equipo.getNombre());
            stmt.setInt(5, equipo.getPtos_T_Obt());
            stmt.setString(6, equipo.getFecha_Insc().toString());
            stmt.setString(7, equipo.getMotivo_desc());
            if (equipo.getFecha_desc() != null)
                stmt.setString(8, equipo.getFecha_desc().toString());
            else
                stmt.setNull(8, Types.VARCHAR);
            stmt.setInt(9, equipo.getId());

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
                        null, // Jugador1
                        null, // Jugador2
                        null, // Torneo
                        null, // PartidosGanados
                        null, // PartidosJugados
                        rs.getString("motivo_desc"),
                        rs.getString("fecha_desc") != null ? LocalDate.parse(rs.getString("fecha_desc")) : null
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
                        null,
                        rs.getString("motivo_desc"),
                        rs.getString("fecha_desc") != null ? LocalDate.parse(rs.getString("fecha_desc")) : null
                );
                equipos.add(equipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipos;
    }

    public int contarEquiposPorTorneo(int idTorneo) {
        String sql = "SELECT COUNT(*) FROM Equipos WHERE id_torneo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Equipo> findByTorneoId(int idTorneo) {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT * FROM Equipos WHERE id_torneo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("id_equipo"));
                equipo.setNombre(rs.getString("nombre"));
                equipo.setPtos_T_Obt(rs.getInt("ptos_T_Obt"));

                String fechaInscStr = rs.getString("fecha_Insc");
                if (fechaInscStr != null && !fechaInscStr.isEmpty()) {
                    equipo.setFecha_Insc(LocalDate.parse(fechaInscStr));
                } else {
                    equipo.setFecha_Insc(null);
                }

                // Cargar motivo y fecha de descalificación
                equipo.setMotivo_desc(rs.getString("motivo_desc"));
                String fechaDescStr = rs.getString("fecha_desc");
                if (fechaDescStr != null && !fechaDescStr.isEmpty()) {
                    equipo.setFecha_desc(LocalDate.parse(fechaDescStr));
                } else {
                    equipo.setFecha_desc(null);
                }


                // Asignar el torneo
                Torneo torneo = new Torneo();
                torneo.setId(rs.getInt("id_torneo"));
                equipo.setTorneo(torneo);

                equipos.add(equipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equipos;
    }
    public boolean estaJugadorEnTorneo(int idJugador, int idTorneo) {
        String sql = "SELECT 1 FROM Equipos WHERE id_torneo = ? AND (id_jugador1 = ? OR id_jugador2 = ?) LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            stmt.setInt(2, idJugador);
            stmt.setInt(3, idJugador);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Devuelve true si ya existe un equipo con exactamente esos dos jugadores en ese torneo.
     * Normalizamos orden: j1 < j2.
     */
    public boolean existeEquipoConJugadores(int idJ1, int idJ2, int idTorneo) {
        // normalizar orden
        int a = Math.min(idJ1, idJ2);
        int b = Math.max(idJ1, idJ2);

        String sql = "SELECT 1 FROM Equipos WHERE id_torneo = ? AND ((id_jugador1 = ? AND id_jugador2 = ?) OR (id_jugador1 = ? AND id_jugador2 = ?)) LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            stmt.setInt(2, a);
            stmt.setInt(3, b);
            stmt.setInt(4, a);
            stmt.setInt(5, b);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
