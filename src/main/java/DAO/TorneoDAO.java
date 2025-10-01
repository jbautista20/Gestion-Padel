package DAO;

import models.Torneo;

import java.time.LocalDate;
import java.util.List;

public interface TorneoDAO {
    void create(Torneo torneo);
    void update(Torneo torneo);
    void delete(int id);
    Torneo findById(int id);
    List<Torneo> findAll();

    List<Torneo> findByCategoria(int categoria);
    List<Torneo> findByFecha(LocalDate fecha);
}