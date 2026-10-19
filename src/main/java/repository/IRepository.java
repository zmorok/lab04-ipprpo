package repository;

import java.util.List;

public interface IRepository<T> {
    List<T> getAll();
    void add(T t);
    void getInfo(int id);
    T delete(int id);
    int getMaxId();
    T getById(int id);
}
