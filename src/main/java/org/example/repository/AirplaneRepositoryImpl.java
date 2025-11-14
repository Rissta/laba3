package org.example.repository;

import org.example.config.DatabaseConfig;
import org.example.entity.Airplane;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Ну я тут пару объясню. Еще и самый нижний метод

// Это расширения работы нашего интерфейса для работы с базой данных

public class AirplaneRepositoryImpl implements AirplaneRepository {

    // Запись объекта в бд
    // Передаем объект класса airplane
    @Override
    public int save(Airplane airplane) {
        // Пишем запрос ручками. Тут уже сами. Разберетесь
        String sql = "INSERT INTO airplane (model, seats) " +
                "VALUES (?, ?) RETURNING id";

        // Открываем соединение с бд
        // PreparedStatement — метод prepareStatement() класса Connection.
        // В метод передаётся выражение SQL, которое может содержать знаки подстановки.
        // В нашем случае знаки подстановки это вопрос '?'
        // От sql инъекций кстати не защищает
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Подставляем наши параметры в запрос по порядку.
            // Первый вопрос в запросе это - set у которого первый параметр единица
            pstmt.setString(1, airplane.getModel());
            pstmt.setInt(2, airplane.getSeats());

            // Выполняем запрос и получаем
            // так сказать табличный результат sql запроса
            // ResultSet - позволяет перемещаться по нашей таблице
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Creating airplane failed, no ID obtained.");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving airplane", e);
        }
    }

    // См. далее
    @Override
    public Optional<Airplane> findById(int id) {
        String sql = "SELECT * FROM airplane WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAirplane(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding airplane by id: " + id, e);
        }
    }

    // Получение всех продуктов
    // Начало аналогично как в первом запросе
    // Только нет параметров и запрос другой
    @Override
    public List<Airplane> findAll() {
        String sql = "SELECT * FROM airplane ORDER BY id";
        List<Airplane> airplanes = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // После получения всех пользователь
            // Перемещаемся по всем, до последнее
            // и записываем их с помощью нашего метода
            // который преобразовывает данные из sql запроса в объект airplane
            // Метод в конце этого класса
            while (rs.next()) {
                airplanes.add(mapResultSetToAirplane(rs));
            }
            return airplanes;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all airplanes", e);
        }
    }

    @Override
    public List<Airplane> findByCategory(String category) {
        String sql = "SELECT * FROM airplane WHERE category = ? ORDER BY name";
        List<Airplane> airplanes = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                airplanes.add(mapResultSetToAirplane(rs));
            }
            return airplanes;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding airplanes by category: " + category, e);
        }
    }

    @Override
    public List<Airplane> findByNameContaining(String name) {
        String sql = "SELECT * FROM airplane WHERE name ILIKE ? ORDER BY name";
        List<Airplane> airplanes = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                airplanes.add(mapResultSetToAirplane(rs));
            }
            return airplanes;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding airplanes by name: " + name, e);
        }
    }

    @Override
    public boolean update(Airplane airplane) {
        String sql = "UPDATE airplane SET model = ?, seats = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, airplane.getModel());
            pstmt.setInt(2, airplane.getSeats());
            pstmt.setInt(3, airplane.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating airplane: " + airplane.getId(), e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM airplane WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting airplane by id: " + id, e);
        }
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM airplane WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error checking if airplane exists: " + id, e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM airplane";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error counting airplanes", e);
        }
    }

    // Метод преобразования данных продукта из sql запроса в объект airplane
    private Airplane mapResultSetToAirplane(ResultSet rs) throws SQLException {
        Airplane airplane = new Airplane();
        // В сетторы передаем колонки из sql
        // Указываем какой тип должны получить и в качестве параметра название столбца
        airplane.setId(rs.getInt("id"));
        airplane.setModel(rs.getString("model"));
        airplane.setSeats(rs.getInt("seats"));
        
        return airplane;
    }

}
