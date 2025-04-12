package application;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The AnswerManager class handles CRUD operations for Answer objects.
 */
public class AnswerManager {
    private final DatabaseHelper databaseHelper;

    /**
     * Constructor to initialize the AnswerManager.
     * @param databaseHelper the database helper instance
     */
    public AnswerManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Saves a new answer to the database, preventing duplicates.
     * @param answer the Answer object to save
     */
    public void saveAnswer(Answer answer) {
        String checkQuery = "SELECT COUNT(*) FROM answers WHERE text = ? AND author = ? AND questionId = ?";
        String insertQuery = "INSERT INTO answers (text, author, questionId) VALUES (?, ?, ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            checkStmt.setString(1, answer.getText());
            checkStmt.setString(2, answer.getAuthor());
            checkStmt.setInt(3, answer.getQuestionId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Duplicate answer found; do not insert
            }
            insertStmt.setString(1, answer.getText());
            insertStmt.setString(2, answer.getAuthor());
            insertStmt.setInt(3, answer.getQuestionId());
            insertStmt.executeUpdate();
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    answer.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Saves a new subset answer to the database, preventing duplicates.
     * @param answer the subset Answer object to save
     */
    public void saveSubSetAnswer(Answer answer) {
        String checkQuery = "SELECT COUNT(*) FROM subSetAnswers WHERE text = ? AND author = ? AND saID = ?";
        String insertQuery = "INSERT INTO subSetAnswers (text, author, saID) VALUES (?, ?, ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            checkStmt.setString(1, answer.getText());
            checkStmt.setString(2, answer.getAuthor());
            checkStmt.setInt(3, answer.getQuestionId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Duplicate subset answer detected, not inserting: " + answer.getText());
                return;
            }
            insertStmt.setString(1, answer.getText());
            insertStmt.setString(2, answer.getAuthor());
            insertStmt.setInt(3, answer.getQuestionId());
            System.out.println("Inserting subset answer for saID: " + answer.getQuestionId());
            insertStmt.executeUpdate();
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    answer.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all answers for a specific main question.
     * @param questionId the ID of the main question
     * @return a list of Answer objects
     */
    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT id, text, author, questionId FROM answers WHERE questionId = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    answers.add(new Answer(rs.getInt("id"), rs.getString("text"), rs.getString("author"), rs.getInt("questionId")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Fetching main question answers for question ID: " + questionId);
        return answers;
    }
    
    /**
     * Retrieves all subset answers for a specific subset question.
     * @param subsetQuestionID the ID of the subset question
     * @return a list of subset Answer objects
     */
    public List<Answer> getSubSetAnswersForQuestion(int subsetQuestionID) {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT * FROM subSetAnswers WHERE saID = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, subsetQuestionID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    answers.add(new Answer(rs.getInt("id"), rs.getString("text"), rs.getString("author"), rs.getInt("saID")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Fetching subset answers for question ID: " + subsetQuestionID);
        return answers;
    }
    
    /**
     * Creates and saves a new main answer.
     * @param id the proposed answer ID
     * @param text the answer text
     * @param author the author of the answer
     * @param questionId the ID of the main question
     * @return the created Answer object, or null if invalid
     */
    public Answer createAnswer(int id, String text, String author, int questionId) {
        if (text == null || text.trim().isEmpty() || text.length() > 500) {
            return null; // Invalid answer text
        }
        Answer answer = new Answer(id, text, author, questionId);
        saveAnswer(answer);
        return answer;
    }
    
    /**
     * Creates and saves a new subset answer.
     * @param id the proposed answer ID for the subset answer
     * @param text the answer text
     * @param author the author of the answer
     * @param subsetQuestionID the ID of the subset question
     * @return the created Answer object, or null if invalid
     */
    public Answer createSubSetAnswer(int id, String text, String author, int subsetQuestionID) {
        if (text == null || text.trim().isEmpty() || text.length() > 500) {
            return null; // Invalid answer text
        }
        Answer answer = new Answer(id, text, author, subsetQuestionID);
        saveSubSetAnswer(answer);
        return answer;
    }
    
    /**
     * Updates an existing main answer if the user is the author.
     * @param answerId the ID of the answer to update
     * @param newText the new answer text
     * @param userName the username of the editor
     * @return true if the update was successful, false otherwise
     */
    public boolean updateAnswer(int answerId, String newText, String userName) {
        String query = "UPDATE answers SET text = ? WHERE id = ? AND author = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, answerId);
            pstmt.setString(3, userName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Updates an existing subset answer if the user is the author.
     * @param answerId the ID of the subset answer to update
     * @param newText the new answer text
     * @param userName the username of the editor
     * @return true if the update was successful, false otherwise
     */
    public boolean updatesubSetAnswer(int answerId, String newText, String userName) {
        String query = "UPDATE subSetAnswers SET text = ? WHERE id = ? AND author = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, answerId);
            pstmt.setString(3, userName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Deletes a main answer if the user is the author or an admin.
     * @param answerId the ID of the answer to delete
     * @param userName the username of the requester
     * @param isAdmin whether the requester is an admin
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteAnswer(int answerId, String userName, boolean isAdmin) {
        String checkQuery = "SELECT author FROM answers WHERE id = ?";
        String deleteQuery = "DELETE FROM answers WHERE id = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            checkStmt.setInt(1, answerId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                String author = rs.getString("author");
                if (author.equals(userName) || isAdmin) {
                    deleteStmt.setInt(1, answerId);
                    return deleteStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Retrieves all main answers written by a specific user.
     * @param userName the username of the author
     * @return a list of Answer objects written by the user
     */
    public List<Answer> getAnswersByUser(String userName) {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT id, text, author, questionId FROM answers WHERE author = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    answers.add(new Answer(rs.getInt("id"), rs.getString("text"), rs.getString("author"), rs.getInt("questionId")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }
}
