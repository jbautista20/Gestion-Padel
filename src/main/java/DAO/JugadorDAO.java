package DAO;

import models.Jugador;

import java.util.List;

public interface JugadorDAO {
    void create(Jugador jugador);
    void update(Jugador jugador);
    void delete(int id);
    Jugador findById(int id);
    List<Jugador> findAll();
}
