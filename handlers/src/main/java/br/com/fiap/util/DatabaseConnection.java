package br.com.fiap.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            connection = createConnection();
        }

        return connection;
    }

    private static Connection createConnection() {
        final String url = System.getenv("DB_URL");
        final String user = System.getenv("DB_USERNAME");
        final String password = System.getenv("DB_PASSWORD");

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}