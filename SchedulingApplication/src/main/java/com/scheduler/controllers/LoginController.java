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
        // Load language bundle based on system locale
        rb = ResourceBundle.getBundle("lang.messages", Locale.getDefault());

        // Set UI text based on language
        headerLabel.setText(rb.getString("login.header"));
        usernameField.setPromptText(rb.getString("login.username"));
        passwordField.setPromptText(rb.getString("login.password"));
        loginButton.setText(rb.getString("login.button"));

        // Display zone
        zoneLabel.setText(rb.getString("login.zone") + ZoneId.systemDefault());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            String sql = "SELECT User_ID FROM users WHERE User_Name = ? AND Password = ?";
            Connection conn = JDBC.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            boolean loginSuccess = rs.next();
            logLoginAttempt(username, loginSuccess);

            if (loginSuccess) {
                currentUserId = rs.getInt("User_ID");
                checkForUpcomingAppointments();
                loadMainScreen();
            } else {
                messageLabel.setText(rb.getString("login.error"));
            }
        } catch (Exception e) {
            messageLabel.setText("Database error: " + e.getMessage());
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
                String formattedTime = appointmentTime.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("alert.upcomingAppointment"));
                alert.setHeaderText(null);
                alert.setContentText(String.format(
                        rb.getString("alert.appointmentDetails"),
                        rs.getInt("Appointment_ID"),
                        appointmentTime.toLocalDate(),
                        appointmentTime.toLocalTime()));
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Upcoming Appointments");
                alert.setHeaderText(null);
                alert.setContentText(rb.getString("alert.noUpcomingAppointment"));
                alert.showAndWait();
            }
        } catch (SQLException e) {
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

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/main.fxml"),
                    rb
            );
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(rb.getString("login.title"));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("Error loading main screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }
}