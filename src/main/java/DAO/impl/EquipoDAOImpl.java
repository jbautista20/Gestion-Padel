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
        String sql = "INSERT INTO Equipos (id_jugador1, id_jugador2, id_torneo, nombre, ptos_T_Obt, fecha_Insc, motivo_desc, fecha_desc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Relaciones
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

            // Atributos propios
            stmt.setString(4, equipo.getNombre());
            stmt.setInt(5, equipo.getPtos_T_Obt());
            stmt.setString(6, equipo.getFecha_Insc().toString());

            // Campos nuevos (pueden ser nulos)
            stmt.setString(7, equipo.getMotivo_desc());
            if (equipo.getFecha_desc() != null)
                stmt.setString(8, equipo.getFecha_desc().toString());
            else
                stmt.setNull(8, Types.VARCHAR);

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
                        null, // Jugador1 (podés cargar luego con JugadorDAO.findById)
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

                // Si tenés un campo torneo en Equipo:
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


}
