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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.util.StringConverter;


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

        // Format datetime columns to show local time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        startColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(formatter.format(time));
                }
            }
        });

        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        endColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(formatter.format(time));
                }
            }
        });

        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        contactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
    }

    private void setupTimeComboBoxes() {
        ObservableList<LocalTime> localTimes = FXCollections.observableArrayList();
        LocalTime localTime = LocalTime.of(0, 0);

        // Add all times in 30-min increments
        while (localTime.isBefore(LocalTime.of(23, 30))) {
            // Convert local time to EST to check if it's within business hours
            ZonedDateTime localZDT = ZonedDateTime.of(LocalDate.now(), localTime, ZoneId.systemDefault());
            ZonedDateTime estZDT = localZDT.withZoneSameInstant(EASTERN_ZONE);
            LocalTime estTime = estZDT.toLocalTime();

            // Only add times that fall within business hours in EST
            if (!estTime.isBefore(BUSINESS_START) && !estTime.isAfter(BUSINESS_END)) {
                localTimes.add(localTime);
            }
            localTime = localTime.plusMinutes(30);
        }

        startTimeComboBox.setItems(localTimes);
        endTimeComboBox.setItems(localTimes);

        // Add time formatter
        StringConverter<LocalTime> timeConverter = new StringConverter<>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

            @Override
            public String toString(LocalTime time) {
                if (time != null) {
                    return formatter.format(time);
                }
                return "";
            }

            @Override
            public LocalTime fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalTime.parse(string, formatter);
                }
                return null;
            }
        };

        startTimeComboBox.setConverter(timeConverter);
        endTimeComboBox.setConverter(timeConverter);
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

    public void loadAppointmentData() {
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
            System.out.println("Starting handleAdd method...");
            validateInput();

            // Get the selected customer ID from the combo box
            String customerSelection = customerComboBox.getValue();
            System.out.println("Selected customer: " + customerSelection);
            int customerId = Integer.parseInt(customerSelection.split("-")[0].trim());
            System.out.println("Parsed customer ID: " + customerId);

            // Get the selected contact ID
            String contactName = contactComboBox.getValue();
            System.out.println("Selected contact: " + contactName);
            int contactId = getContactId(contactName);
            System.out.println("Retrieved contact ID: " + contactId);

            LocalDateTime startDateTime = combineDateTime(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalDateTime endDateTime = combineDateTime(endDatePicker.getValue(), endTimeComboBox.getValue());
            System.out.println("Start time: " + startDateTime);
            System.out.println("End time: " + endDateTime);

            if (!isWithinBusinessHours(startDateTime, endDateTime)) {
                throw new Exception("Appointment must be within business hours (8:00 AM - 10:00 PM EST)");
            }

            // Check for overlaps with same customer's appointments
            if (AppointmentDAO.hasOverlappingAppointment(null, startDateTime, endDateTime, customerId)) {
                throw new Exception("This customer already has an appointment scheduled during this time");
            }

            System.out.println("Creating new appointment object...");
            Appointment newAppointment = new Appointment(
                    0, // ID will be auto-generated
                    titleField.getText(),
                    descriptionField.getText(),
                    locationField.getText(),
                    typeField.getText(),
                    startDateTime,
                    endDateTime,
                    customerId,
                    LoginController.getCurrentUserId(),
                    contactId
            );

            System.out.println("Calling AppointmentDAO.addAppointment...");
            boolean success = AppointmentDAO.addAppointment(newAppointment);
            System.out.println("Add appointment result: " + success);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment added successfully");
                loadAppointmentData();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add appointment");
            }
        } catch (Exception e) {
            System.out.println("Error in handleAdd: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Loading contacts into combo box...");

            // Setup cell factory to display contact names
            contactComboBox.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                }
            });

            // Setup the string converter for the combo box
            contactComboBox.setConverter(new StringConverter<String>() {
                @Override
                public String toString(String contact) {
                    return contact;
                }

                @Override
                public String fromString(String string) {
                    return string;
                }
            });

            // Convert contacts to string list and populate combo box
            ObservableList<String> contactNames = FXCollections.observableArrayList();
            for (Contact contact : contacts) {
                System.out.println("Adding contact to combo box: " + contact.getContactName());
                contactNames.add(contact.getContactName());
            }
            contactComboBox.setItems(contactNames);

            System.out.println("Contacts loaded: " + contactNames.size());

            // Setup Customers ComboBox
            refreshCustomerComboBox();

        } catch (Exception e) {
            System.out.println("Error in setupComboBoxes: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading combo box data: " + e.getMessage());
        }
    }


    public void refreshCustomerComboBox() {
        try {
            System.out.println("Refreshing customer combo box...");
            ObservableList<Customer> customers = CustomerDAO.getAllCustomers();
            ObservableList<String> customerStrings = FXCollections.observableArrayList();

            for (Customer customer : customers) {
                String customerString = customer.getCustomerId() + " - " + customer.getCustomerName();
                System.out.println("Adding customer: " + customerString);
                customerStrings.add(customerString);
            }

            String currentSelection = customerComboBox.getValue();
            customerComboBox.setItems(customerStrings);

            if (customerStrings.contains(currentSelection)) {
                customerComboBox.setValue(currentSelection);
            }

            System.out.println("Customer combo box updated with " + customerStrings.size() + " items");
        } catch (Exception e) {
            System.out.println("Error refreshing customer combo box: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isWithinBusinessHours(LocalDateTime localStart, LocalDateTime localEnd) {
        // Convert local time to Eastern Time for business hours check
        ZonedDateTime startEst = localStart.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(EASTERN_ZONE);
        ZonedDateTime endEst = localEnd.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(EASTERN_ZONE);

        LocalTime startTimeEst = startEst.toLocalTime();
        LocalTime endTimeEst = endEst.toLocalTime();
        DayOfWeek dayEst = startEst.getDayOfWeek();

        System.out.println("\nBusiness Hours Check:");
        System.out.println("Local Start: " + localStart);
        System.out.println("Local End: " + localEnd);
        System.out.println("EST Start: " + startTimeEst);
        System.out.println("EST End: " + endTimeEst);
        System.out.println("EST Day: " + dayEst);

        // Check if it's a weekday
        if (dayEst.getValue() > 5) {
            System.out.println("Outside business days - weekend detected");
            return false;
        }

        // Check if within business hours (8 AM - 10 PM EST)
        if (startTimeEst.isBefore(BUSINESS_START) ||
                endTimeEst.isAfter(BUSINESS_END)) {
            System.out.println("Outside business hours");
            return false;
        }

        System.out.println("Within business hours");
        return true;
    }


    private boolean hasOverlap(Integer excludeId, LocalDateTime start, LocalDateTime end) {
        String customerSelection = customerComboBox.getValue();
        if (customerSelection == null) {
            return false;
        }
        int customerId = Integer.parseInt(customerSelection.split("-")[0].trim());
        return AppointmentDAO.hasOverlappingAppointment(excludeId, start, end, customerId);
    }

    @FXML
    private void handleUpdate() {
        try {
            // Check if appointment is selected
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select an appointment to update");
                return;
            }

            System.out.println("Updating appointment ID: " + selectedAppointment.getAppointmentId());

            // Validate input
            validateInput();

            LocalDateTime startDateTime = combineDateTime(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalDateTime endDateTime = combineDateTime(endDatePicker.getValue(), endTimeComboBox.getValue());

            if (!isWithinBusinessHours(startDateTime, endDateTime)) {
                throw new Exception("Appointment must be within business hours (8:00 AM - 10:00 PM EST)");
            }

            String customerSelection = customerComboBox.getValue();
            if (customerSelection == null || customerSelection.trim().isEmpty()) {
                throw new Exception("Please select a customer");
            }
            int customerId = Integer.parseInt(customerSelection.split("-")[0].trim());

            // Check for overlapping appointments, excluding current appointment
            if (AppointmentDAO.hasOverlappingAppointment(selectedAppointment.getAppointmentId(),
                    startDateTime, endDateTime, customerId)) {
                throw new Exception("This appointment overlaps with an existing appointment");
            }

            String contactName = contactComboBox.getValue();
            if (contactName == null || contactName.trim().isEmpty()) {
                throw new Exception("Please select a contact");
            }
            int contactId = getContactId(contactName);

            Appointment updatedAppointment = new Appointment(
                    selectedAppointment.getAppointmentId(),
                    titleField.getText(),
                    descriptionField.getText(),
                    locationField.getText(),
                    typeField.getText(),
                    startDateTime,
                    endDateTime,
                    customerId,
                    LoginController.getCurrentUserId(),
                    contactId
            );

            System.out.println("Attempting to update appointment in database...");
            if (AppointmentDAO.updateAppointment(updatedAppointment)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment updated successfully");
                loadAppointmentData();
                clearFields();
            } else {
                throw new Exception("Failed to update appointment");
            }
        } catch (Exception e) {
            System.out.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void showCustomerDetails(Appointment appointment) {
        if (appointment != null) {
            System.out.println("Loading appointment details for ID: " + appointment.getAppointmentId());

            appointmentIdField.setText(String.valueOf(appointment.getAppointmentId()));
            titleField.setText(appointment.getTitle());
            descriptionField.setText(appointment.getDescription());
            locationField.setText(appointment.getLocation());
            typeField.setText(appointment.getType());
            startDatePicker.setValue(appointment.getStart().toLocalDate());
            startTimeComboBox.setValue(appointment.getStart().toLocalTime());
            endDatePicker.setValue(appointment.getEnd().toLocalDate());
            endTimeComboBox.setValue(appointment.getEnd().toLocalTime());

            // Find and select the correct customer in combo box
            for (String customerOption : customerComboBox.getItems()) {
                if (customerOption.startsWith(appointment.getCustomerId() + " -")) {
                    customerComboBox.setValue(customerOption);
                    break;
                }
            }

            // Find and select the correct contact
            try {
                Contact contact = ContactDAO.getContactById(appointment.getContactId());
                if (contact != null) {
                    contactComboBox.setValue(contact.getContactName());
                }
            } catch (Exception e) {
                System.out.println("Error loading contact details: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleTableSelection() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            showCustomerDetails(selectedAppointment);
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
        if (date == null || time == null) {
            throw new IllegalArgumentException("Date and time must be provided");
        }
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
        try {
            ObservableList<Contact> contacts = ContactDAO.getAllContacts();
            for (Contact contact : contacts) {
                if (contact.getContactName().equals(contactName)) {
                    return contact.getContactId();
                }
            }
            throw new IllegalArgumentException("Contact not found: " + contactName);
        } catch (Exception e) {
            System.out.println("Error getting contact ID: " + e.getMessage());
            throw new IllegalArgumentException("Error getting contact ID", e);
        }
    }


    public void refreshAll() {
        refreshCustomerComboBox();
        loadAppointmentData();
    }
    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing appointment view...");
        refreshCustomerComboBox();
        loadAppointmentData();
        System.out.println("Refresh complete");
    }
}
