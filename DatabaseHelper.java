package databasePart1;
import java.sql.*;
import java.util.UUID;
import application.User;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, handling invitation codes,
 * and providing search methods for questions and answers.
 */
public class DatabaseHelper {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

    // Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 

    private Connection connection = null;
    private Statement statement = null; 

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            
            // You can use this command to clear the database and restart from fresh.
            //statement.execute("DROP ALL OBJECTS");
            
            //deleteAllTables();
            
            createTables();  // Create the necessary tables if they don't exist
            
            System.out.println("Database tables created successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        // Create the invitation codes table
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "isUsed BOOLEAN DEFAULT FALSE)";
        statement.execute(invitationCodesTable);

        // Create the cse360users table
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(20), "
                + "rating DOUBLE DEFAULT 0.0)";
        statement.execute(userTable);
        
        // Create the questions table (Main Questions)
        String questionsTable = "CREATE TABLE IF NOT EXISTS questions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text VARCHAR(500), "
                + "author VARCHAR(255))";
        statement.execute(questionsTable);

        // Create the subSetQuestions table (Linked to Questions)
        String subSetQuestionsTable = "CREATE TABLE IF NOT EXISTS subSetQuestions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "qID INT, "  // Links this subset question to a main question
                + "text VARCHAR(500), "
                + "author VARCHAR(255), "
                + "FOREIGN KEY (qID) REFERENCES questions(id) ON DELETE CASCADE)";
        statement.execute(subSetQuestionsTable);

        // Create the answers table with new "superliked" column
        String answersTable = "CREATE TABLE IF NOT EXISTS answers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text VARCHAR(500), "
                + "author VARCHAR(255), "
                + "questionId INT, "
                + "resolved BOOLEAN DEFAULT FALSE, " 
                + "superliked BOOLEAN DEFAULT FALSE, " 
                + "FOREIGN KEY (questionId) REFERENCES questions(id) ON DELETE CASCADE)";
        statement.execute(answersTable);
        
        // Create the subSetAnswers table
        String subsetAnswersTable = "CREATE TABLE IF NOT EXISTS subSetAnswers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "author VARCHAR(255) NOT NULL, "
                + "text VARCHAR(255) NOT NULL, "
                + "saID INT, "
                + "resolved BOOLEAN DEFAULT FALSE, " 
                + "FOREIGN KEY (saID) REFERENCES subSetQuestions(id) ON DELETE CASCADE)";
        statement.execute(subsetAnswersTable);
        
        // Create the reviews table
        String reviewsTable = "CREATE TABLE IF NOT EXISTS reviews ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text VARCHAR(500), "
                + "author VARCHAR(255), "
                + "answerId INT, "
                + "FOREIGN KEY (answerId) REFERENCES answers(id) ON DELETE CASCADE)";
        statement.execute(reviewsTable);
        
        // Create the messages table
        String messagesTable = "CREATE TABLE IF NOT EXISTS messages ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "fromAuthor VARCHAR(255), "
                + "toUser INT, "
                + "text VARCHAR(200), "
                + "FOREIGN KEY (toUser) REFERENCES cse360users(id) ON DELETE CASCADE)";
        statement.execute(messagesTable);
        
        // Create the trustedReviewers table for maintaining a student's list of trusted reviewers.
        String trustedReviewersTable = "CREATE TABLE IF NOT EXISTS trustedReviewers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "studentUserName VARCHAR(255), "
                + "reviewerUserName VARCHAR(255), "
                + "weightage DOUBLE DEFAULT 1.0, "
                + "FOREIGN KEY (studentUserName) REFERENCES cse360users(userName) ON DELETE CASCADE, "
                + "FOREIGN KEY (reviewerUserName) REFERENCES cse360users(userName) ON DELETE CASCADE)";
        statement.execute(trustedReviewersTable);
        
        // Create the reviewerRequests table to manage requests for reviewer role.
        String reviewerRequestsTable = "CREATE TABLE IF NOT EXISTS reviewerRequests ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "studentUserName VARCHAR(255), "
                + "status VARCHAR(20) DEFAULT 'pending', "
                + "FOREIGN KEY (studentUserName) REFERENCES cse360users(userName) ON DELETE CASCADE)";
        statement.execute(reviewerRequestsTable);
        
        // Create the questionReviews table for reviews on questions.
        String questionReviewsTable = "CREATE TABLE IF NOT EXISTS questionReviews ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text VARCHAR(500), "
                + "reviewer VARCHAR(255), "
                + "questionId INT, "
                + "FOREIGN KEY (questionId) REFERENCES questions(id) ON DELETE CASCADE)";
        statement.execute(questionReviewsTable);
    }

