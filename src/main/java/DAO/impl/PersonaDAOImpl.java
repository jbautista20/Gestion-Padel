package DAO.impl;

import DAO.GenericDAO;
import db.Conexion;
import models.Persona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAOImpl implements GenericDAO<Persona> {

    private Connection conn;

    public PersonaDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Persona persona) {
        String sql = "INSERT INTO Personas (nombre, apellido, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getApellido());
            stmt.setString(3, persona.getTelefono());
            stmt.setString(4, persona.getDireccion());

            stmt.executeUpdate();

            // obtener el id generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    persona.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Persona persona) {
        String sql = "UPDATE Personas SET nombre = ?, apellido = ?, telefono = ?, direccion = ? WHERE id_persona = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getApellido());
            stmt.setString(3, persona.getTelefono());
            stmt.setString(4, persona.getDireccion());
            stmt.setInt(5, persona.getId());

            stmt.executeUpdate();
            System.out.println("Persona actualizada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Personas WHERE id_persona = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Persona eliminada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Persona findById(int id) {
        String sql = "SELECT * FROM Personas WHERE id_persona = ?";
        Persona persona = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                persona = new Persona(
                        rs.getInt("id_persona"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        null // los turnos no se cargan acá (otro DAO)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persona;
    }

    @Override
    public List<Persona> findAll() {
        String sql = "SELECT * FROM Personas";
        List<Persona> personas = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Persona persona = new Persona(
                        rs.getInt("id_persona"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        null
                );
                personas.add(persona);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personas;
    }
}