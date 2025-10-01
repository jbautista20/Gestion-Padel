package DAO.impl;

import DAO.TorneoDAO;
import db.Conexion;
import models.Torneo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TorneoDAOImpl implements TorneoDAO {

    private Connection conn;

    public TorneoDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Torneo torneo) {
        String sql = "INSERT INTO Torneo (Tipo, Categoria, Fecha, Premio1, Premio2, Valor_Inscripcion, Estados) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //stmt.setString(1, torneo.getTipo().toString());
            stmt.setInt(2, torneo.getCategoria());
            stmt.setString(3, torneo.getFecha().toString());
            stmt.setString(4, torneo.getPremio1());
            stmt.setString(5, torneo.getPremio2());
            stmt.setInt(6, torneo.getValor_Inscripcion());
            //stmt.setString(7, torneo.getEstados().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Torneo torneo) {
        String sql = "UPDATE Torneo SET Tipo=?, Categoria=?, Fecha=?, Premio1=?, Premio2=?, Valor_Inscripcion=?, Estados=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //stmt.setString(1, torneo.getTipo().toString());
            stmt.setInt(2, torneo.getCategoria());
            stmt.setString(3, torneo.getFecha().toString());
            stmt.setString(4, torneo.getPremio1());
            stmt.setString(5, torneo.getPremio2());
            stmt.setInt(6, torneo.getValor_Inscripcion());
            //stmt.setString(7, torneo.getEstados().toString());
            stmt.setInt(8, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Torneo WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Torneo findById(int id) {
        String sql = "SELECT * FROM Torneo WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Torneo torneo = new Torneo();
                torneo.setCategoria(rs.getInt("Categoria"));
                torneo.setFecha(LocalDate.parse(rs.getString("Fecha")));
                torneo.setPremio1(rs.getString("Premio1"));
                torneo.setPremio2(rs.getString("Premio2"));
                torneo.setValor_Inscripcion(rs.getInt("Valor_Inscripcion"));
                return torneo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Torneo> findAll() {
        List<Torneo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Torneo";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Torneo torneo = new Torneo();
                torneo.setCategoria(rs.getInt("Categoria"));
                torneo.setFecha(LocalDate.parse(rs.getString("Fecha")));
                torneo.setPremio1(rs.getString("Premio1"));
                torneo.setPremio2(rs.getString("Premio2"));
                torneo.setValor_Inscripcion(rs.getInt("Valor_Inscripcion"));
                lista.add(torneo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Torneo> findByCategoria(int categoria) {
        List<Torneo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Torneo WHERE Categoria=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoria);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Torneo torneo = new Torneo();
                torneo.setCategoria(rs.getInt("Categoria"));
                torneo.setFecha(LocalDate.parse(rs.getString("Fecha")));
                torneo.setPremio1(rs.getString("Premio1"));
                torneo.setPremio2(rs.getString("Premio2"));
                torneo.setValor_Inscripcion(rs.getInt("Valor_Inscripcion"));
                lista.add(torneo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Torneo> findByFecha(LocalDate fecha) {
        return List.of();
    }
}
