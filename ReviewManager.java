package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import databasePart1.DatabaseHelper;

public class ReviewManager {
    private final DatabaseHelper databaseHelper;

    // Constructor to initialize the ReviewManager.
    public ReviewManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    // Saves a new review to the database.
    public void saveReview(Review review) {
        String insertQuery = "INSERT INTO reviews (text, author, answerId) VALUES (?, ?, ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, review.getText());
            pstmt.setString(2, review.getAuthor());
            pstmt.setInt(3, review.getQuestionId()); // Note: getQuestionId() returns answerId in Review.java
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        review.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all reviews for a specific answer.
     * @param answerId the identifier of the answer
     * @return a list of reviews associated with the answer
     */
    public List<Review> getReviewsForAnswer(int answerId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT id, text, author, answerId FROM reviews WHERE answerId = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, answerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new Review(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("author"),
                        rs.getInt("answerId")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
    
    // Retrieves all reviews written by a specific user.
    public List<Review> getReviewsFromUser(String user) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT id, text, author, answerId FROM reviews WHERE author = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new Review(
                        rs.getInt("id"), 
                        rs.getString("text"), 
                        rs.getString("author"), 
                        rs.getInt("answerId")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
    
    // Retrieves the rating (weightage) for a given reviewer from the users table.
    public double getRatingFromUser(String user) {
        double rating = 0;
        String query = "SELECT rating FROM cse360users WHERE userName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rating = rs.getDouble("rating");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rating;
    }
    
    // Updates an existing review if the user is the author.
    public boolean updateReview(int reviewId, String newText, String userName) {
        String query = "UPDATE reviews SET text = ? WHERE id = ? AND author = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, reviewId);
            pstmt.setString(3, userName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Deletes a review if the user is the author or has admin privileges.
    public boolean deleteReview(int reviewId, String userName, boolean isAdmin) {
        String checkQuery = "SELECT author FROM reviews WHERE id = ?";
        String deleteQuery = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            checkStmt.setInt(1, reviewId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String author = rs.getString("author");
                    if (author.equals(userName) || isAdmin) {
                        deleteStmt.setInt(1, reviewId);
                        return deleteStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
