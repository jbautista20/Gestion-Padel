package DAO;

import models.Equipo;


import java.util.List;

public interface EquipoDAO {
    void create(Equipo equipo);
    void update(Equipo equipo);
    void delete(int id);
    Equipo findById(int id);
    List<Equipo> findAll();
}
