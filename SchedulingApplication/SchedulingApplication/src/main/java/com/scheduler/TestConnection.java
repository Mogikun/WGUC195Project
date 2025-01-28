package com.scheduler;

import com.scheduler.dao.JDBC;
import java.sql.Connection;

/**
 * Test class for verifying database connection
 * @author Calvin Mogi
 */
public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        try {
            // Attempt to open connection
            JDBC.getConnection();
            Connection conn = JDBC.getConnection();

            // Check if connection is valid
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connection test successful!");
                System.out.println("Database connected: " + conn.getCatalog());
            } else {
                System.out.println("Connection test failed!");
            }

            // Close connection
            JDBC.closeConnection();
            System.out.println("Connection closed.");

        } catch (Exception e) {
            System.out.println("Error during connection test!");
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}