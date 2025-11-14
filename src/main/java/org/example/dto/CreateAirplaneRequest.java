package org.example.dto;

public class CreateAirplaneRequest {
    private String model;
    private Integer seats;

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
}
