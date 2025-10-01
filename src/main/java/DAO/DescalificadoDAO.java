package DAO;

import models.Descalificado;

import java.util.List;

public interface DescalificadoDAO {
    void create(Descalificado descalificado);
    void update(Descalificado descalificado);
    void delete(int id);
    Descalificado findById(int id);
    List<Descalificado>findAll();
}
