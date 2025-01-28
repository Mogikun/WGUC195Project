// UserDAO.java
package com.scheduler.dao;

import com.scheduler.models.User;
import java.sql.*;

/**
 * Data Access Object for User-related database operations
 * @author Calvin Mogi
 */
public class UserDAO {

    /**
     * Validates user login credentials
     * @param username The username to validate
     * @param password The password to validate
     * @return User object if valid, null if invalid
     */
    public static User validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("User_ID"),
                            rs.getString("User_Name"),
                            rs.getString("Password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets user by ID
     * @param userId The ID of the user to retrieve
     * @return User object if found, null if not found
     */
    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE User_ID = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("User_ID"),
                            rs.getString("User_Name"),
                            rs.getString("Password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}



