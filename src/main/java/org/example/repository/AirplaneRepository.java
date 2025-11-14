package org.example.repository;

import org.example.entity.Airplane;

import java.util.List;
import java.util.Optional;

// Интерфейс для работы с базой данных с помощью нашего класса Airplane
// В интерфейсах только абстрактные классы, без логики

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
