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


//Важное отступление:
//Устройство теста. Мы проверяем работу не репозитория который работает с бд,
//а работу сервиса, который работает с репозиторием.
//мы заменяем логику репозитория чтобы не работать с реальной бд,
//а просто возвращать значения которые должны прийти


//@ExtendWith(MockitoExtension.class)
//Это аннотация JUnit Jupiter, которая расширяет жизненный цикл теста дополнительной логикой.
//JUnit создаёт экземпляр тестового класса.
//Находит все расширения (Extensions), подключённые через @ExtendWith.
//MockitoExtension внедряет в жизненный цикл теста обработчики Mockito.
//Что делает MockitoExtension:
//Сканирует поля тестового класса на наличие @Mock, @InjectMocks, @Captor.
//Автоматически создаёт мок-объекты (прокси).
//Инициализирует их до выполнения каждого теста.
//Обеспечивает строгий порядок валидации взаимодействий (при использовании strict stubs).
//Следит за тем, чтобы тесты выполнялись в изолированной среде, где каждый мок создаётся заново.

@ExtendWith(MockitoExtension.class)
class AirplaneServiceTest {

    //@Mock
    //Помечает поле для создания Mockito.
    //Mockito генерирует динамический прокси (через ByteBuddy), который:
    //перехватывает вызовы методов интерфейса/класса,
    //записывает данные о вызовах в internal invocation store,
    //не выполняет реальный код, а отдаёт заранее настроенное поведение (when(...) → thenReturn(...)).
    //Важное: без определения возращает:
    //0/false/null по умолчанию,
    //пустые коллекции для популярных типов.

    // Проще говоря все методы

    @Mock
    private AirplaneRepository repository;

    private AirplaneService service;

    //@BeforeEach
    //Обозначает метод, который запускается перед каждым @Test.
    //JUnit на каждом тесте создаёт новый инстанс класса.
    //После инстанцирования выполняет метод с @BeforeEach.
    //Это гарантирует чистое состояние между тестами.

    @BeforeEach
    void setUp() {
        service = new AirplaneService(repository);
    }

    //@Test
    //
    //Обозначает тестовый метод.
    //JUnit оборачивает тест в ExecutionContext.
    //Управляет исключениями, таймаутами, расширениями, моками и т.д.
    //Любое неперехваченное исключение → тест "падает".

    @Test
    void save_ShouldReturnId_WhenAirplaneIsValid() {
        // Arrange
        String model = "Boeing 737";
        Integer seats = 180;
        int expectedId = 1;

        //when(...).thenReturn(...)
        //when(...) регистрирует match rule — правило соответствия вызова.
        //Mockito создаёт запись об опрации, return который нужно заменить.
        //thenReturn(...) завершает настройку и привязывает к правилу ответ.
        //Когда repository.save(any(Airplane.class)) позже вызывается, Mockito:
        //сравнивает параметры с matchers (через ArgumentMatcher),
        //находит подходящее правило,
        //возвращает заранее установленное значение.
        //Если написать функцию которая полчилась, то она выглядит так:
        //int save(Airplane airplane) {
        //  return 1;
        //}

        when(repository.save(any(Airplane.class))).thenReturn(expectedId);

        // Act
        int actualId = service.save(model, seats);
        System.out.println(actualId);

        // Assert

        // assertEquals использует equals() объекта для проверки на равенство

        assertEquals(expectedId, actualId);

        // Проверяет, что метод был вызван.
        //Mockito ведёт invocation log — журнал вызовов мок-объекта.
        //При verify():
        //проходится по логу,
        //ищет соответствующий вызов (по имени метода и аргументам),
        //если вызова нет то выдает ошибку.

        verify(repository).save(any(Airplane.class));


    }

    @Test
    void findById_ShouldReturnAirplane_WhenAirplaneExists() {
        // Arrange
        int id = 1;
        Airplane expectedAirplane = new Airplane("Boeing 737", 180);
        expectedAirplane.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(expectedAirplane));

        // Act
        Airplane actualAirplane = service.findById(id);
        System.out.println(actualAirplane);

        // Assert
        assertNotNull(actualAirplane);
        assertEquals(expectedAirplane.getId(), actualAirplane.getId());
        assertEquals(expectedAirplane.getModel(), actualAirplane.getModel());
        assertEquals(expectedAirplane.getSeats(), actualAirplane.getSeats());



        verify(repository).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenAirplaneNotFound() {
        // Arrange
        int id = 999;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert

        //Проверяет, что вызываемый код бросил конкретное исключение.
        //JUnit вызывает лямбдо функцию внутри блока try/catch.
        //Ловит все (Throwable).
        //Проверяет тип через instanceof.
        //Возвращает пойманное исключение, чтобы можно было проверить сообщение.

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
        // Arrange
        List<Airplane> expectedAirplanes = Arrays.asList(
                new Airplane("Boeing 737", 180),
                new Airplane("Airbus A320", 150)
        );
        expectedAirplanes.get(0).setId(1);
        expectedAirplanes.get(1).setId(2);

        when(repository.findAll()).thenReturn(expectedAirplanes);

        // Act
        List<Airplane> actualAirplanes = service.findAll();

        // Assert
        assertNotNull(actualAirplanes);
        assertEquals(2, actualAirplanes.size());
        assertEquals(expectedAirplanes, actualAirplanes);
        verify(repository).findAll();
        System.out.println(actualAirplanes);

    }

    @Test
    void update_ShouldUpdateAirplane_WhenAirplaneExists() {
        // Arrange
        Airplane airplane = new Airplane("Boeing 737 MAX", 200);
        airplane.setId(1);
        System.out.println(repository.findAll());

        when(repository.update(airplane)).thenReturn(true);

        // Act
        service.update(airplane);

        // Assert
        verify(repository).update(airplane);

    }

    @Test
    void update_ShouldThrowException_WhenAirplaneNotFound() {
        // Arrange
        Airplane airplane = new Airplane("Boeing 737 MAX", 200);
        airplane.setId(999);

        when(repository.update(airplane)).thenReturn(false);

        // Act & Assert
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
        // Arrange
        Airplane airplane = new Airplane("Boeing 737 MAX", 200);
        // ID is null by default

        // Act & Assert
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
        // Arrange
        int id = 1;
        when(repository.deleteById(id)).thenReturn(true);

        // Act
        service.deleteById(id);

        // Assert
        verify(repository).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrowException_WhenAirplaneNotFound() {
        // Arrange
        int id = 999;
        when(repository.deleteById(id)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.deleteById(id)
        );

        assertEquals("Airplane with id " + id + " not found for deletion", exception.getMessage());
        verify(repository).deleteById(id);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoAirplanes() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<Airplane> actualAirplanes = service.findAll();

        // Assert
        assertNotNull(actualAirplanes);
        assertTrue(actualAirplanes.isEmpty());
        verify(repository).findAll();
    }

}