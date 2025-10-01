package DAO;

import models.Turno;
import java.util.List;

public interface TurnoDAO {
    void create(Turno turno);
    void update(Turno turno);
    void delete(int id);
    Turno findById(int id);
    List<Turno>findAll();
}
