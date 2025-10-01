package DAO;

import models.Partido;

import java.util.List;

public interface PartidoDAO {
    void create(Partido partido);
    void update(Partido partido);
    void delete(int id);
    Partido findById(int id);
    List<Partido>findAll();
}

