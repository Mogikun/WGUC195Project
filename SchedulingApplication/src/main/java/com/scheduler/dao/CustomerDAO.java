// CustomerDAO.java
package com.scheduler.dao;

import com.scheduler.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

/**
 * Data Access Object for Customer-related database operations
 * @author Calvin Mogi
 */
public class CustomerDAO {

    /**
     * Gets all customers from the database
     *
     * @return ObservableList of all customers
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String sql = "SELECT c.*, d.Division, co.Country FROM customers c " +
                "JOIN first_level_divisions d ON c.Division_ID = d.Division_ID " +
                "JOIN countries co ON d.Country_ID = co.Country_ID";

        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Address"),
                        rs.getString("Postal_Code"),
                        rs.getString("Phone"),
                        rs.getInt("Division_ID")
                );
                customer.setDivisionName(rs.getString("Division"));
                customer.setCountry(rs.getString("Country"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Adds a new customer to the database
     *
     * @param customer The customer to add
     * @return true if successful, false if failed
     */
    public static boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID, Create_Date, Created_By, Last_Update, Last_Updated_By) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getPostalCode());
            ps.setString(4, customer.getPhone());
            ps.setInt(5, customer.getDivisionId());
            ps.setString(6, "admin"); // Replace with actual user
            ps.setString(7, "admin"); // Replace with actual user

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing customer in the database
     *
     * @param customer The customer to update
     * @return true if successful, false if failed
     */
    public static boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, " +
                "Postal_Code = ?, Phone = ?, Division_ID = ?, " +
                "Last_Update = NOW(), Last_Updated_By = ? " +
                "WHERE Customer_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getPostalCode());
            ps.setString(4, customer.getPhone());
            ps.setInt(5, customer.getDivisionId());
            ps.setString(6, "admin"); // Replace with actual user
            ps.setInt(7, customer.getCustomerId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a customer from the database
     *
     * @param customerId The ID of the customer to delete
     * @return true if successful, false if failed
     */
    public static boolean deleteCustomer(int customerId) {
        // First delete all appointments for this customer
        String appointmentSql = "DELETE FROM appointments WHERE Customer_ID = ?";
        String customerSql = "DELETE FROM customers WHERE Customer_ID = ?";

        try (Connection conn = JDBC.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);

            try (PreparedStatement psAppointment = conn.prepareStatement(appointmentSql);
                 PreparedStatement psCustomer = conn.prepareStatement(customerSql)) {

                // Delete appointments first
                psAppointment.setInt(1, customerId);
                psAppointment.executeUpdate();

                // Then delete customer
                psCustomer.setInt(1, customerId);
                int rowsAffected = psCustomer.executeUpdate();

                conn.commit();
                return rowsAffected > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}