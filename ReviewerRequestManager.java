package application;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The ReviewerRequestManager class handles operations related to reviewer requests,
 * including submitting requests, retrieving request status, and approving/denying requests.
 */
public class ReviewerRequestManager {
    private final DatabaseHelper databaseHelper;
    
    /**
     * Constructor to initialize the ReviewerRequestManager.
     * @param databaseHelper the database helper instance
     */
    public ReviewerRequestManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Submits a new reviewer request for the given student, only if one does not already exist.
     * @param studentUserName the username of the student requesting reviewer role
     */
    public void submitRequest(String studentUserName) {
        // Check if a reviewer request already exists for this student
        String checkQuery = "SELECT COUNT(*) FROM reviewerRequests WHERE studentUserName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, studentUserName);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Request already exists; do not insert another one
                System.out.println("Reviewer request already exists for " + studentUserName);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        // No existing request found; proceed to insert a new request
        String insertQuery = "INSERT INTO reviewerRequests (studentUserName) VALUES (?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, studentUserName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves the status of a reviewer request for a given student.
     * @param studentUserName the username of the student
     * @return the status of the request (e.g., "pending", "approved", or "denied")
     */
    public String getRequestStatus(String studentUserName) {
        String status = "none";
        String query = "SELECT status FROM reviewerRequests WHERE studentUserName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentUserName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }
    
    /**
     * Approves a reviewer request with the specified request ID.
     * @param requestId the ID of the request to approve
     * @return true if the update was successful, false otherwise
     */
    public boolean approveRequest(int requestId) {
        String updateQuery = "UPDATE reviewerRequests SET status = 'approved' WHERE id = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setInt(1, requestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Denies a reviewer request with the specified request ID.
     * @param requestId the ID of the request to deny
     * @return true if the update was successful, false otherwise
     */
    public boolean denyRequest(int requestId) {
        String updateQuery = "UPDATE reviewerRequests SET status = 'denied' WHERE id = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setInt(1, requestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Retrieves the request ID for a reviewer's request submitted by the given student.
     * @param studentUserName the username of the student
     * @return the request ID, or -1 if not found
     */
    public int getRequestId(String studentUserName) {
        int requestId = -1;
        String query = "SELECT id FROM reviewerRequests WHERE studentUserName = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentUserName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    requestId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestId;
    }
    
    /**
     * Retrieves a list of student usernames with pending reviewer requests.
     * @return a list of student usernames whose requests are pending
     */
    public List<String> getPendingReviewerRequests() {
        List<String> pendingRequests = new ArrayList<>();
        String query = "SELECT studentUserName FROM reviewerRequests WHERE status = 'pending'";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                pendingRequests.add(rs.getString("studentUserName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingRequests;
    }
}
