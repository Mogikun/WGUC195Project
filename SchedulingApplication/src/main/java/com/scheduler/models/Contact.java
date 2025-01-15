// Contact.java
package com.scheduler.models;

/**
 * Represents a contact in the scheduling system
 * @author Calvin Mogi
 */
public class Contact {
    private int contactId;
    private String contactName;
    private String email;

    /**
     * Constructs a new Contact
     * @param contactId The contact's ID
     * @param contactName The contact's name
     * @param email The contact's email
     */
    public Contact(int contactId, String contactName, String email) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.email = email;
    }

    // Getters and Setters
    public int getContactId() { return contactId; }
    public void setContactId(int contactId) { this.contactId = contactId; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}