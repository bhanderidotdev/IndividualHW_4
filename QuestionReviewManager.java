package application;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * QuestionReviewManager handles CRUD operations for reviews on questions.
 */
public class QuestionReviewManager {
    private final DatabaseHelper databaseHelper;
    
    /**
     * Constructs a QuestionReviewManager.
     * @param databaseHelper the helper used for database operations
     */
    public QuestionReviewManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Saves a new question review to the database.
     * @param qr the QuestionReview object to save
     */
    public void saveReview(QuestionReview qr) {
        String insertQuery = "INSERT INTO questionReviews (text, reviewer, questionId) VALUES (?, ?, ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, qr.getText());
            pstmt.setString(2, qr.getReviewer());
            pstmt.setInt(3, qr.getQuestionId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        qr.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all reviews for a specific question.
     * @param questionId the identifier of the question
     * @return a list of QuestionReview objects associated with the question
     */
    public List<QuestionReview> getReviewsForQuestion(int questionId) {
        List<QuestionReview> reviews = new ArrayList<>();
        String query = "SELECT id, text, reviewer, questionId FROM questionReviews WHERE questionId = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new QuestionReview(rs.getInt("id"), rs.getString("text"), rs.getString("reviewer"), rs.getInt("questionId")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
    
    /**
     * Retrieves all question reviews by a specific reviewer.
     * @param reviewer the reviewer's username
     * @return a list of QuestionReview objects
     */
    public List<QuestionReview> getReviewsByReviewer(String reviewer) {
        List<QuestionReview> reviews = new ArrayList<>();
        String query = "SELECT id, text, reviewer, questionId FROM questionReviews WHERE reviewer = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, reviewer);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new QuestionReview(rs.getInt("id"), rs.getString("text"), rs.getString("reviewer"), rs.getInt("questionId")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
