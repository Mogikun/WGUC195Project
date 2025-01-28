package com.scheduler;

import com.scheduler.dao.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class SchedulingApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Initialize database connection
            JDBC.getConnection();

            // Load resource bundle
            ResourceBundle bundle = ResourceBundle.getBundle("lang.messages", Locale.getDefault());

            // Load FXML with resource bundle
            FXMLLoader fxmlLoader = new FXMLLoader(
                    SchedulingApplication.class.getResource("/views/login.fxml"),
                    bundle
            );

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Scheduling System");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}