package DAO;
import models.Resultado;

import java.util.List;
public interface ResultadoDAO {
    void create(Resultado resultado);
    void update(Resultado resultado);
    void delete(int id);
    Resultado findById(int id);
    List<Resultado>findAll();
}
