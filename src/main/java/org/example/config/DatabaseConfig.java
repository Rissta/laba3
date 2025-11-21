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

public class DatabaseConfig {

    private static HikariDataSource dataSource;
    private static String URL = "jdbc:postgresql://213.171.3.200:5432/labs";
    private static String USERNAME = "student_15";
    private static String PASSWORD = "etLSXCAwjmFhmW";
    private static String POOL_NAME = "airplane-pool";
    private static String SCHEMA = "schema_15";
    private static Integer POOL_SIZE = 10;




    static {
        try {

            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setPoolName(POOL_NAME);
            config.setMaximumPoolSize(POOL_SIZE);
            config.setSchema(SCHEMA);

            config.addDataSourceProperty( "cachePrepStmts" , "true" ); // Кэширование PreparedStatement
            config.addDataSourceProperty( "prepStmtCacheSize" , "250" ); // Размер кэша PreparedStatement
            config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" ); // Макс. размер SQL в кэше

            dataSource = new HikariDataSource( config );

            initializeLiquibase();
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка подключения базы данных", e);
        }
    }


    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }


    private static void initializeLiquibase() {
        try (Connection connection = dataSource.getConnection()) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db-changelog.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка миграции базы данных с Liquibase", e);
        }
    }

}