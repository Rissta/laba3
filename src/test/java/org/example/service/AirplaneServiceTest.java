package org.example.service;

import org.example.entity.Airplane;
import org.example.exception.EntityNotFoundException;
import org.example.repository.AirplaneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AirplaneServiceTest {

    @Mock
    private AirplaneRepository repository;

    private AirplaneService service;

    @BeforeEach
    void setUp() {
        service = new AirplaneService(repository);
    }

    @Test
    void save_ShouldReturnId_WhenAirplaneIsValid() {

        String model = "Boeing 737";
        Integer seats = 180;
        int expectedId = 1;

        when(repository.save(any(Airplane.class))).thenReturn(expectedId);

        int actualId = service.save(model, seats);
        System.out.println(actualId);



        assertEquals(expectedId, actualId);

        verify(repository).save(any(Airplane.class));


    }

    @Test
    void findById_ShouldReturnAirplane_WhenAirplaneExists() {

        int id = 1;
        Airplane expectedAirplane = new Airplane("Boeing 737", 180);
        expectedAirplane.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(expectedAirplane));

        Airplane actualAirplane = service.findById(id);
        System.out.println(actualAirplane);

        assertNotNull(actualAirplane);
        assertEquals(expectedAirplane.getId(), actualAirplane.getId());
        assertEquals(expectedAirplane.getModel(), actualAirplane.getModel());
        assertEquals(expectedAirplane.getSeats(), actualAirplane.getSeats());



        verify(repository).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenAirplaneNotFound() {

        int id = 999;
        when(repository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.findById(id)
        );

        System.out.println(exception.getMessage());
        assertEquals("Airplane with id " + id + " not found", exception.getMessage());
        verify(repository).findById(id);
    }

    @Test
    void findAll_ShouldReturnAllAirplanes() {
        List<Airplane> expectedAirplanes = Arrays.asList(
                new Airplane("Boeing 737", 180),
                new Airplane("Airbus A320", 150)
        );
        expectedAirplanes.get(0).setId(1);
        expectedAirplanes.get(1).setId(2);

        when(repository.findAll()).thenReturn(expectedAirplanes);

        List<Airplane> actualAirplanes = service.findAll();

        assertNotNull(actualAirplanes);
        assertEquals(2, actualAirplanes.size());
        assertEquals(expectedAirplanes, actualAirplanes);
        verify(repository).findAll();
        System.out.println(actualAirplanes);

    }

    @Test
    void update_ShouldUpdateAirplane_WhenAirplaneExists() {
        Airplane airplane = new Airplane("Boeing 737 MAX", 200);
        airplane.setId(1);
        System.out.println(repository.findAll());

        when(repository.update(airplane)).thenReturn(true);

        service.update(airplane);

        verify(repository).update(airplane);

    }

    @Test
    void update_ShouldThrowException_WhenAirplaneNotFound() {

        Airplane airplane = new Airplane("Boeing 737 MAX", 200);
        airplane.setId(999);

        when(repository.update(airplane)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.update(airplane)
        );

        System.out.println(exception.getMessage());
        assertEquals("Airplane with id " + airplane.getId() + " not found for update", exception.getMessage());
        verify(repository).update(airplane);
    }

    @Test
    void update_ShouldThrowException_WhenIdIsNull() {
        Airplane airplane = new Airplane("Boeing 737 MAX", 200);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.update(airplane)
        );

        System.out.println(exception.getMessage());
        assertEquals("Airplane ID cannot be null for update", exception.getMessage());
        verify(repository, never()).update(any(Airplane.class));
    }

    @Test
    void deleteById_ShouldDeleteAirplane_WhenAirplaneExists() {
        int id = 1;
        when(repository.deleteById(id)).thenReturn(true);

        service.deleteById(id);

        verify(repository).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrowException_WhenAirplaneNotFound() {
        int id = 999;
        when(repository.deleteById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.deleteById(id)
        );

        assertEquals("Airplane with id " + id + " not found for deletion", exception.getMessage());
        verify(repository).deleteById(id);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoAirplanes() {
        when(repository.findAll()).thenReturn(List.of());

        List<Airplane> actualAirplanes = service.findAll();

        assertNotNull(actualAirplanes);
        assertTrue(actualAirplanes.isEmpty());
        verify(repository).findAll();
    }

}