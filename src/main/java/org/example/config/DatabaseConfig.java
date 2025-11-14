package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;


import java.sql.Connection;
import java.sql.SQLException;

// Класс для конфигурации и управления подключением к базе данных
// Использует HikariCP для подключения и Liquibase для миграций

public class DatabaseConfig {

    private static HikariDataSource dataSource;
    private static String URL = "jdbc:postgresql://localhost:5432/laba3_db";
    private static String USERNAME = "postgres";
    private static String PASSWORD = "3123865";
    private static String POOL_NAME = "airplane-pool";
    private static Integer POOL_SIZE = 10;




    static {
        try {
            // HikariConfig - хранит настройки подключения к бд
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(URL); // URL базы данных
            config.setUsername(USERNAME); // Имя пользователя
            config.setPassword(PASSWORD); // Пароль
            config.setPoolName(POOL_NAME); // Имя пула соединений
            config.setMaximumPoolSize(POOL_SIZE); // Макс. размер пула

            config.addDataSourceProperty( "cachePrepStmts" , "true" ); // Кэширование PreparedStatement
            config.addDataSourceProperty( "prepStmtCacheSize" , "250" ); // Размер кэша PreparedStatement
            config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" ); // Макс. размер SQL в кэше

            // Создание пула соединений с указанной конфигурацией
            dataSource = new HikariDataSource( config );

            // Выполняем миграцию бд с файла db-changelog
            initializeLiquibase();
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка подключения базы данных", e);
        }
    }



    // Предоставляет соединение с базой данных из пула
    // Возвращает объект для работы с БД
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Закрывает соединение (Вызывать самому в конце работы программы)
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    // Инициализирует и выполняет миграции базы данных с помощью Liquibase
    // Создает таблицы, индексы, и т.д. как в db-changelog.xml
    private static void initializeLiquibase() {
        try (Connection connection = dataSource.getConnection()) {

            // findCorrectDatabaseImplementation автоматически определяет тип БД (PostgreSQL, MySQL и т.д.)
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db-changelog.xml",  // Файл с описанием изменений БД
                    new ClassLoaderResourceAccessor(), // Для загрузки файлов из classpath
                    database // Целевая база данных
            );

            // Выполняем все pending миграции
            liquibase.update();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка миграции базы данных с Liquibase", e);
        }
    }

}