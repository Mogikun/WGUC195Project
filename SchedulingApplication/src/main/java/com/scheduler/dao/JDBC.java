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
    private static final String userName = "root"; // MySQL username
    private static final String password = "pokemon1"; // MySQL password

    private static Connection connection = null;

    public static void openConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
                System.out.println("Connection closed!");
            }
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
}