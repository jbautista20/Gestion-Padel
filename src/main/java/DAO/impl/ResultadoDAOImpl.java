package DAO.impl;

import DAO.ResultadoDAO;
import db.Conexion;
import models.Resultado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultadoDAOImpl implements ResultadoDAO {

    private Connection conn;

    public ResultadoDAOImpl() {
        this.conn = Conexion.getConexion();
    }

    @Override
    public void create(Resultado resultado) {
        String sql = "INSERT INTO Resultados (id_partido, set1, set2, set3) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, resultado.getPartido() != null ? resultado.getPartido().getId() : 0);
            stmt.setString(2, resultado.getSet1());
            stmt.setString(3, resultado.getSet2());
            stmt.setString(4, resultado.getSet3());

            stmt.executeUpdate();

            // obtener el id generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    resultado.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Resultado resultado) {
        String sql = "UPDATE Resultados SET id_partido = ?, set1 = ?, set2 = ?, set3 = ? WHERE id_resultado = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resultado.getPartido() != null ? resultado.getPartido().getId() : 0);
            stmt.setString(2, resultado.getSet1());
            stmt.setString(3, resultado.getSet2());
            stmt.setString(4, resultado.getSet3());
            stmt.setInt(5, resultado.getId());

            stmt.executeUpdate();
            System.out.println("Resultado actualizado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Resultados WHERE id_resultado = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Resultado eliminado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resultado findById(int id) {
        String sql = "SELECT * FROM Resultados WHERE id_resultado = ?";
        Resultado resultado = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                resultado = new Resultado(
                        rs.getInt("id_resultado"),
                        rs.getString("set1"),
                        rs.getString("set2"),
                        rs.getString("set3"),
                        null // Partido → cargar con PartidoDAO.findById(rs.getInt("id_partido"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    @Override
    public List<Resultado> findAll() {
        String sql = "SELECT * FROM Resultados";
        List<Resultado> resultados = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Resultado resultado = new Resultado(
                        rs.getInt("id_resultado"),
                        rs.getString("set1"),
                        rs.getString("set2"),
                        rs.getString("set3"),
                        null // Partido → cargar con PartidoDAO si hace falta
                );
                resultados.add(resultado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }
}