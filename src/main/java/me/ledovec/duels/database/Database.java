package me.ledovec.duels.database;

import java.util.HashMap;
import java.util.List;

public interface Database<T, V> {

    void createTable();

    void create(T t);
    void update(T t, HashMap<String, V> values);

    List<Integer> get(T t);
}
