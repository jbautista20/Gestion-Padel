package DAO;

import models.Cancelacion;

import java.util.List;

public interface CancelacionDAO {
    void create(Cancelacion cancelacion); // crea una cancelacion
    void update(Cancelacion cancelacion); //actualiza
    void delete(int id); //elimina por id
    Cancelacion findById(int id); //busca por id
    List<Cancelacion>findAll(); //es como un select de toda la base
}
