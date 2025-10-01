package DAO;

import models.Cancelacion;

import java.util.List;

public interface CancelacionDAO {
    void create(Cancelacion cancelacion);
    void update(Cancelacion cancelacion);
    void delete(int id);
    Cancelacion findById(int id);
    List<Cancelacion>findAll();
}
