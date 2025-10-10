package DAO.impl;

import DAO.TorneoDAO;
import db.Conexion;
import models.Es;
import models.T;
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
        String sql = "INSERT INTO Torneos (tipo, categoria, fecha, premio1, premio2, valor_insc, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, torneo.getTipo().name());        // Enum T
            stmt.setInt(2, torneo.getCategoria());
            stmt.setString(3, torneo.getFecha().toString());
            stmt.setString(4, torneo.getPremio1());
            stmt.setString(5, torneo.getPremio2());
            stmt.setInt(6, torneo.getValor_Inscripcion());
            stmt.setString(7, torneo.getEstados().name());     // Enum Es

            stmt.executeUpdate(); //consulta

            // obtener el último ID insertado
            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs = stmt2.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    torneo.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Torneo torneo) {
        String sql = "UPDATE Torneos SET tipo = ?, categoria = ?, fecha = ?, premio1 = ?, premio2 = ?, valor_insc = ?, estado = ? WHERE torneo_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, torneo.getTipo().name()); // enum → String
            stmt.setInt(2, torneo.getCategoria());
            stmt.setString(3, torneo.getFecha().toString()); // LocalDate → String (YYYY-MM-DD)
            stmt.setString(4, torneo.getPremio1());
            stmt.setString(5, torneo.getPremio2());
            stmt.setInt(6, torneo.getValor_Inscripcion());
            stmt.setString(7, torneo.getEstados().name()); // enum → String
            stmt.setInt(8, torneo.getId());

            stmt.executeUpdate();
            System.out.println("Torneo actualizado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Torneos WHERE id_torneo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Torneo eliminado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Torneo findById(int id) {
        String sql = "SELECT * FROM Torneos WHERE id_torneo = ?";
        Torneo torneo = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                torneo = new Torneo();
                torneo.setId(rs.getInt("id_torneo"));
                torneo.setTipo(T.valueOf(rs.getString("tipo")));
                torneo.setCategoria(rs.getInt("categoria"));
                torneo.setFecha(LocalDate.parse(rs.getString("fecha")));
                torneo.setPremio1(rs.getString("premio1"));
                torneo.setPremio2(rs.getString("premio2"));
                torneo.setValor_Inscripcion(rs.getInt("valor_insc"));
                torneo.setEstados(Es.valueOf(rs.getString("estado")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return torneo;
    }

    public List<Torneo> findAll() {
        String sql = "SELECT id_torneo, tipo, categoria, fecha, premio1, premio2, valor_insc, estado FROM Torneos";
        List<Torneo> torneos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.println("Columna: " + meta.getColumnName(i));
            }

            while (rs.next()) {
                Torneo torneo = new Torneo();
                torneo.setId(rs.getInt("id_torneo"));
                torneo.setTipo(T.valueOf(rs.getString("tipo")));
                torneo.setCategoria(rs.getInt("categoria"));
                torneo.setFecha(LocalDate.parse(rs.getString("fecha")));
                torneo.setPremio1(rs.getString("premio1"));
                torneo.setPremio2(rs.getString("premio2"));
                torneo.setValor_Inscripcion(rs.getInt("valor_insc"));
                torneo.setEstados(Es.valueOf(rs.getString("estado")));

                torneos.add(torneo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return torneos;
    }

}