package DAO.impl;

import DAO.CanchaDAO;
import db.Conexion;
import models.Cancha;
import models.Turno;
import models.Partido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CanchaDAOImpl implements CanchaDAO {

    private Connection conn;

    public CanchaDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Cancha cancha) {
        String sql = "INSERT INTO Canchas (numero) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cancha.getNumero());
            stmt.executeUpdate();
            System.out.println("Cancha insertada correctamente: " + cancha.getNumero());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Cancha cancha) {
        String sql = "UPDATE Canchas SET numero = ? WHERE numero = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cancha.getNumero()); // cambiar el número
            stmt.setInt(2, cancha.getNumero());
            stmt.executeUpdate();
            System.out.println("Cancha actualizada: " + cancha.getNumero());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int numero) {
        String sql = "DELETE FROM Canchas WHERE numero = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            stmt.executeUpdate();
            System.out.println("Cancha eliminada: " + numero);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cancha findById(int numero) {
        String sql = "SELECT * FROM Canchas WHERE numero = ?";
        Cancha cancha = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cancha = new Cancha();
                    cancha.setNumero(rs.getInt("numero"));
                    cancha.setTurnos(new ArrayList<>());
                    cancha.setPartidos(new ArrayList<>());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cancha;
    }

    @Override
    public List<Cancha> findAll() {
        String sql = "SELECT * FROM Canchas";
        List<Cancha> canchas = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cancha cancha = new Cancha();
                cancha.setNumero(rs.getInt("numero"));
                cancha.setTurnos(new ArrayList<>());
                cancha.setPartidos(new ArrayList<>());
                canchas.add(cancha);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return canchas;
    }
}
