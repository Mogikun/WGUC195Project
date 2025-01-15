package com.scheduler.controllers;

import com.scheduler.dao.AppointmentDAO;
import com.scheduler.dao.CustomerDAO;
import com.scheduler.dao.ContactDAO;
import com.scheduler.models.Appointment;
import com.scheduler.models.Contact;
import com.scheduler.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> descriptionColumn;
    @FXML private TableColumn<Appointment, String> locationColumn;
    @FXML private TableColumn<Appointment, String> typeColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML private TableColumn<Appointment, Integer> customerIdColumn;
    @FXML private TableColumn<Appointment, Integer> contactIdColumn;

    @FXML private TextField appointmentIdField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField typeField;
    @FXML private ComboBox<String> contactComboBox;
    @FXML private ComboBox<String> customerComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<LocalTime> startTimeComboBox;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<LocalTime> endTimeComboBox;
    @FXML private RadioButton weekViewRadio;
    @FXML private RadioButton monthViewRadio;

    private static final ZoneId LOCAL_ZONE = ZoneId.systemDefault();
    private static final ZoneId EASTERN_ZONE = ZoneId.of("America/New_York");
    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(22, 0);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupComboBoxes();
        setupTimeComboBoxes();
        setupRadioButtons();
        loadAppointmentData();
    }

    private void setupTableColumns() {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        contactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
    }

    private void setupTimeComboBoxes() {
        ObservableList<LocalTime> times = FXCollections.observableArrayList();
        LocalTime time = LocalTime.of(0, 0);
        while (!time.plusMinutes(30).isBefore(LocalTime.of(0, 0))) {
            times.add(time);
            time = time.plusMinutes(30);
        }
        startTimeComboBox.setItems(times);
        endTimeComboBox.setItems(times);
    }

    private void setupRadioButtons() {
        ToggleGroup viewToggle = new ToggleGroup();
        weekViewRadio.setToggleGroup(viewToggle);
        monthViewRadio.setToggleGroup(viewToggle);
        weekViewRadio.setSelected(true);

        viewToggle.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadAppointmentData();
            }
        });
    }

    private void loadAppointmentData() {
        ObservableList<Appointment> appointments;
        if (weekViewRadio.isSelected()) {
            appointments = AppointmentDAO.getWeekAppointments();
        } else {
            appointments = AppointmentDAO.getMonthAppointments();
        }
        appointmentTable.setItems(appointments);
    }
    @FXML
    private void handleClear() {
        appointmentIdField.clear();
        titleField.clear();
        descriptionField.clear();
        locationField.clear();
        typeField.clear();
        startDatePicker.setValue(null);
        startTimeComboBox.setValue(null);
        endDatePicker.setValue(null);
        endTimeComboBox.setValue(null);
        contactComboBox.setValue(null);
        customerComboBox.setValue(null);
        appointmentTable.getSelectionModel().clearSelection();
    }
    @FXML
    private void handleAdd() {
        try {
            validateInput();
            LocalDateTime startDateTime = combineDateTime(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalDateTime endDateTime = combineDateTime(endDatePicker.getValue(), endTimeComboBox.getValue());

            if (!isWithinBusinessHours(startDateTime, endDateTime)) {
                throw new Exception("Appointment must be within business hours (8:00 AM - 10:00 PM EST)");
            }

            if (hasOverlap(null, startDateTime, endDateTime)) {
                throw new Exception("This appointment overlaps with an existing appointment");
            }

            Appointment newAppointment = new Appointment(
                    0, // ID will be auto-generated
                    titleField.getText(),
                    descriptionField.getText(),
                    locationField.getText(),
                    typeField.getText(),
                    startDateTime,
                    endDateTime,
                    Integer.parseInt(customerComboBox.getValue().split("-")[0]),
                    LoginController.getCurrentUserId(),
                    getContactId(contactComboBox.getValue())
            );

            if (AppointmentDAO.addAppointment(newAppointment)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment added successfully");
                loadAppointmentData();
                clearFields();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
    /**
     * Sets up the contact and customer combo boxes
     */
    private void setupComboBoxes() {
        try {
            // Setup Contacts ComboBox
            ObservableList<Contact> contacts = ContactDAO.getAllContacts();
            ObservableList<String> contactNames = FXCollections.observableArrayList();
            for (Contact contact : contacts) {
                contactNames.add(contact.getContactName());
            }
            contactComboBox.setItems(contactNames);

            // Setup Customers ComboBox
            ObservableList<Customer> customers = CustomerDAO.getAllCustomers();
            ObservableList<String> customerNames = FXCollections.observableArrayList();
            for (Customer customer : customers) {
                customerNames.add(customer.getCustomerId() + " - " + customer.getCustomerName());
            }
            customerComboBox.setItems(customerNames);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading combo box data: " + e.getMessage());
        }
    }

    private boolean isWithinBusinessHours(LocalDateTime start, LocalDateTime end) {
        ZonedDateTime startEastern = start.atZone(LOCAL_ZONE).withZoneSameInstant(EASTERN_ZONE);
        ZonedDateTime endEastern = end.atZone(LOCAL_ZONE).withZoneSameInstant(EASTERN_ZONE);

        return !startEastern.toLocalTime().isBefore(BUSINESS_START) &&
                !endEastern.toLocalTime().isAfter(BUSINESS_END) &&
                startEastern.toLocalDate().getDayOfWeek().getValue() <= 5 && // Monday to Friday
                endEastern.toLocalDate().getDayOfWeek().getValue() <= 5;
    }

    private boolean hasOverlap(Integer excludeId, LocalDateTime start, LocalDateTime end) {
        return AppointmentDAO.hasOverlappingAppointment(excludeId, start, end);
    }

    @FXML
    private void handleUpdate() {
        try {
            validateInput();
            LocalDateTime startDateTime = combineDateTime(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalDateTime endDateTime = combineDateTime(endDatePicker.getValue(), endTimeComboBox.getValue());

            if (!isWithinBusinessHours(startDateTime, endDateTime)) {
                throw new Exception("Appointment must be within business hours (8:00 AM - 10:00 PM EST)");
            }

            int appointmentId = Integer.parseInt(appointmentIdField.getText());
            if (hasOverlap(appointmentId, startDateTime, endDateTime)) {
                throw new Exception("This appointment overlaps with an existing appointment");
            }

            Appointment appointment = new Appointment(
                    appointmentId,
                    titleField.getText(),
                    descriptionField.getText(),
                    locationField.getText(),
                    typeField.getText(),
                    startDateTime,
                    endDateTime,
                    Integer.parseInt(customerComboBox.getValue().split("-")[0]),
                    LoginController.getCurrentUserId(),
                    getContactId(contactComboBox.getValue())
            );

            if (AppointmentDAO.updateAppointment(appointment)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment updated successfully");
                loadAppointmentData();
                clearFields();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Appointment appointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an appointment to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Delete appointment ID: " + appointment.getAppointmentId() +
                ", Type: " + appointment.getType() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (AppointmentDAO.deleteAppointment(appointment.getAppointmentId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment deleted successfully");
                loadAppointmentData();
                clearFields();
            }
        }
    }

    private void validateInput() throws Exception {
        if (titleField.getText().isEmpty()) throw new Exception("Title is required");
        if (descriptionField.getText().isEmpty()) throw new Exception("Description is required");
        if (locationField.getText().isEmpty()) throw new Exception("Location is required");
        if (typeField.getText().isEmpty()) throw new Exception("Type is required");
        if (startDatePicker.getValue() == null) throw new Exception("Start date is required");
        if (startTimeComboBox.getValue() == null) throw new Exception("Start time is required");
        if (endDatePicker.getValue() == null) throw new Exception("End date is required");
        if (endTimeComboBox.getValue() == null) throw new Exception("End time is required");
        if (contactComboBox.getValue() == null) throw new Exception("Contact is required");
        if (customerComboBox.getValue() == null) throw new Exception("Customer is required");

        LocalDateTime start = combineDateTime(startDatePicker.getValue(), startTimeComboBox.getValue());
        LocalDateTime end = combineDateTime(endDatePicker.getValue(), endTimeComboBox.getValue());
        if (end.isBefore(start) || end.equals(start)) {
            throw new Exception("End time must be after start time");
        }
    }

    private LocalDateTime combineDateTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        appointmentIdField.clear();
        titleField.clear();
        descriptionField.clear();
        locationField.clear();
        typeField.clear();
        startDatePicker.setValue(null);
        startTimeComboBox.setValue(null);
        endDatePicker.setValue(null);
        endTimeComboBox.setValue(null);
        contactComboBox.setValue(null);
        customerComboBox.setValue(null);
    }

    private int getContactId(String contactName) {
        // Implement this method to get contact ID from the database
        return 1; // Placeholder
    }
}