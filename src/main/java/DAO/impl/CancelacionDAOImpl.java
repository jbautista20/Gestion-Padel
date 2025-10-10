package DAO.impl;

import DAO.GenericDAO;
import db.Conexion;
import models.Cancelacion;
import models.Turno;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CancelacionDAOImpl implements GenericDAO<Cancelacion> {

    private Connection conn;

    public CancelacionDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Cancelacion cancelacion) {
        String sql = "INSERT INTO Cancelaciones (fecha, reintegro, id_turno) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cancelacion.getFecha().toString());
            stmt.setInt(2, cancelacion.getReintegro());
            stmt.setInt(3, cancelacion.getTurno().getId());

            stmt.executeUpdate();

            // Obtener ID generado
            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs = stmt2.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    cancelacion.setId(rs.getInt(1));
                }
            }

            System.out.println("Cancelación insertada ID: " + cancelacion.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Cancelacion cancelacion) {
        String sql = "UPDATE Cancelaciones SET fecha=?, reintegro=?, id_turno=? WHERE id_cancelacion=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cancelacion.getFecha().toString());
            stmt.setInt(2, cancelacion.getReintegro());
            stmt.setInt(3, cancelacion.getTurno().getId());
            stmt.setInt(4, cancelacion.getId());

            stmt.executeUpdate();
            System.out.println("Cancelación actualizada ID: " + cancelacion.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Cancelaciones WHERE id_cancelacion=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Cancelación eliminada ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cancelacion findById(int id) {
        String sql = "SELECT * FROM Cancelaciones WHERE id_cancelacion=?";
        Cancelacion cancelacion = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cancelacion = new Cancelacion();
                    cancelacion.setId(rs.getInt("id_cancelacion"));
                    cancelacion.setFecha(LocalDate.parse(rs.getString("fecha")));
                    cancelacion.setReintegro(rs.getInt("reintegro"));

                    Turno turno = new Turno();
                    turno.setId(rs.getInt("id_turno"));
                    cancelacion.setTurno(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cancelacion;
    }

    @Override
    public List<Cancelacion> findAll() {
        String sql = "SELECT * FROM Cancelaciones";
        List<Cancelacion> cancelaciones = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cancelacion cancelacion = new Cancelacion();
                cancelacion.setId(rs.getInt("id_cancelacion"));
                cancelacion.setFecha(LocalDate.parse(rs.getString("fecha")));
                cancelacion.setReintegro(rs.getInt("reintegro"));

                Turno turno = new Turno();
                turno.setId(rs.getInt("id_turno"));
                cancelacion.setTurno(turno);

                cancelaciones.add(cancelacion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cancelaciones;
    }
}