    // Check if the database is empty
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }

    // Registers a new user in the database.
    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        }
    }
    
    // Updates the password of an existing user in the database.
    public void updateUserPassword(String userName, String newPassword) throws SQLException {
        String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Validates a user's login credentials.
    public boolean login(User user) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    // Gets the database connection.
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return connection;
    }
    
    // Checks if a user already exists in the database based on their userName.
    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // If the count is greater than 0, the user exists
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // If an error occurs, assume user doesn't exist
    }
    
    public int getUserId(String userName) {
        String query = "SELECT id FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Retrieves the role of a user from the database using their userName.
    public String getUserRole(String userName) {
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Generates a new invitation code and inserts it into the database.
    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
        String query = "INSERT INTO InvitationCodes (code) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }
    
    // Validates an invitation code to check if it is unused.
    public boolean validateInvitationCode(String code) {
        String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Mark the code as used
                markInvitationCodeAsUsed(code);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Marks the invitation code as used in the database.
    private void markInvitationCodeAsUsed(String code) {
        String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // New method: Search for questions and answers by keyword (combined search, not case sensitive).
    public ResultSet searchQuestionsAndAnswers(String keyword) throws SQLException {
        String searchQuery = "SELECT q.id AS questionId, q.text AS questionText, q.author AS questionAuthor, " +
                             "a.id AS answerId, a.text AS answerText, a.author AS answerAuthor " +
                             "FROM questions q LEFT JOIN answers a ON q.id = a.questionId " +
                             "WHERE LOWER(q.text) LIKE LOWER(?) OR LOWER(a.text) LIKE LOWER(?)";
        PreparedStatement pstmt = connection.prepareStatement(searchQuery);
        String likeKeyword = "%" + keyword + "%";
        pstmt.setString(1, likeKeyword);
        pstmt.setString(2, likeKeyword);
        return pstmt.executeQuery();
    }
    
    // New method: Search only questions by keyword (not case sensitive).
    public ResultSet searchQuestions(String keyword) throws SQLException {
        String query = "SELECT id, text, author FROM questions WHERE LOWER(text) LIKE LOWER(?)";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, "%" + keyword + "%");
        return pstmt.executeQuery();
    }
    
    // New method: Search only answers by keyword (not case sensitive).
    public ResultSet searchAnswers(String keyword) throws SQLException {
        String query = "SELECT id, text, author, questionId FROM answers WHERE LOWER(text) LIKE LOWER(?)";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, "%" + keyword + "%");
        return pstmt.executeQuery();
    }
    
    // New method: Get answers for a specific question.
    public ResultSet getAnswersForQuestion(int questionId) throws SQLException {
        String query = "SELECT id, text, author, superliked FROM answers WHERE questionId = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, questionId);
        return pstmt.executeQuery();
    }
    
    // New method: Get the question related to a specific answer.
    public ResultSet getQuestionForAnswer(int answerId) throws SQLException {
        String query = "SELECT q.id, q.text, q.author FROM questions q " +
                       "JOIN answers a ON q.id = a.questionId WHERE a.id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, answerId);
        return pstmt.executeQuery();
    }
    
    public ResultSet checkTableStructure() throws SQLException {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'QUESTIONS'";
        PreparedStatement pstmt = connection.prepareStatement(query);
        return pstmt.executeQuery();
    }
    
    public void printTableStructure() {
        try (ResultSet rs = checkTableStructure()) {
            System.out.println("Columns in 'questions' table:");
            while (rs.next()) {
                System.out.println(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAllTables() {
        String query = "DROP ALL OBJECTS";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("All tables deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void setSubSetResolved(int answerID) throws SQLException {
        System.out.println("AnswerID is: " + answerID);
        boolean currentStatus = false;

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT resolved FROM subSetAnswers WHERE id = ?")) {

            checkStmt.setInt(1, answerID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    currentStatus = rs.getBoolean("resolved");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        boolean newStatus = !currentStatus; 

        try (Connection conn = getConnection();
             PreparedStatement updateStmt = conn.prepareStatement("UPDATE subSetAnswers SET resolved = ? WHERE id = ?")) {

            updateStmt.setBoolean(1, newStatus);
            updateStmt.setInt(2, answerID);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void setResolved(int answerID) throws SQLException {
        System.out.println("AnswerID is: " + answerID);

        boolean currentStatus = false;

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT resolved FROM answers WHERE id = ?")) {

            checkStmt.setInt(1, answerID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    currentStatus = rs.getBoolean("resolved");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        boolean newStatus = !currentStatus;

        try (Connection conn = getConnection();
             PreparedStatement updateStmt = conn.prepareStatement("UPDATE answers SET resolved = ? WHERE id = ?")) {

            updateStmt.setBoolean(1, newStatus);
            updateStmt.setInt(2, answerID);
            updateStmt.executeUpdate();
            
            System.out.println("Resolved status updated to: " + newStatus + " for answer ID: " + answerID);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isSubSetResolved(int answerID) {
        String query = "SELECT resolved FROM subSetAnswers WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, answerID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {  
                return rs.getBoolean("resolved");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }   
    
    public boolean isResolved(int answerID) {
        String query = "SELECT resolved FROM answers WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, answerID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {  
                return rs.getBoolean("resolved"); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    } 

    // New method: Set an answer as superliked.
    public boolean setSuperlike(int answerId) {
        String query = "UPDATE answers SET superliked = TRUE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, answerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // New method: Flag a user (for example, by setting their rating to -1).
    public boolean flagUser(String userName) {
        String query = "UPDATE cse360users SET rating = -1 WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // New method: Schedule a meeting. This method creates the meetings table if needed and inserts a new meeting.
    public boolean scheduleMeeting(String title, String date, String time, String details) {
        try {
            // Create the meetings table if it doesn't exist
            String createMeetings = "CREATE TABLE IF NOT EXISTS meetings ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "title VARCHAR(255), "
                    + "date VARCHAR(50), "
                    + "time VARCHAR(10), "
                    + "details VARCHAR(500))";
            statement.execute(createMeetings);
            String query = "INSERT INTO meetings (title, date, time, details) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, title);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.setString(4, details);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void closeConnection() {
        try { 
            if(statement != null) statement.close(); 
        } catch(SQLException se2) { 
            se2.printStackTrace();
        } 
        try { 
            if(connection != null) connection.close(); 
        } catch(SQLException se) { 
            se.printStackTrace();
        } 
    }
}
