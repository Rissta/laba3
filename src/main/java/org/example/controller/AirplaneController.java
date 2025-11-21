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