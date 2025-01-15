package com.scheduler.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;
    @FXML private Label userLabel;
    @FXML private BorderPane mainBorderPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set the logged-in user's name
        userLabel.setText("User: " + LoginController.getCurrentUserId());
    }

    @FXML
    private void handleLogout() {
        // TODO: Implement logout functionality
    }

    @FXML
    private void handleCustomersTab() {
        // Customers tab selected
    }

    @FXML
    private void handleAppointmentsTab() {
        // Appointments tab selected
    }

    @FXML
    private void handleReportsTab() {
        // Reports tab selected
    }
}