package DAO.impl;

import DAO.GenericDAO;
import db.Conexion;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
public class TurnoDAOImpl implements GenericDAO<Turno> {

    private Connection conn;

    public TurnoDAOImpl() {
        this.conn = Conexion.getInstance().getConnection();
    }

    @Override
    public void create(Turno turno) {
        String sql = "INSERT INTO Turnos (fecha, id_persona, hora, estado, pago, fecha_pago, num_cancha, fecha_cancelacion, reintegro_cancelacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, turno.getFecha().toString());

            // Persona (puede ser null)
            if (turno.getPersona() != null)
                stmt.setInt(2, turno.getPersona().getId());
            else
                stmt.setNull(2, Types.INTEGER);

            stmt.setString(3, turno.getHora().toString());
            stmt.setString(4, turno.getEstado().name());
            stmt.setInt(5, turno.getPago());

            if (turno.getFecha_Pago() != null)
                stmt.setString(6, turno.getFecha_Pago().toString());
            else
                stmt.setNull(6, Types.VARCHAR);

            if (turno.getCancha() != null)
                stmt.setInt(7, turno.getCancha().getNumero());
            else
                stmt.setNull(7, Types.INTEGER);

            // Nuevos campos: cancelación
            if (turno.getFecha_Cancelacion() != null)
                stmt.setString(8, turno.getFecha_Cancelacion().toString());
            else
                stmt.setNull(8, Types.VARCHAR);

            stmt.setString(9, turno.getReintegro_Cancelacion());

            stmt.executeUpdate();

            // Obtener ID generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    turno.setId(rs.getInt(1));
                }
            }

            System.out.println("Turno creado correctamente: " + turno.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Turno turno) {
        String sql = "UPDATE Turnos SET fecha=?, id_persona=?, hora=?, estado=?, pago=?, fecha_pago=?, num_cancha=?, fecha_cancelacion=?, reintegro_cancelacion=? WHERE id_turno=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, turno.getFecha().toString());

            if (turno.getPersona() != null)
                stmt.setInt(2, turno.getPersona().getId());
            else
                stmt.setNull(2, Types.INTEGER);

            stmt.setString(3, turno.getHora().toString());
            stmt.setString(4, turno.getEstado().name());
            stmt.setInt(5, turno.getPago());

            if (turno.getFecha_Pago() != null)
                stmt.setString(6, turno.getFecha_Pago().toString());
            else
                stmt.setNull(6, Types.VARCHAR);

            if (turno.getCancha() != null)
                stmt.setInt(7, turno.getCancha().getNumero());
            else
                stmt.setNull(7, Types.INTEGER);

            if (turno.getFecha_Cancelacion() != null)
                stmt.setString(8, turno.getFecha_Cancelacion().toString());
            else
                stmt.setNull(8, Types.VARCHAR);

            stmt.setString(9, turno.getReintegro_Cancelacion());
            stmt.setInt(10, turno.getId());

            stmt.executeUpdate();
            System.out.println("Turno actualizado: " + turno.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Turnos WHERE id_turno=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Turno eliminado: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Turno findById(int id) {
        String sql = "SELECT * FROM Turnos WHERE id_turno=?";
        Turno turno = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    turno = new Turno(
                            rs.getInt("id_turno"),
                            LocalDate.parse(rs.getString("fecha")),
                            LocalTime.parse(rs.getString("hora")),
                            E.valueOf(rs.getString("estado")),
                            rs.getInt("pago"),
                            rs.getString("fecha_pago") != null ? LocalDate.parse(rs.getString("fecha_pago")) : null,
                            null, // Persona se carga abajo
                            null, // Cancha se carga abajo
                            rs.getString("fecha_cancelacion") != null ? LocalDate.parse(rs.getString("fecha_cancelacion")) : null,
                            rs.getString("reintegro_cancelacion")
                    );

                    // Relaciones
                    Persona persona = new Persona();
                    persona.setId(rs.getInt("id_persona"));
                    turno.setPersona(persona);

                    Cancha cancha = new Cancha();
                    cancha.setNumero(rs.getInt("num_cancha"));
                    turno.setCancha(cancha);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return turno;
    }

    @Override
    public List<Turno> findAll() {
        String sql = "SELECT * FROM Turnos";
        List<Turno> turnos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Turno turno = new Turno(
                        rs.getInt("id_turno"),
                        LocalDate.parse(rs.getString("fecha")),
                        LocalTime.parse(rs.getString("hora")),
                        E.valueOf(rs.getString("estado")),
                        rs.getInt("pago"),
                        rs.getString("fecha_pago") != null ? LocalDate.parse(rs.getString("fecha_pago")) : null,
                        null, // Persona (se asigna abajo)
                        null, // Cancha (se asigna abajo)
                        rs.getString("fecha_cancelacion") != null ? LocalDate.parse(rs.getString("fecha_cancelacion")) : null,
                        rs.getString("reintegro_cancelacion")
                );

                Persona persona = new Persona();
                persona.setId(rs.getInt("id_persona"));
                turno.setPersona(persona);

                Cancha cancha = new Cancha();
                cancha.setNumero(rs.getInt("num_cancha"));
                turno.setCancha(cancha);

                turnos.add(turno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return turnos;
    }

    public boolean existeTurno(int numeroCancha, LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(*) FROM Turnos WHERE num_cancha = ? AND fecha = ? AND hora = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroCancha);
            stmt.setString(2, fecha.toString());
            stmt.setString(3, hora.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void ocuparTurnosPorFecha(LocalDate fecha) {
        String sql = "UPDATE Turnos SET estado = ? WHERE fecha = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, E.Ocupado.name());
            stmt.setString(2, fecha.toString());
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Turnos ocupados: " + rowsUpdated);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hayTurnosOcupadosEnFecha(LocalDate fecha) {
        String sql = "SELECT COUNT(*) FROM Turnos WHERE fecha = ? AND estado = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha.toString());
            stmt.setString(2, "Ocupado");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //--------------------------------------------------
    //--------------FUNCIONES PARA LISTAR RESERVAS------------

    public List<Turno> obtenerTurnosPorEstado(E estado) {
        String sql = "SELECT t.*, p.nombre, p.apellido " +
                "FROM Turnos t " +
                "JOIN Personas p ON t.id_persona = p.id_persona " +
                "WHERE t.estado = ? AND t.id_persona IS NOT NULL";

        List<Turno> turnos = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Turno turno = new Turno(
                            rs.getInt("id_turno"),
                            LocalDate.parse(rs.getString("fecha")),
                            LocalTime.parse(rs.getString("hora")),
                            E.valueOf(rs.getString("estado")),
                            rs.getInt("pago"),
                            rs.getString("fecha_pago") != null ? LocalDate.parse(rs.getString("fecha_pago")) : null,
                            null, // Persona se asigna abajo
                            null, // Cancha se asigna abajo
                            rs.getString("fecha_cancelacion") != null ? LocalDate.parse(rs.getString("fecha_cancelacion")) : null,
                            rs.getString("reintegro_cancelacion")
                    );

                    // Asignar Persona completa
                    Persona persona = new Persona();
                    persona.setId(rs.getInt("id_persona"));
                    persona.setNombre(rs.getString("nombre"));
                    persona.setApellido(rs.getString("apellido"));
                    turno.setPersona(persona);

                    // Asignar Cancha
                    Cancha cancha = new Cancha();
                    cancha.setNumero(rs.getInt("num_cancha"));
                    turno.setCancha(cancha);

                    turnos.add(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return turnos;
    }

    public List<Turno> obtenerTurnosPorEstadoYFecha(E estado, LocalDate fecha) {
        String sql = "SELECT t.*, p.nombre, p.apellido " +
                "FROM Turnos t " +
                "JOIN Personas p ON t.id_persona = p.id_persona " +
                "WHERE t.estado = ? AND t.fecha = ? AND t.id_persona IS NOT NULL";

        List<Turno> turnos = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado.name());
            stmt.setString(2, fecha.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Turno turno = new Turno(
                            rs.getInt("id_turno"),
                            LocalDate.parse(rs.getString("fecha")),
                            LocalTime.parse(rs.getString("hora")),
                            E.valueOf(rs.getString("estado")),
                            rs.getInt("pago"),
                            rs.getString("fecha_pago") != null ? LocalDate.parse(rs.getString("fecha_pago")) : null,
                            null,
                            null,
                            rs.getString("fecha_cancelacion") != null ? LocalDate.parse(rs.getString("fecha_cancelacion")) : null,
                            rs.getString("reintegro_cancelacion")
                    );

                    Persona persona = new Persona();
                    persona.setId(rs.getInt("id_persona"));
                    persona.setNombre(rs.getString("nombre"));
                    persona.setApellido(rs.getString("apellido"));
                    turno.setPersona(persona);

                    Cancha cancha = new Cancha();
                    cancha.setNumero(rs.getInt("num_cancha"));
                    turno.setCancha(cancha);

                    turnos.add(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return turnos;
    }

}

