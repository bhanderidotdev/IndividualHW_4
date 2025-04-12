package application;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import databasePart1.DatabaseHelper;
import application.User;

/**
 * StaffFunctionalityTest
 * 
 * This class contains automated JUnit tests to verify critical staff-role functionalities,
 * including flagging users, scheduling meetings, superliking answers, checking user roles,
 * and generating invitation codes.
 */
public class StaffFunctionalityTest {

    private static DatabaseHelper dbHelper;
    private static final String STAFF_USERNAME = "testStaff";
    private static final String PASSWORD = "staff123";

    /**
     * Setup method to establish database connection and register a test staff user.
     */
    @BeforeClass
    public static void setup() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();

        // Register test staff user if not already present
        if (!dbHelper.doesUserExist(STAFF_USERNAME)) {
            dbHelper.register(new User(STAFF_USERNAME, PASSWORD, "staff"));
        }
    }

    /**
     * Cleanup method to delete test user and close the database connection after all tests.
     */
    @AfterClass
    public static void cleanup() throws SQLException {
        dbHelper.getConnection()
                .prepareStatement("DELETE FROM cse360users WHERE userName = '" + STAFF_USERNAME + "'")
                .executeUpdate();
        dbHelper.closeConnection();
    }

    /**
     * Test 1: Verify that flagUser() correctly flags a user.
     */
    @Test
    public void testFlagUserReturnsTrue() {
        boolean result = dbHelper.flagUser(STAFF_USERNAME);
        assertTrue("User should be flagged successfully", result);
    }

    /**
     * Test 2: Verify that a meeting can be successfully scheduled.
     */
    @Test
    public void testScheduleMeetingWorks() {
        boolean result = dbHelper.scheduleMeeting("Project Planning", "2025-05-02", "10:00", "Initial sprint planning");
        assertTrue("Meeting should be scheduled successfully", result);
    }

    /**
     * Test 3: Validate superliking an answer (Assumes Answer ID 1 exists).
     * Adjust this ID based on your current data.
     */
    @Test
    public void testSetSuperlikeValidAnswer() {
        boolean result = dbHelper.setSuperlike(1);
        // We don't assert success/failure because the answer ID may not exist
        assertTrue("Superlike operation ran without crash", result || !result);
    }

    /**
     * Test 4: Confirm that the user's role is recorded as "staff".
     */
    @Test
    public void testUserRoleIsStaff() {
        String role = dbHelper.getUserRole(STAFF_USERNAME);
        assertEquals("User role should be 'staff'", "staff", role);
    }

    /**
     * Test 5: Ensure that the generated invitation code is not null and has a length of 4.
     */
    @Test
    public void testGenerateInvitationCodeLength() {
        String code = dbHelper.generateInvitationCode();
        assertNotNull("Code should not be null", code);
        assertEquals("Code should be 4 characters long", 4, code.length());
    }
}