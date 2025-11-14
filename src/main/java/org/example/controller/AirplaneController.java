package org.example.controller;

import org.example.dto.CreateAirplaneRequest;
import org.example.entity.Airplane;
import org.example.service.AirplaneService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AirplaneController {
    private final AirplaneService airplaneService;
    private final ObjectMapper objectMapper;

    public AirplaneController(AirplaneService airplaneService) {
        this.airplaneService = airplaneService;
        this.objectMapper = new ObjectMapper();
    }

    public void start() {
        Javalin app = Javalin.create().start(8080);

        app.get("/airplanes", this::getAllAirplanes);
        app.get("/airplanes/{id}", this::getAirplaneById);
        app.post("/airplanes", this::createAirplane);
        app.put("/airplanes/{id}", this::updateAirplane);
        app.delete("/airplanes/{id}", this::deleteAirplane);
    }

    private void getAllAirplanes(Context ctx) {
        try {
            var airplanes = airplaneService.findAll();
            ctx.json(airplanes);
        } catch (Exception e) {
            ctx.status(400).result("Error retrieving airplanes: " + e.getMessage());
        }
    }

    private void getAirplaneById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var airplane = airplaneService.findById(id);
            ctx.json(airplane);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid ID format");
        } catch (Exception e) {
            ctx.status(404).result(e.getMessage());
        }
    }

    private void createAirplane(Context ctx) {
        try {
            var airplaneRequest = objectMapper.readValue(ctx.body(), CreateAirplaneRequest.class);
            int id = airplaneService.save(airplaneRequest.getModel(), airplaneRequest.getSeats());
            ctx.status(200).result(String.valueOf(id));
        } catch (Exception e) {
            ctx.status(400).result("Error creating airplane: " + e.getMessage());
        }
    }

    // Тут
    private void updateAirplane(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var airplane = objectMapper.readValue(ctx.body(), Airplane.class);
            airplane.setId(id);
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