// User.java
package com.scheduler.models;

/**
 * Represents a user in the scheduling system
 * @author Calvin Mogi
 */
public class User {
    private int userId;
    private String userName;
    private String password;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;

    /**
     * Constructs a new User
     * @param userId The user's ID
     * @param userName The user's username
     * @param password The user's password
     */
    public User(int userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}







