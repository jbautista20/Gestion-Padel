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
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Turno turno) {
        String sql = "INSERT INTO Turnos (fecha, id_persona, hora, estado, pago, fecha_pago, num_cancha) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, turno.getFecha().toString());
            stmt.setInt(2, turno.getPersona().getId());
            stmt.setString(3, turno.getHora().toString());
            stmt.setString(4, turno.getEstado().name());
            stmt.setInt(5, turno.getPago());
            stmt.setString(6, turno.getFecha_Pago() != null ? turno.getFecha_Pago().toString() : null);
            stmt.setInt(7, turno.getCancha().getNumero());

            stmt.executeUpdate();

            // Obtener ID generado por SQLite
            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs = stmt2.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    turno.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Turno turno) {
        String sql = "UPDATE Turnos SET fecha=?, id_persona=?, hora=?, estado=?, pago=?, fecha_pago=?, num_cancha=? WHERE id_turno=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, turno.getFecha().toString());
            stmt.setInt(2, turno.getPersona().getId());
            stmt.setString(3, turno.getHora().toString());
            stmt.setString(4, turno.getEstado().name());
            stmt.setInt(5, turno.getPago());
            stmt.setString(6, turno.getFecha_Pago() != null ? turno.getFecha_Pago().toString() : null);
            stmt.setInt(7, turno.getCancha().getNumero());
            stmt.setInt(8, turno.getId());

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
                    turno = new Turno();
                    turno.setId(rs.getInt("id_turno"));
                    turno.setFecha(LocalDate.parse(rs.getString("fecha")));
                    turno.setHora(LocalTime.parse(rs.getString("hora")));
                    turno.setEstado(E.valueOf(rs.getString("estado")));
                    turno.setPago(rs.getInt("pago"));
                    String fechaPago = rs.getString("fecha_pago");
                    turno.setFecha_Pago(fechaPago != null ? LocalDate.parse(fechaPago) : null);

                    // Cargar relaciones
                    Persona persona = new Persona();
                    persona.setId(rs.getInt("id_persona"));
                    turno.setPersona(persona);

                    Cancha cancha = new Cancha();
                    cancha.setNumero(rs.getInt("num_cancha"));
                    turno.setCancha(cancha);

                    // Cancelacion queda null por ahora
                    turno.setCancelacion(null);
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
                Turno turno = new Turno();
                turno.setId(rs.getInt("id_turno"));
                turno.setFecha(LocalDate.parse(rs.getString("fecha")));
                turno.setHora(LocalTime.parse(rs.getString("hora")));
                turno.setEstado(E.valueOf(rs.getString("estado")));
                turno.setPago(rs.getInt("pago"));
                String fechaPago = rs.getString("fecha_pago");
                turno.setFecha_Pago(fechaPago != null ? LocalDate.parse(fechaPago) : null);

                // Relaciones
                Persona persona = new Persona();
                persona.setId(rs.getInt("id_persona"));
                turno.setPersona(persona);

                Cancha cancha = new Cancha();
                cancha.setNumero(rs.getInt("num_cancha"));
                turno.setCancha(cancha);

                turno.setCancelacion(null);

                turnos.add(turno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return turnos;
    }
}
