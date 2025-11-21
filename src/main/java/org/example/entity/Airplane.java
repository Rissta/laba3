package org.example.entity;


import java.util.Objects;

public class Airplane {

    Integer id;
    String model;
    Integer seats;

    public Airplane() {}

    public Airplane(String model, Integer seats) {
        this.model = model;
        this.seats = seats;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Airplane{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", seats=" + seats +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Airplane airplane = (Airplane) o;
        return Objects.equals(id, airplane.id) && Objects.equals(model, airplane.model) && Objects.equals(seats, airplane.seats);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
