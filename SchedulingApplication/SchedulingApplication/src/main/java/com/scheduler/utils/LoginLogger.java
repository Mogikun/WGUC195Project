package com.scheduler.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging login activities
 * @author [Your Name]
 */
public class LoginLogger {
    private static final String LOG_FILE = "login_activity.txt";
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs a login attempt to the login_activity.txt file
     * @param username The username used in the attempt
     * @param success Whether the login attempt was successful
     * @param ipAddress The IP address of the attempt (optional)
     */
    public static void log(String username, boolean success) {
        LocalDateTime timestamp = LocalDateTime.now();
        String status = success ? "SUCCESSFUL" : "FAILED";

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String logEntry = String.format("%s [%s] User '%s' - Login attempt %s",
                    timestamp.format(formatter),
                    timestamp.getHour() < 12 ? "AM" : "PM",
                    username,
                    status
            );
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads the entire login activity log
     * @return String containing all log entries
     */
    public static String readLog() {
        StringBuilder log = new StringBuilder();
        try (java.util.Scanner scanner = new java.util.Scanner(
                new java.io.File(LOG_FILE))) {
            while (scanner.hasNextLine()) {
                log.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            e.printStackTrace();
        }
        return log.toString();
    }
}