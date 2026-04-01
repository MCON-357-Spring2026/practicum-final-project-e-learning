package com.elearning.service;

import java.util.List;
import java.util.Optional;

public interface ServiceInterface<T> {
    List<T> getAll();
    Optional<T> getById(String id);
    T create(T obj);
    Optional<T> update(String id, T obj);
    Optional<T> replace(String id, T obj);
    boolean delete(String id);
}
