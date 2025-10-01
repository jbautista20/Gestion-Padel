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
        String sql = "INSERT INTO Torneo (tipo, categoria, fecha, premio1, premio2, valor_insc, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, torneo.getTipo().name());        // Enum T
            stmt.setInt(2, torneo.getCategoria());
            stmt.setString(3, torneo.getFecha().toString());
            stmt.setString(4, torneo.getPremio1());
            stmt.setString(5, torneo.getPremio2());
            stmt.setInt(6, torneo.getValor_Inscripcion());
            stmt.setString(7, torneo.getEstados().name());     // Enum Es

            stmt.executeUpdate();

            //recuperar id de la bd
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                torneo.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Torneo torneo) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Torneo findById(int id) {
        return null;
    }

    @Override
    public List<Torneo> findAll() {
        return List.of();
    }
}