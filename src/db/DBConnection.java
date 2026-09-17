package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Static instance for Singleton (Module 3)
    private static Connection connection = null;

    // XAMPP default database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/disaster_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Keep empty for XAMPP

    // Private constructor prevents multiple connections
    private DBConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load the MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Database connection failed!");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Main method so you can test it right now
    public static void main(String[] args) {
        getConnection();
    }
}