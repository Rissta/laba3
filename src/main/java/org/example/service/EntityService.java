package org.example.service;

import org.example.entity.Airplane;
import java.util.List;

public interface EntityService {
    int save(String model, Integer seats);

    Airplane findById(int id);

    List<Airplane> findAll();

    void update(Airplane airplane);

    void deleteById(int id);
}