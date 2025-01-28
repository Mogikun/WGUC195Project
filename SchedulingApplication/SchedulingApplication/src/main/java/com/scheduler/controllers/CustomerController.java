package com.scheduler.controllers;

import com.scheduler.dao.CustomerDAO;
import com.scheduler.dao.JDBC;
import com.scheduler.models.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;

public class CustomerController implements Initializable {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> postalCodeColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> divisionColumn;
    @FXML private TableColumn<Customer, String> countryColumn; // Add this line

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

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country")); // Add this line
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

    private void updateDivisionComboBox(String country) {
        // First, let's check what's in the countries table
        System.out.println("\nChecking countries table:");
        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT Country_ID, Country FROM countries");
            while (rs.next()) {
                System.out.println("Country ID: " + rs.getInt("Country_ID") +
                        ", Country: " + rs.getString("Country"));
            }
        } catch (SQLException e) {
            System.out.println("Error checking countries: " + e.getMessage());
            e.printStackTrace();
        }

        // Now check first_level_divisions table
        System.out.println("\nChecking first_level_divisions table:");
        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT Division_ID, Division, Country_ID FROM first_level_divisions");
            while (rs.next()) {
                System.out.println("Division ID: " + rs.getInt("Division_ID") +
                        ", Division: " + rs.getString("Division") +
                        ", Country ID: " + rs.getInt("Country_ID"));
            }
        } catch (SQLException e) {
            System.out.println("Error checking divisions: " + e.getMessage());
            e.printStackTrace();
        }

        // Map the UI country names to database country names
        String dbCountry = switch (country) {
            case "U.S" -> "United States";
            case "UK" -> "United Kingdom";
            default -> country;
        };

        System.out.println("\nAttempting to find divisions for country: " + dbCountry);

        String sql = "SELECT fld.Division_ID, fld.Division, c.Country " +
                "FROM first_level_divisions fld " +
                "JOIN countries c ON fld.Country_ID = c.Country_ID " +
                "WHERE c.Country = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dbCountry);
            System.out.println("Executing query: " + sql);
            System.out.println("With parameter: " + dbCountry);

            ResultSet rs = ps.executeQuery();

            ObservableList<String> divisions = FXCollections.observableArrayList();
            while (rs.next()) {
                String division = rs.getString("Division");
                int divisionId = rs.getInt("Division_ID");
                String resultCountry = rs.getString("Country");
                System.out.println("Found: Division ID=" + divisionId +
                        ", Division=" + division +
                        ", Country=" + resultCountry);
                divisions.add(division);
            }

            divisionComboBox.setItems(divisions);
            divisionComboBox.setValue(null);

            if (divisions.isEmpty()) {
                System.out.println("No divisions found for country: " + dbCountry);
            }

        } catch (SQLException e) {
            System.out.println("Error in division query: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading divisions: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            validateInput();
            String divisionName = divisionComboBox.getValue();
            int divisionId = getDivisionId(divisionName);

            Customer newCustomer = new Customer(
                    0, // ID will be auto-generated
                    nameField.getText(),
                    addressField.getText(),
                    postalCodeField.getText(),
                    phoneField.getText(),
                    divisionId
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
    private void handleUpdate() {
        try {
            validateInput();
            String divisionName = divisionComboBox.getValue();
            int divisionId = getDivisionId(divisionName);

            Customer customer = new Customer(
                    Integer.parseInt(customerIdField.getText()),
                    nameField.getText(),
                    addressField.getText(),
                    postalCodeField.getText(),
                    phoneField.getText(),
                    divisionId
            );

            if (CustomerDAO.updateCustomer(customer)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully");
                loadCustomerData();
                clearFields();
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
        if (nameField.getText().isEmpty()) throw new Exception("Name is required");
        if (addressField.getText().isEmpty()) throw new Exception("Address is required");
        if (postalCodeField.getText().isEmpty()) throw new Exception("Postal code is required");
        if (phoneField.getText().isEmpty()) throw new Exception("Phone number is required");
        if (divisionComboBox.getValue() == null) throw new Exception("Division is required");
    }

    private int getDivisionId(String divisionName) {
        String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, divisionName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("Division_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid division: " + divisionName);
    }

    private void showCustomerDetails(Customer customer) {
        customerIdField.setText(String.valueOf(customer.getCustomerId()));
        nameField.setText(customer.getCustomerName());
        addressField.setText(customer.getAddress());
        postalCodeField.setText(customer.getPostalCode());
        phoneField.setText(customer.getPhone());
        countryComboBox.setValue(customer.getCountry());
        divisionComboBox.setValue(customer.getDivisionName());
    }

    @FXML
    private void handleClear() {
        clearFields();
        customerTable.getSelectionModel().clearSelection();
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}