package DAO;

import models.Torneo;


import java.util.List;

public interface TorneoDAO {
    void create(Torneo torneo);
    void update(Torneo torneo);
    void delete(int id);
    Torneo findById(int id);
    List<Torneo> findAll();

}