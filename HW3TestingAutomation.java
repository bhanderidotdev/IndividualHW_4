package application;

import databasePart1.DatabaseHelper;

/**
 * <p> Title: HW3TestingAutomation Class. </p>
 * 
 * <p> Description: A demonstration of automated tests for key methods from the Question and Answer modules.
 * The tests cover the following methods:
 * <ol>
 *   <li>Question.setText(String text)</li>
 *   <li>Question.addAnswer(Answer answer)</li>
 *   <li>Answer.setText(String text)</li>
 *   <li>QuestionManager.editQuestion(int questionId, String newText, String userName)</li>
 *   <li>AnswerManager.updateAnswer(int answerId, String newText, String userName)</li>
 * </ol>
 * Each test verifies correct behavior under valid and invalid input conditions.
 * </p>
 * 
 * <p> Copyright: Andrew Palmer © 2025 </p>
 * 
 * @author Andrew Palmer
 * @version 1.0 2025-03-22
 */
public class HW3TestingAutomation {

    static int numPassed = 0;  // Counter for passed tests
    static int numFailed = 0;  // Counter for failed tests
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    /**
     * The main method connects to the database, runs the automated tests,
     * and prints the results.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            databaseHelper.connectToDatabase();
            System.out.println("Connecting to database...");
            System.out.println("Database tables created successfully.");
            System.out.println("______________________________________");
            System.out.println("\nHW3 Automated Testing for Question/Answer Methods");

            runTestQuestionSetText();
            runTestQuestionAddAnswer();
            runTestAnswerSetText();
            runTestQuestionManagerEditQuestion();
            runTestAnswerManagerUpdateAnswer();

            System.out.println("____________________________________________________________________________");
            System.out.println("\nNumber of tests passed: " + numPassed);
            System.out.println("Number of tests failed: " + numFailed);
        } catch (Exception e) {
            System.err.println("Error during tests: " + e.getMessage());
        } finally {
            databaseHelper.closeConnection();
        }
    }

    /**
     * Tests the Question.setText method by attempting valid and invalid updates.
     * Valid updates should change the text while invalid ones (null, empty, or too long)
     * leave the text unchanged.
     */
    private static void runTestQuestionSetText() {
        System.out.println("\nRunning Test: Question.setText");
        // Create a Question with initial text.
        Question q = new Question(1, "Original text", "Author1");
        
        // Valid update
        q.setText("Updated valid text");
        boolean test1 = "Updated valid text".equals(q.getText());
        validateTestResult(test1, true);

        // Invalid update: null value should be ignored.
        q.setText(null);
        boolean test2 = "Updated valid text".equals(q.getText());
        validateTestResult(test2, true);

        // Invalid update: empty string should be ignored.
        q.setText("   ");
        boolean test3 = "Updated valid text".equals(q.getText());
        validateTestResult(test3, true);

        // Invalid update: too long string (501 characters) should be ignored.
        String longText = new String(new char[501]).replace('\0', 'a');
        q.setText(longText);
        boolean test4 = "Updated valid text".equals(q.getText());
        validateTestResult(test4, true);
    }

    /**
     * Tests the Question.addAnswer method by verifying that the answer list is updated.
     * It creates a Question, adds an Answer, and checks that the list size increases.
     */
    private static void runTestQuestionAddAnswer() {
        System.out.println("\nRunning Test: Question.addAnswer");
        Question q = new Question(2, "Test question", "Author2");
        int initialCount = q.getAnswers().size();
        Answer a = new Answer(1, "Test answer", "AuthorA", 2);
        q.addAnswer(a);
        boolean test1 = q.getAnswers().size() == initialCount + 1;
        validateTestResult(test1, true);
    }

    /**
     * Tests the Answer.setText method by checking updates with valid and invalid text.
     * Valid updates should change the text while invalid updates (null, empty, too long)
     * should be ignored.
     */
    private static void runTestAnswerSetText() {
        System.out.println("\nRunning Test: Answer.setText");
        Answer a = new Answer(2, "Original answer", "AuthorB", 2);
        
        // Valid update
        a.setText("Updated answer");
        boolean test1 = "Updated answer".equals(a.getText());
        validateTestResult(test1, true);

        // Invalid update: null should be ignored.
        a.setText(null);
        boolean test2 = "Updated answer".equals(a.getText());
        validateTestResult(test2, true);

        // Invalid update: empty string should be ignored.
        a.setText("   ");
        boolean test3 = "Updated answer".equals(a.getText());
        validateTestResult(test3, true);

        // Invalid update: too long string (501 characters) should be ignored.
        String longText = new String(new char[501]).replace('\0', 'b');
        a.setText(longText);
        boolean test4 = "Updated answer".equals(a.getText());
        validateTestResult(test4, true);
    }

    /**
     * Tests the QuestionManager.editQuestion method by verifying that only the author can edit the question.
     * It first attempts an edit with the correct author (which should succeed) and then with an incorrect user (which should fail).
     */
    private static void runTestQuestionManagerEditQuestion() {
        System.out.println("\nRunning Test: QuestionManager.editQuestion");
        QuestionManager qm = new QuestionManager(databaseHelper);
        // Create a question using the manager.
        Question q = qm.createQuestion(3, "Initial question", "TestUser");
        // Edit with the correct user.
        boolean editSuccess = qm.editQuestion(q.getId(), "Edited question", "TestUser");
        validateTestResult(editSuccess, true);
        // Attempt edit with an incorrect user (should fail).
        boolean editFail = qm.editQuestion(q.getId(), "Another edit", "WrongUser");
        validateTestResult(editFail, false);
    }

    /**
     * Tests the AnswerManager.updateAnswer method by ensuring that only the author can update an answer.
     * It creates a valid Question with a unique text to satisfy the foreign key constraint,
     * then creates an Answer linked to that question.
     * An update is attempted with the correct author (expected to succeed)
     * and then with an incorrect user (expected to fail).
     */
    private static void runTestAnswerManagerUpdateAnswer() {
        System.out.println("\nRunning Test: AnswerManager.updateAnswer");
        AnswerManager am = new AnswerManager(databaseHelper);
        QuestionManager qm = new QuestionManager(databaseHelper);
        
        // Create a unique question to ensure insertion into the database.
        String uniqueText = "Dummy question for answer update test " + System.currentTimeMillis();
        Question qForAnswer = qm.createQuestion(100, uniqueText, "AnswerUser");
        System.out.println("Created Question with id: " + qForAnswer.getId());
        
        // Create an Answer using the manager with the valid questionId.
        Answer a = am.createAnswer(3, "Initial answer", "AnswerUser", qForAnswer.getId());
        
        // Update with the correct user.
        boolean updateSuccess = am.updateAnswer(a.getId(), "Updated answer text", "AnswerUser");
        validateTestResult(updateSuccess, true);
        
        // Attempt update with an incorrect user (should fail).
        boolean updateFail = am.updateAnswer(a.getId(), "Another update", "WrongUser");
        validateTestResult(updateFail, false);
    }

    /**
     * Validates the result of a test case by comparing the actual outcome to the expected outcome.
     *
     * @param result   the actual result of the test
     * @param expected the expected result of the test
     */
    private static void validateTestResult(boolean result, boolean expected) {
        if (result == expected) {
            System.out.println("*** Success *** Test passed!");
            numPassed++;
        } else {
            System.out.println("*** Failure *** Test failed!");
            numFailed++;
        }
    }
}