package com.scheduler.dao;

import com.scheduler.models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.*;

public class AppointmentDAO {

    private static Appointment createAppointmentFromResultSet(ResultSet rs) throws SQLException {
        // Get UTC timestamps from database
        Timestamp startUTC = rs.getTimestamp("Start");
        Timestamp endUTC = rs.getTimestamp("End");

        // Convert UTC to local time
        ZonedDateTime startZDT = startUTC.toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime endZDT = endUTC.toInstant().atZone(ZoneId.systemDefault());

        // Convert to LocalDateTime for display
        LocalDateTime localStart = startZDT.toLocalDateTime();
        LocalDateTime localEnd = endZDT.toLocalDateTime();

        System.out.println("Converting appointment times:");
        System.out.println("UTC Start: " + startUTC);
        System.out.println("Local Start: " + localStart);
        System.out.println("UTC End: " + endUTC);
        System.out.println("Local End: " + localEnd);

        return new Appointment(
                rs.getInt("Appointment_ID"),
                rs.getString("Title"),
                rs.getString("Description"),
                rs.getString("Location"),
                rs.getString("Type"),
                localStart,  // Using local time
                localEnd,    // Using local time
                rs.getInt("Customer_ID"),
                rs.getInt("User_ID"),
                rs.getInt("Contact_ID")
        );
    }

    public static boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, " +
                "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, " +
                "Customer_ID, User_ID, Contact_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Convert local time to UTC for storage
            ZonedDateTime startZDT = appointment.getStart().atZone(ZoneId.systemDefault());
            ZonedDateTime endZDT = appointment.getEnd().atZone(ZoneId.systemDefault());

            Instant startUTC = startZDT.toInstant();
            Instant endUTC = endZDT.toInstant();

            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getType());
            ps.setTimestamp(5, Timestamp.from(startUTC));
            ps.setTimestamp(6, Timestamp.from(endUTC));
            ps.setString(7, "admin");
            ps.setString(8, "admin");
            ps.setInt(9, appointment.getCustomerId());
            ps.setInt(10, appointment.getUserId());
            ps.setInt(11, appointment.getContactId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error adding appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<Appointment> getWeekAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        // Get current week's start and end dates
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekEnd = weekStart.plusDays(7);

        String sql = "SELECT * FROM appointments WHERE Start >= ? AND Start < ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Convert local time to UTC for database query
            ZonedDateTime startZDT = weekStart.atZone(ZoneId.systemDefault());
            ZonedDateTime endZDT = weekEnd.atZone(ZoneId.systemDefault());

            Instant startUTC = startZDT.toInstant();
            Instant endUTC = endZDT.toInstant();

            ps.setTimestamp(1, Timestamp.from(startUTC));
            ps.setTimestamp(2, Timestamp.from(endUTC));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(createAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting week appointments: " + e.getMessage());
            e.printStackTrace();
        }

        return appointments;
    }

    public static ObservableList<Appointment> getMonthAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        // Get current month's start and end dates
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        String sql = "SELECT * FROM appointments WHERE Start >= ? AND Start < ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Convert local time to UTC for database query
            ZonedDateTime startZDT = monthStart.atZone(ZoneId.systemDefault());
            ZonedDateTime endZDT = monthEnd.atZone(ZoneId.systemDefault());

            Instant startUTC = startZDT.toInstant();
            Instant endUTC = endZDT.toInstant();

            ps.setTimestamp(1, Timestamp.from(startUTC));
            ps.setTimestamp(2, Timestamp.from(endUTC));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(createAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting month appointments: " + e.getMessage());
            e.printStackTrace();
        }

        return appointments;
    }
    public static boolean hasOverlappingAppointment(Integer excludeId, LocalDateTime start, LocalDateTime end, int customerId) {
        String sql = "SELECT * FROM appointments WHERE Customer_ID = ? AND " +
                "((Start BETWEEN ? AND ?) OR (End BETWEEN ? AND ?) OR (Start <= ? AND End >= ?))";

        if (excludeId != null) {
            sql += " AND Appointment_ID != ?";
        }

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Convert local times to UTC for database comparison
            ZonedDateTime startZDT = start.atZone(ZoneId.systemDefault());
            ZonedDateTime endZDT = end.atZone(ZoneId.systemDefault());

            Instant startUTC = startZDT.toInstant();
            Instant endUTC = endZDT.toInstant();

            ps.setInt(1, customerId);
            ps.setTimestamp(2, Timestamp.from(startUTC));
            ps.setTimestamp(3, Timestamp.from(endUTC));
            ps.setTimestamp(4, Timestamp.from(startUTC));
            ps.setTimestamp(5, Timestamp.from(endUTC));
            ps.setTimestamp(6, Timestamp.from(startUTC));
            ps.setTimestamp(7, Timestamp.from(endUTC));

            if (excludeId != null) {
                ps.setInt(8, excludeId);
            }

            System.out.println("Checking overlaps for customer " + customerId);
            System.out.println("Start time (UTC): " + startUTC);
            System.out.println("End time (UTC): " + endUTC);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Found an overlapping appointment
                Timestamp overlapStart = rs.getTimestamp("Start");
                Timestamp overlapEnd = rs.getTimestamp("End");
                System.out.println("Found overlapping appointment:");
                System.out.println("ID: " + rs.getInt("Appointment_ID"));
                System.out.println("Overlap Start: " + overlapStart);
                System.out.println("Overlap End: " + overlapEnd);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Error checking appointment overlap: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, " +
                "Type = ?, Start = ?, End = ?, Last_Update = NOW(), " +
                "Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Convert local times to UTC for storage
            ZonedDateTime startZDT = appointment.getStart().atZone(ZoneId.systemDefault());
            ZonedDateTime endZDT = appointment.getEnd().atZone(ZoneId.systemDefault());

            Instant startUTC = startZDT.toInstant();
            Instant endUTC = endZDT.toInstant();

            System.out.println("Updating appointment ID: " + appointment.getAppointmentId());
            System.out.println("Start time (UTC): " + startUTC);
            System.out.println("End time (UTC): " + endUTC);

            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getType());
            ps.setTimestamp(5, Timestamp.from(startUTC));
            ps.setTimestamp(6, Timestamp.from(endUTC));
            ps.setString(7, "admin"); // Last_Updated_By
            ps.setInt(8, appointment.getCustomerId());
            ps.setInt(9, appointment.getUserId());
            ps.setInt(10, appointment.getContactId());
            ps.setInt(11, appointment.getAppointmentId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}