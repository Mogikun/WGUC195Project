package com.scheduler.dao;

import com.scheduler.models.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ContactDAO {
    public static Contact getContactById(int contactId) {
        String sql = "SELECT * FROM contacts WHERE Contact_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contactId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Contact(
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name"),
                        rs.getString("Email")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting contact by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

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
            System.out.println("Error getting all contacts: " + e.getMessage());
            e.printStackTrace();
        }
        return contacts;
    }
}