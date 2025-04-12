package application;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private final DatabaseHelper databaseHelper;
    
    public MessageManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    // Sends a new message by inserting it into the messages table.
    // Assumes the messages table has columns: id (AUTO_INCREMENT), fromAuthor (VARCHAR),
    // toUser (INT), and text (VARCHAR).
    public void sendMessage(Message message) {
        String insertQuery = "INSERT INTO messages (fromAuthor, toUser, text) VALUES (?, ?, ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, message.getAuthor()); // sender
            pstmt.setInt(2, message.getId()); // recipient's user id (stored in Message as 'toUserId')
            pstmt.setString(3, message.getText());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Optionally, update the message object with the DB message id if needed.
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Retrieves all messages for a given recipient username.
    // The recipient's user id is determined using DatabaseHelper.getUserId().
    public List<Message> getMessagesForUser(String userName) {
        List<Message> messages = new ArrayList<>();
        int userId = databaseHelper.getUserId(userName);
        String query = "SELECT id, fromAuthor, text FROM messages WHERE toUser = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message(
                        rs.getInt("id"), 
                        rs.getString("text"), 
                        rs.getString("fromAuthor")
                    );
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
    // Deletes a message by message id if the given user is the recipient.
    public boolean deleteMessage(int messageId, String userName) {
        int userId = databaseHelper.getUserId(userName);
        String query = "DELETE FROM messages WHERE id = ? AND toUser = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, messageId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
