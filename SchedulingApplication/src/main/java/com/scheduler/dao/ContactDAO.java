package com.scheduler.dao;

import com.scheduler.models.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object for Contact-related database operations
 * @author Calvin Mogi
 */
public class ContactDAO {

    /**
     * Gets all contacts from the database
     * @return ObservableList of all contacts
     */
    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contacts";

        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Contact contact = new Contact(
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name"),
                        rs.getString("Email")
                );
                contacts.add(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Gets a contact by ID
     * @param contactId The ID of the contact to retrieve
     * @return Contact object if found, null if not found
     */
    public static Contact getContactById(int contactId) {
        String sql = "SELECT * FROM contacts WHERE Contact_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contactId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Contact(
                            rs.getInt("Contact_ID"),
                            rs.getString("Contact_Name"),
                            rs.getString("Email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a contact's schedule (all appointments for a contact)
     * @param contactId The ID of the contact to get schedule for
     * @return ObservableList of appointments for the contact
     */
    public static ObservableList<AppointmentSchedule> getContactSchedule(int contactId) {
        ObservableList<AppointmentSchedule> schedule = FXCollections.observableArrayList();
        String sql = "SELECT a.Appointment_ID, a.Title, a.Type, a.Description, " +
                "a.Start, a.End, a.Customer_ID " +
                "FROM appointments a " +
                "WHERE a.Contact_ID = ? " +
                "ORDER BY a.Start";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contactId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AppointmentSchedule appointment = new AppointmentSchedule(
                            rs.getInt("Appointment_ID"),
                            rs.getString("Title"),
                            rs.getString("Type"),
                            rs.getString("Description"),
                            rs.getTimestamp("Start").toLocalDateTime(),
                            rs.getTimestamp("End").toLocalDateTime(),
                            rs.getInt("Customer_ID")
                    );
                    schedule.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedule;
    }
}

