package com.scheduler.controllers;

import com.scheduler.dao.ContactDAO;
import com.scheduler.models.Contact;
import com.scheduler.models.AppointmentSchedule;
import com.scheduler.models.Reports;
import com.scheduler.models.Reports.AppointmentTypeCount;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.util.StringConverter;

public class ReportController implements Initializable {
    @FXML private TableView<AppointmentTypeCount> typeMonthTable;
    @FXML private TableColumn<AppointmentTypeCount, String> typeColumn;
    @FXML private TableColumn<AppointmentTypeCount, String> monthColumn;
    @FXML private TableColumn<AppointmentTypeCount, Integer> totalColumn;

    @FXML private ComboBox<Contact> contactComboBox;
    @FXML private TableView<AppointmentSchedule> contactScheduleTable;
    @FXML private TableColumn<AppointmentSchedule, Integer> appointmentIdColumn;
    @FXML private TableColumn<AppointmentSchedule, String> titleColumn;
    @FXML private TableColumn<AppointmentSchedule, String> typeScheduleColumn;
    @FXML private TableColumn<AppointmentSchedule, String> descriptionColumn;
    @FXML private TableColumn<AppointmentSchedule, String> startDateTimeColumn;
    @FXML private TableColumn<AppointmentSchedule, String> endDateTimeColumn;
    @FXML private TableColumn<AppointmentSchedule, Integer> customerIdColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadContacts();
        refreshReports();

        contactComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadContactSchedule(newVal.getContactId());
            }
        });
    }

    private void setupColumns() {
        // Type/Month report columns
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Contact schedule columns
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeScheduleColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }

    private void loadContacts() {
        ObservableList<Contact> contacts = ContactDAO.getAllContacts();
        contactComboBox.setItems(contacts);

        // Set cell factory to display contact names
        contactComboBox.setCellFactory(lv -> new ListCell<Contact>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setText(null);
                } else {
                    setText(contact.getContactName());
                }
            }
        });

        // Set converter for selected value display
        contactComboBox.setConverter(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                return contact == null ? "" : contact.getContactName();
            }

            @Override
            public Contact fromString(String string) {
                return null;
            }
        });
    }

    private void loadContactSchedule(int contactId) {
        ObservableList<AppointmentSchedule> schedule = Reports.getContactSchedule(contactId);
        contactScheduleTable.setItems(schedule);
    }

    @FXML
    public void refreshReports() {
        System.out.println("Refreshing reports...");
        ObservableList<AppointmentTypeCount> typeMonthData = Reports.getAppointmentCounts();
        typeMonthTable.setItems(typeMonthData);
        System.out.println("Type/Month report updated with " + typeMonthData.size() + " entries");
    }
}