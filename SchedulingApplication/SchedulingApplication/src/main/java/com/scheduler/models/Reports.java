package com.scheduler.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.scheduler.dao.JDBC;
import com.scheduler.models.AppointmentSchedule;

public class Reports {

    public static class AppointmentTypeCount {
        private final String type;
        private final String month;
        private final int total;

        public AppointmentTypeCount(String type, String month, int total) {
            this.type = type;
            this.month = month;
            this.total = total;
        }

        public String getType() { return type; }
        public String getMonth() { return month; }
        public int getTotal() { return total; }
    }

    public static ObservableList<AppointmentTypeCount> getAppointmentCounts() {
        ObservableList<AppointmentTypeCount> counts = FXCollections.observableArrayList();
        String sql = "SELECT Type, " +
                "DATE_FORMAT(Start, '%M') as Month, " +
                "COUNT(*) as Total, " +
                "MONTH(Start) as MonthNum " +
                "FROM appointments " +
                "GROUP BY Type, DATE_FORMAT(Start, '%M'), MONTH(Start) " +
                "ORDER BY MonthNum, Type";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                counts.add(new AppointmentTypeCount(
                        rs.getString("Type"),
                        rs.getString("Month"),
                        rs.getInt("Total")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error getting appointment counts: " + e.getMessage());
            e.printStackTrace();
        }
        return counts;
    }

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
            System.out.println("Getting schedule for contact ID: " + contactId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                schedule.add(new AppointmentSchedule(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Type"),
                        rs.getString("Description"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID")
                ));
            }

            System.out.println("Found " + schedule.size() + " appointments for contact");

        } catch (SQLException e) {
            System.out.println("Error getting contact schedule: " + e.getMessage());
            e.printStackTrace();
        }
        return schedule;
    }
}