package com.scheduler.models;

import java.time.LocalDateTime;

/**
 * Model class to store appointment schedule information
 */
public class AppointmentSchedule {
    private int appointmentId;
    private String title;
    private String type;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerId;

    public AppointmentSchedule(int appointmentId, String title, String type,
                               String description, LocalDateTime start,
                               LocalDateTime end, int customerId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
    }

    // Getters
    public int getAppointmentId() {
        return appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public int getCustomerId() {
        return customerId;
    }

    // Setters
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}