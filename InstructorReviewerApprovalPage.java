package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.List;

/**
 * The InstructorReviewerApprovalPage class provides a UI for an instructor (admin)
 * to view pending reviewer requests, review a student's Q&A, and approve or deny the request.
 */
public class InstructorReviewerApprovalPage {
    private final DatabaseHelper databaseHelper;
    private final User instructor;
    private final Stage primaryStage;
    private final ReviewerRequestManager requestManager;
    private final QuestionManager questionManager;
    private final AnswerManager answerManager;
    
    /**
     * Constructor to initialize the InstructorReviewerApprovalPage.
     * @param databaseHelper the database helper instance
     * @param instructor the instructor user (admin)
     * @param primaryStage the primary stage for the UI
     */
    public InstructorReviewerApprovalPage(DatabaseHelper databaseHelper, User instructor, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.instructor = instructor;
        this.primaryStage = primaryStage;
        this.requestManager = new ReviewerRequestManager(databaseHelper);
        this.questionManager = new QuestionManager(databaseHelper);
        this.answerManager = new AnswerManager(databaseHelper);
    }
    
    /**
     * Displays the instructor approval UI, listing pending reviewer requests and student Q&A details.
     */
    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Pending Reviewer Requests");
        
        ListView<String> pendingList = new ListView<>();
        refreshPendingList(pendingList);
        
        // TextArea for displaying selected student's Q&A details.
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setPrefHeight(200);
        
        // Button to view the selected student's questions and answers.
        Button viewDetailsButton = new Button("View Student Q&A");
        viewDetailsButton.setOnAction(e -> {
            String selectedStudent = pendingList.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                StringBuilder details = new StringBuilder();
                List<Question> questions = questionManager.getQuestionsByUser(selectedStudent);
                details.append("Questions by ").append(selectedStudent).append(":\n");
                for (Question q : questions) {
                    details.append(q.getId()).append(": ").append(q.getText()).append("\n");
                }
                List<Answer> answers = answerManager.getAnswersByUser(selectedStudent);
                details.append("\nAnswers by ").append(selectedStudent).append(":\n");
                for (Answer a : answers) {
                    details.append(a.getId()).append(": ").append(a.getText()).append("\n");
                }
                detailsArea.setText(details.toString());
            }
        });
        
        // Button to approve the selected student's reviewer request.
        Button approveButton = new Button("Approve Request");
        approveButton.setOnAction(e -> {
            String selectedStudent = pendingList.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                int requestId = requestManager.getRequestId(selectedStudent);
                boolean approved = requestManager.approveRequest(requestId);
                if (approved) {
                    // Update the student's role to "reviewer" in the users table.
                    try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement("UPDATE cse360users SET role = ? WHERE userName = ?")) {
                        pstmt.setString(1, "reviewer");
                        pstmt.setString(2, selectedStudent);
                        pstmt.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    refreshPendingList(pendingList);
                    detailsArea.clear();
                }
            }
        });
        
        // Button to deny the selected student's reviewer request.
        Button denyButton = new Button("Deny Request");
        denyButton.setOnAction(e -> {
            String selectedStudent = pendingList.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                int requestId = requestManager.getRequestId(selectedStudent);
                boolean denied = requestManager.denyRequest(requestId);
                if (denied) {
                    refreshPendingList(pendingList);
                    detailsArea.clear();
                }
            }
        });
        
        // Button to navigate back to the instructor home page.
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            UserHomePage homePage = new UserHomePage(databaseHelper, instructor);
            homePage.show(primaryStage);
        });
        
        HBox buttonsBox = new HBox(10, viewDetailsButton, approveButton, denyButton, backButton);
        layout.getChildren().addAll(titleLabel, pendingList, buttonsBox, detailsArea);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor - Reviewer Requests");
    }
    
    /**
     * Refreshes the pending reviewer requests list.
     * @param pendingList the ListView to update with pending reviewer usernames
     */
    private void refreshPendingList(ListView<String> pendingList) {
        pendingList.getItems().clear();
        List<String> pendingRequests = requestManager.getPendingReviewerRequests();
        pendingList.getItems().addAll(pendingRequests);
    }
}
