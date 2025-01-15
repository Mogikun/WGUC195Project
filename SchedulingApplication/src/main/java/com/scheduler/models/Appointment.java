// Appointment.java
package com.scheduler.models;

import java.time.LocalDateTime;

/**
 * Represents an appointment in the scheduling system
 * @author Calvin Mogi
 *
 */
public class Appointment {
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;
    private int customerId;
    private int userId;
    private int contactId;

    /**
     * Constructs a new Appointment
     * @param appointmentId The appointment's ID
     * @param title The appointment's title
     * @param description The appointment's description
     * @param location The appointment's location
     * @param type The appointment's type
     * @param start The appointment's start time
     * @param end The appointment's end time
     * @param customerId The associated customer's ID
     * @param userId The associated user's ID
     * @param contactId The associated contact's ID
     */
    public Appointment(int appointmentId, String title, String description,
                       String location, String type, LocalDateTime start,
                       LocalDateTime end, int customerId, int userId, int contactId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getContactId() { return contactId; }
    public void setContactId(int contactId) { this.contactId = contactId; }
}