package com.scheduler.controllers;

import com.scheduler.dao.CustomerDAO;
import com.scheduler.models.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.sql.*;
import javafx.scene.control.Alert;

public class CustomerController implements Initializable {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> postalCodeColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> divisionColumn;

    @FXML private TextField customerIdField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField postalCodeField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private ComboBox<String> divisionComboBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadCustomerData();
        setupComboBoxes();
    }
    /**
     * Sets up the country and division combo boxes
     */
    private void setupComboBoxes() {
        try {
            // Setup Countries ComboBox
            ObservableList<String> countries = FXCollections.observableArrayList(
                    "U.S", "UK", "Canada"
            );
            countryComboBox.setItems(countries);

            // Add listener to country selection to update divisions
            countryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateDivisionComboBox(newVal);
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading combo box data: " + e.getMessage());
        }
    }

    /**
     * Updates the division combo box based on selected country
     * @param country The selected country
     */
    private void updateDivisionComboBox(String country) {
        try {
            ObservableList<String> divisions = FXCollections.observableArrayList();

            switch(country) {
                case "U.S":
                    divisions.addAll(
                            "Alabama", "Alaska", "Arizona", "Arkansas", "California",
                            "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
                            "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                            "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
                            "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
                            "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
                            "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
                            "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
                            "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
                            "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
                    );
                    break;

                case "UK":
                    divisions.addAll(
                            "England", "Wales", "Scotland", "Northern Ireland"
                    );
                    break;

                case "Canada":
                    divisions.addAll(
                            "Alberta", "British Columbia", "Manitoba", "New Brunswick",
                            "Newfoundland and Labrador", "Northwest Territories", "Nova Scotia",
                            "Nunavut", "Ontario", "Prince Edward Island", "Quebec",
                            "Saskatchewan", "Yukon"
                    );
                    break;
            }

            divisionComboBox.setItems(divisions);
            divisionComboBox.setValue(null); // Clear previous selection

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating divisions: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
    }

    private void loadCustomerData() {
        customerTable.setItems(CustomerDAO.getAllCustomers());
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showCustomerDetails(newSelection);
                    }
                });
    }

    private void showCustomerDetails(Customer customer) {
        customerIdField.setText(String.valueOf(customer.getCustomerId()));
        nameField.setText(customer.getCustomerName());
        addressField.setText(customer.getAddress());
        postalCodeField.setText(customer.getPostalCode());
        phoneField.setText(customer.getPhone());
        // Set country and division in combo boxes
        countryComboBox.setValue(customer.getCountry());
        divisionComboBox.setValue(customer.getDivisionName());
    }

    @FXML
    private void handleAdd() {
        try {
            validateInput();
            Customer newCustomer = new Customer(
                    0, // ID will be auto-generated
                    nameField.getText(),
                    addressField.getText(),
                    postalCodeField.getText(),
                    phoneField.getText(),
                    getDivisionId(divisionComboBox.getValue())
            );

            if (CustomerDAO.addCustomer(newCustomer)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully");
                loadCustomerData();
                clearFields();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
    @FXML
    private void handleClear() {
        customerIdField.clear();
        nameField.clear();
        addressField.clear();
        postalCodeField.clear();
        phoneField.clear();
        countryComboBox.setValue(null);
        divisionComboBox.setValue(null);
        customerTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleUpdate() {
        try {
            validateInput();
            Customer customer = new Customer(
                    Integer.parseInt(customerIdField.getText()),
                    nameField.getText(),
                    addressField.getText(),
                    postalCodeField.getText(),
                    phoneField.getText(),
                    getDivisionId(divisionComboBox.getValue())
            );

            if (CustomerDAO.updateCustomer(customer)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully");
                loadCustomerData();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        if (customer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Delete customer " + customer.getCustomerName() + "? All associated appointments will also be deleted.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (CustomerDAO.deleteCustomer(customer.getCustomerId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer deleted successfully");
                loadCustomerData();
                clearFields();
            }
        }
    }

    private void validateInput() throws Exception {
        if (nameField.getText().isEmpty()) {
            throw new Exception("Name is required");
        }
        if (addressField.getText().isEmpty()) {
            throw new Exception("Address is required");
        }
        if (postalCodeField.getText().isEmpty()) {
            throw new Exception("Postal code is required");
        }
        if (phoneField.getText().isEmpty()) {
            throw new Exception("Phone number is required");
        }
        if (divisionComboBox.getValue() == null) {
            throw new Exception("Division must be selected");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        customerIdField.clear();
        nameField.clear();
        addressField.clear();
        postalCodeField.clear();
        phoneField.clear();
        countryComboBox.setValue(null);
        divisionComboBox.setValue(null);
    }

    // Helper method to get division ID from name
    private int getDivisionId(String divisionName) {
        // Implement this method to get division ID from your database
        return 1; // Placeholder
    }

}