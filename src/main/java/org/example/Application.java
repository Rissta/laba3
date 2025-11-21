package org.example;

import org.example.config.DatabaseConfig;
import org.example.controller.AirplaneController;
import org.example.repository.AirplaneRepository;
import org.example.repository.AirplaneRepositoryImpl;
import org.example.service.AirplaneService;

public class Application {
    public static void main(String[] args) {
        AirplaneRepository repository = new AirplaneRepositoryImpl();
        AirplaneService service = new AirplaneService(repository);
        AirplaneController controller = new AirplaneController(service);

        controller.start();

        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConfig::closeDataSource));
    }
}