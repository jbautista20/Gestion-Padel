package DAO;

import java.util.List;

public interface GenericDAO<T> {
    void create(T t);
    void update(T t);
    void delete(int id);
    T findById(int id);
    List<T> findAll();
}
