package com.scheduler.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone=SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "root";
    private static final String password = "pokemon1";

    private static Connection connection = null;

    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(jdbcUrl, userName, password);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Error:" + e.getMessage());
            }
        } else {
            try {
                // Check if connection is still valid
                if (!connection.isValid(5)) {
                    System.out.println("Connection lost, reconnecting...");
                    connection = DriverManager.getConnection(jdbcUrl, userName, password);
                }
            } catch (SQLException e) {
                System.out.println("Error checking connection:" + e.getMessage());
                try {
                    connection = DriverManager.getConnection(jdbcUrl, userName, password);
                } catch (SQLException ex) {
                    System.out.println("Error reconnecting:" + ex.getMessage());
                }
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("Connection closed!");
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
}