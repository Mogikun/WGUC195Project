// AppointmentDAO.java
package com.scheduler.dao;

import com.scheduler.models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
/**
 * Data Access Object for Appointment-related database operations
 * @author Calvin Mogi
 */
public class AppointmentDAO {

    /**
     * Gets all appointments from the database
     *
     * @return ObservableList of all appointments
     */
    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments";

        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                appointments.add(createAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Helper method to create Appointment object from ResultSet
     */
    private static Appointment createAppointmentFromResultSet(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getInt("Appointment_ID"),
                rs.getString("Title"),
                rs.getString("Description"),
                rs.getString("Location"),
                rs.getString("Type"),
                rs.getTimestamp("Start").toLocalDateTime(),
                rs.getTimestamp("End").toLocalDateTime(),
                rs.getInt("Customer_ID"),
                rs.getInt("User_ID"),
                rs.getInt("Contact_ID")
        );
    }

    /**
     * Adds a new appointment to the database
     *
     * @param appointment The appointment to add
     * @return true if successful, false if failed
     */
    public static boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, " +
                "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, " +
                "Customer_ID, User_ID, Contact_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getType());
            ps.setTimestamp(5, Timestamp.valueOf(appointment.getStart()));
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getEnd()));
            ps.setString(7, "admin"); // Replace with actual user
            ps.setString(8, "admin"); // Replace with actual user
            ps.setInt(9, appointment.getCustomerId());
            ps.setInt(10, appointment.getUserId());
            ps.setInt(11, appointment.getContactId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing appointment in the database
     *
     * @param appointment The appointment to update
     * @return true if successful, false if failed
     */
    public static boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, " +
                "Type = ?, Start = ?, End = ?, Last_Update = NOW(), " +
                "Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getType());
            ps.setTimestamp(5, Timestamp.valueOf(appointment.getStart()));
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getEnd()));
            ps.setString(7, "admin"); // Replace with actual user
            ps.setInt(8, appointment.getCustomerId());
            ps.setInt(9, appointment.getUserId());
            ps.setInt(10, appointment.getContactId());
            ps.setInt(11, appointment.getAppointmentId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an appointment from the database
     *
     * @param appointmentId The ID of the appointment to delete
     * @return true if successful, false if failed
     */
    public static boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets appointments for a specific customer
     *
     * @param customerId The customer ID to get appointments for
     * @return ObservableList of appointments for the customer
     */
    public static ObservableList<Appointment> getCustomerAppointments(int customerId) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE Customer_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    /**
     * Gets all appointments for the current week
     * @return ObservableList of appointments
     */
    public static ObservableList<Appointment> getWeekAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        LocalDateTime weekStart = LocalDateTime.now().with(DayOfWeek.MONDAY);
        LocalDateTime weekEnd = weekStart.plusWeeks(1);

        String sql = "SELECT * FROM appointments WHERE Start >= ? AND Start < ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(weekStart));
            ps.setTimestamp(2, Timestamp.valueOf(weekEnd));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getInt("User_ID"),
                        rs.getInt("Contact_ID")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    /**
     * Gets all appointments for the current month
     * @return ObservableList of appointments
     */
    public static ObservableList<Appointment> getMonthAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        String sql = "SELECT * FROM appointments WHERE Start >= ? AND Start < ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(monthStart));
            ps.setTimestamp(2, Timestamp.valueOf(monthEnd));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getInt("User_ID"),
                        rs.getInt("Contact_ID")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    /**
     * Checks if there are any overlapping appointments
     * @param excludeId appointment ID to exclude from check (for updates)
     * @param start start time to check
     * @param end end time to check
     * @return true if there is an overlap, false otherwise
     */
    public static boolean hasOverlappingAppointment(Integer excludeId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE " +
                "((Start BETWEEN ? AND ?) OR (End BETWEEN ? AND ?) OR (Start <= ? AND End >= ?))";

        if (excludeId != null) {
            sql += " AND Appointment_ID != ?";
        }

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));
            ps.setTimestamp(4, Timestamp.valueOf(end));
            ps.setTimestamp(5, Timestamp.valueOf(start));
            ps.setTimestamp(6, Timestamp.valueOf(end));

            if (excludeId != null) {
                ps.setInt(7, excludeId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}