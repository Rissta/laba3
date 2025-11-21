package org.example.repository;

import org.example.entity.Airplane;

import java.util.List;
import java.util.Optional;


public interface AirplaneRepository {
    int save(Airplane airplane);
    Optional<Airplane> findById(int id);
    List<Airplane> findAll();
    List<Airplane> findByCategory(String category);
    List<Airplane> findByNameContaining(String name);
    boolean update(Airplane airplane);
    boolean deleteById(int id);
    boolean existsById(int id);
    int count();

}
