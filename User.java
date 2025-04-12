package application;

import java.util.List;

import databasePart1.DatabaseHelper;

import java.sql.Connection;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private String role;
    private double rating;
    private List<Message> inbox;

    // Constructor to initialize a new User object with userName, password, and role.
    public User( String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.rating = 0;
    }
    // Might need constructor to create user object from String userName for role checks.
    public User(String userName, DatabaseHelper databaseHelper) {
        String query = "SELECT userName, password, role, rating FROM cse360users WHERE userName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.userName = rs.getString("userName");
                    this.password = rs.getString("password");
                    this.role = rs.getString("role");
                    this.rating = rs.getDouble("rating");
                } else {
                    System.out.println("User not found: " + userName);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user data");
            e.printStackTrace();
        }
    }
    
   public List<Message> getInbox(DatabaseHelper databaseHelper){
	   String query = "";
	   try (Connection conn = databaseHelper.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(query)) 
	        {
	        
	        
	        } catch (SQLException e) {
	   			e.printStackTrace();
	        }
	   return inbox;
   }
    
    // Updates the user password.
    public void setPassword(String password) {
        this.password = password;
    }
    
    // Sets the role of the user.
    public void setRole(String role) {
    	this.role=role;
    }
    
    public void setRating(double rating) {
    	this.rating = rating;
    }

    public double getRating() { return rating; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    
}