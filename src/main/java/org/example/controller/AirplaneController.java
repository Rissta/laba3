package org.example.controller;

import org.example.dto.AirplaneDTO;
import org.example.entity.Airplane;
import org.example.service.AirplaneService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class AirplaneController {
    private final AirplaneService airplaneService;
    private final ObjectMapper objectMapper;

    public AirplaneController(AirplaneService airplaneService) {
        this.airplaneService = airplaneService;
        this.objectMapper = new ObjectMapper();
    }

    //Тут уже гпт расписал потому что надо разбираться как устроено внутри

    public void start() {

        //Javalin.create() создаёт серверный экземпляр с дефолтной конфигурацией. Можно передать конфиг через Javalin.create(cfg -> { ... })
        //(например, включить CORS, логирование, добавить свой JSON mapper и т.д.).
        //start(8080) запускает встроенный Jetty на порту 8080. После вызова start сервер прослушивает запросы.
        //Обратите внимание: вы не сохраняете app в поле и не реализуете stop() — это нормально для простого приложения,
        // но в тестах/production обычно требуется контроль lifecycle (например, app.stop() при shutdown).
        //app.get(path, handler) — регистрирует обработчик (Handler — функциональный интерфейс (Context ctx) -> void).
        //Здесь передаётся метод экземпляра.

        Javalin app = Javalin.create().start(8080);

        app.get("/airplanes", this::getAllAirplanes);
        app.get("/airplanes/{id}", this::getAirplaneById);
        app.post("/airplanes", this::createAirplane);
        app.put("/airplanes/{id}", this::updateAirplane);
        app.delete("/airplanes/{id}", this::deleteAirplane);
    }


    //Context — центральный объект обработчика запросов (Javalin)
    //io.javalin.http.Context инкапсулирует HTTP-запрос и ответ, и предоставляет удобные методы:
    //ctx.pathParam("id") — извлекает параметр пути как String. Есть удобные альтернативы:
    //ctx.pathParamAsClass("id", Integer.class).get() — парсит и конвертирует,
    // возвращая ValidationResult/Optional-подобный объект; можно обработать ошибку парсинга аккуратнее.
    //ctx.body() — возвращает тело запроса как String.
    //Альтернатива: ctx.bodyAsClass(Airplane.class) — Javalin сам десериализует JSON в объект, используя свой JSON mapper.
    //ctx.json(obj) — сериализует obj в JSON и ставит Content-Type: application/json.
    //ctx.status(200) — ставит код ответа.
    //ctx.result("...") — устанавливает тело ответа (plain text или уже сериализованное). ctx.json() и
    //ctx.result() можно комбинировать (но не нужно: используйте json для объектов, result для строк).
    private void getAllAirplanes(Context ctx) {
        try {
            List<Airplane> airplanes = airplaneService.findAll();
            List<AirplaneDTO> response = airplanes.stream()
                    .map(a -> new AirplaneDTO(a.getId(), a.getModel(), a.getSeats()))
                    .toList();
            ctx.json(response);

        } catch (Exception e) {
            ctx.status(400).result("Error retrieving airplanes: " + e.getMessage());
        }
    }

    private void getAirplaneById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Airplane airplane = airplaneService.findById(id);
            AirplaneDTO response = new AirplaneDTO(
                    airplane.getId(),
                    airplane.getModel(),
                    airplane.getSeats()
            );
            ctx.json(response);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid ID format");
        } catch (Exception e) {
            ctx.status(404).result(e.getMessage());
        }
    }

    private void createAirplane(Context ctx) {
        try {
            AirplaneDTO request = objectMapper.readValue(ctx.body(), AirplaneDTO.class);
            int id = airplaneService.save(request.getModel(), request.getSeats());
            ctx.status(200).result(String.valueOf(id));
        } catch (Exception e) {
            ctx.status(400).result("Error creating airplane: " + e.getMessage());
        }
    }

    private void updateAirplane(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            AirplaneDTO airplaneRequest = objectMapper.readValue(ctx.body(), AirplaneDTO.class);
            Airplane airplane = airplaneService.findById(id);
            airplane.setSeats(airplaneRequest.getSeats());
            airplane.setModel(airplaneRequest.getModel());
            airplaneService.update(airplane);
            ctx.status(200);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid ID format");
        } catch (Exception e) {
            ctx.status(404).result(e.getMessage());
        }
    }


    private void deleteAirplane(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            airplaneService.deleteById(id);
            ctx.status(200);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid ID format");
        } catch (Exception e) {
            ctx.status(404).result(e.getMessage());
        }
    }

}