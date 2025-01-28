package com.scheduler.controllers;

import com.scheduler.dao.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.time.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.time.format.DateTimeFormatter;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private Label zoneLabel;
    @FXML private Label headerLabel;

    private ResourceBundle rb;
    private static int currentUserId;

    @FXML
    public void initialize() {
        rb = ResourceBundle.getBundle("lang.messages", Locale.getDefault());
        headerLabel.setText(rb.getString("login.header"));
        usernameField.setPromptText(rb.getString("login.username"));
        passwordField.setPromptText(rb.getString("login.password"));
        loginButton.setText(rb.getString("login.button"));
        zoneLabel.setText(rb.getString("login.zone") + ZoneId.systemDefault());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBC.getConnection();
            String sql = "SELECT User_ID FROM users WHERE User_Name = ? AND Password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            boolean loginSuccess = rs.next();
            logLoginAttempt(username, loginSuccess);

            if (loginSuccess) {
                currentUserId = rs.getInt("User_ID");
                checkForUpcomingAppointments();
                loadMainScreen();
            } else {
                messageLabel.setText(rb.getString("login.error"));
            }
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/main.fxml"),
                    rb
            );
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(rb.getString("main.title"));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("Error loading main screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkForUpcomingAppointments() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fifteenMinutesFromNow = now.plusMinutes(15);

            String sql = "SELECT * FROM appointments WHERE User_ID = ? AND " +
                    "Start BETWEEN ? AND ? ORDER BY Start LIMIT 1";

            Connection conn = JDBC.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setTimestamp(3, Timestamp.valueOf(fifteenMinutesFromNow));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LocalDateTime appointmentTime = rs.getTimestamp("Start").toLocalDateTime();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming Appointment");
                alert.setHeaderText(null);
                alert.setContentText(String.format(
                        "You have an upcoming appointment:\nAppointment ID: %d\nDate: %s\nTime: %s",
                        rs.getInt("Appointment_ID"),
                        appointmentTime.toLocalDate(),
                        appointmentTime.toLocalTime()));
                alert.showAndWait();
            }
            // Removed the else block that was showing "No upcoming appointments" message

        } catch (SQLException e) {
            System.out.println("Error checking upcoming appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void logLoginAttempt(String username, boolean success) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("login_activity.txt", true))) {
            LocalDateTime now = LocalDateTime.now();
            String status = success ? "SUCCESSFUL" : "FAILED";
            writer.println(now + " - Login attempt by " + username + " was " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }
}