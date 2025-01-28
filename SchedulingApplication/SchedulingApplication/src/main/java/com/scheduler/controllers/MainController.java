package com.scheduler.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Node;

public class MainController implements Initializable {
    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;
    @FXML private Label userLabel;
    @FXML private BorderPane mainBorderPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userLabel.setText("User: " + LoginController.getCurrentUserId());

        // Add listener for tab selection changes
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                handleTabChange(newTab);
            }
        });
    }

    private void handleTabChange(Tab tab) {
        if (tab.getText().equals("Appointments")) {
            AppointmentController controller = findController(tab);
            if (controller != null) {
                controller.refreshAll();
            }
        }
    }

    private AppointmentController findController(Tab tab) {
        try {
            Node content = tab.getContent();
            if (content != null) {
                Node appointmentController = content.lookup("#appointmentController");
                if (appointmentController != null) {
                    return (AppointmentController) appointmentController.getUserData();
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding appointment controller: " + e.getMessage());
        }
        return null;
    }

    private void refreshAppointmentsTab(Tab appointmentsTab) {
        try {
            // Find the AppointmentController through the tab's content
            Node content = appointmentsTab.getContent();
            if (content != null) {
                AppointmentController controller = (AppointmentController) content.lookup("#appointmentController").getUserData();
                if (controller != null) {
                    System.out.println("Refreshing appointments view");
                    controller.refreshAll();
                }
            }
        } catch (Exception e) {
            System.out.println("Error refreshing appointments tab: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // TODO: Implement logout functionality
    }
}