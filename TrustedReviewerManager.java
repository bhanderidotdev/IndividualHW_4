package application;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrustedReviewerManager {
    private final DatabaseHelper databaseHelper;
    
    public TrustedReviewerManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    // Adds a trusted reviewer for a student with a given weightage.
    public void addTrustedReviewer(String studentUserName, String reviewerUserName, double weightage) {
        String insertQuery = "INSERT INTO trustedReviewers (studentUserName, reviewerUserName, weightage) VALUES (?, ?, ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, studentUserName);
            pstmt.setString(2, reviewerUserName);
            pstmt.setDouble(3, weightage);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Removes a trusted reviewer from a student's list.
    public boolean removeTrustedReviewer(String studentUserName, String reviewerUserName) {
        String deleteQuery = "DELETE FROM trustedReviewers WHERE studentUserName = ? AND reviewerUserName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, studentUserName);
            pstmt.setString(2, reviewerUserName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Updates the weightage for a trusted reviewer.
    public boolean updateWeightage(String studentUserName, String reviewerUserName, double newWeightage) {
        String updateQuery = "UPDATE trustedReviewers SET weightage = ? WHERE studentUserName = ? AND reviewerUserName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setDouble(1, newWeightage);
            pstmt.setString(2, studentUserName);
            pstmt.setString(3, reviewerUserName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Retrieves the list of trusted reviewers for a given student, ordered by weightage descending.
    public List<String> getTrustedReviewers(String studentUserName) {
        List<String> reviewers = new ArrayList<>();
        String query = "SELECT reviewerUserName FROM trustedReviewers WHERE studentUserName = ? ORDER BY weightage DESC";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentUserName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviewers.add(rs.getString("reviewerUserName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviewers;
    }
    
    // Retrieves the weightage value for a specific trusted reviewer of a student.
    public double getWeightage(String studentUserName, String reviewerUserName) {
        double weightage = 0;
        String query = "SELECT weightage FROM trustedReviewers WHERE studentUserName = ? AND reviewerUserName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentUserName);
            pstmt.setString(2, reviewerUserName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    weightage = rs.getDouble("weightage");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weightage;
    }
}
