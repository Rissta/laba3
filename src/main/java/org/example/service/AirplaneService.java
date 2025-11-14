package org.example.service;

import org.example.entity.Airplane;
import org.example.exception.EntityNotFoundException;
import org.example.repository.AirplaneRepository;

import java.util.List;

public class AirplaneService implements EntityService {

    private final AirplaneRepository repository;

    public AirplaneService(AirplaneRepository repository) {
        this.repository = repository;
    }

    @Override
    public int save(String model, Integer seats) {
        Airplane airplane = new Airplane(model, seats);
        return repository.save(airplane);
    }

    @Override
    public Airplane findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airplane with id " + id + " not found"));
    }

    @Override
    public List<Airplane> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Airplane airplane) {
        if (airplane.getId() == null) {
            throw new IllegalArgumentException("Airplane ID cannot be null for update");
        }

        boolean updated = repository.update(airplane);
        if (!updated) {
            throw new EntityNotFoundException("Airplane with id " + airplane.getId() + " not found for update");
        }
    }

    @Override
    public void deleteById(int id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Airplane with id " + id + " not found for deletion");
        }
    }
}