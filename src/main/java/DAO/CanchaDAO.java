package DAO;
import models.Cancha;

import java.util.List;

public interface CanchaDAO {
    void create(Cancha cancha);
    void update(Cancha cancha);
    void delete(int id);
    Cancha findById(int id);
    List<Cancha>findAll();
}
