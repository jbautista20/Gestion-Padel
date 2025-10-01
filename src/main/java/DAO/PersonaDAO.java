package DAO;

import models.Persona;

import java.util.List;

public interface PersonaDAO {
    void create(Persona persona);
    void update(Persona persona);
    void delete(int id);
    Persona findById(int id);
    List<Persona> findAll();
}


