package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.util.List;

/**
 * StudentQuestionReviewPopup displays the reviews for a specific question.
 * Each review is shown with both a "Rate Reviewer" button and a "Message Reviewer" button.
 * "Rate Reviewer" allows a student to assign a weight to the reviewer,
 * while "Message Reviewer" opens a compose dialog for private messaging.
 */
public class StudentQuestionReviewPopup {
    private final DatabaseHelper databaseHelper;
    private final int questionId;
    private final String studentUserName;
    private final Stage primaryStage;
    
    /**
     * Constructs a StudentQuestionReviewPopup.
     * @param databaseHelper the helper used for database operations
     * @param questionId the identifier of the question
     * @param studentUserName the username of the student viewing the reviews
     * @param primaryStage the primary stage of the application
     */
    public StudentQuestionReviewPopup(DatabaseHelper databaseHelper, int questionId, String studentUserName, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.questionId = questionId;
        this.studentUserName = studentUserName;
        this.primaryStage = primaryStage;
    }
    
    /**
     * Displays a popup window listing all reviews for the specified question.
     * Each review row includes a "Rate Reviewer" button and a "Message Reviewer" button.
     */
    public void show() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Reviews for Question ID: " + questionId);
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Question Reviews:");
        ListView<HBox> reviewList = new ListView<>();
        
        // Retrieve reviews using the QuestionReviewManager.
        QuestionReviewManager qrm = new QuestionReviewManager(databaseHelper);
        List<QuestionReview> reviews = qrm.getReviewsForQuestion(questionId);
        if (reviews.isEmpty()) {
            Label emptyLabel = new Label("No reviews available.");
            reviewList.getItems().add(new HBox(emptyLabel));
        } else {
            for (QuestionReview qr : reviews) {
                HBox row = new HBox(10);
                Label reviewLabel = new Label(qr.toString());
                
                // "Rate Reviewer" button.
                Button rateButton = new Button("Rate Reviewer");
                rateButton.setOnAction(e -> showRateReviewerDialog(qr.getReviewer()));
                
                // "Message Reviewer" button.
                Button messageButton = new Button("Message Reviewer");
                messageButton.setOnAction(e -> composeMessageToReviewer(qr.getReviewer()));
                
                row.getChildren().addAll(reviewLabel, rateButton, messageButton);
                reviewList.getItems().add(row);
            }
        }
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        
        layout.getChildren().addAll(titleLabel, reviewList, closeButton);
        Scene scene = new Scene(layout, 450, 350);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    /**
     * Opens a dialog allowing the student to rate the reviewer.
     * @param reviewerName the username of the reviewer to rate
     */
    private void showRateReviewerDialog(String reviewerName) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Rate Reviewer: " + reviewerName);
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label promptLabel = new Label("Enter weight (e.g., 1.0 to 5.0):");
        TextField weightField = new TextField();
        Button saveButton = new Button("Save");
        Label statusLabel = new Label();
        
        saveButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                if (weight < 1.0 || weight > 5.0) {
                    statusLabel.setText("Weight must be between 1.0 and 5.0.");
                } else {
                    TrustedReviewerManager trm = new TrustedReviewerManager(databaseHelper);
                    double currentWeight = trm.getWeightage(studentUserName, reviewerName);
                    if (currentWeight == 0) {
                        trm.addTrustedReviewer(studentUserName, reviewerName, weight);
                    } else {
                        trm.updateWeightage(studentUserName, reviewerName, weight);
                    }
                    statusLabel.setText("Weight updated to " + weight);
                    dialog.close();
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid weight value.");
            }
        });
        
        layout.getChildren().addAll(promptLabel, weightField, saveButton, statusLabel);
        Scene scene = new Scene(layout, 300, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    /**
     * Opens a compose dialog to send a private message to the reviewer.
     * The full conversation can be accessed via the Inbox.
     * @param reviewerName the username of the reviewer to message
     */
    private void composeMessageToReviewer(String reviewerName) {
        Stage composeStage = new Stage();
        composeStage.initModality(Modality.APPLICATION_MODAL);
        composeStage.setTitle("Compose Message to " + reviewerName);
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label recipientLabel = new Label("To: " + reviewerName);
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here (max 200 characters)...");
        messageArea.setWrapText(true);
        
        Button sendButton = new Button("Send");
        Label statusLabel = new Label();
        
        sendButton.setOnAction(e -> {
            String text = messageArea.getText().trim();
            if (text.isEmpty() || text.length() > 200) {
                statusLabel.setText("Invalid message. Please check the text.");
            } else {
                int recipientId = databaseHelper.getUserId(reviewerName);
                if (recipientId == -1) {
                    statusLabel.setText("Reviewer not found.");
                } else {
                    Message newMessage = new Message(0, text, studentUserName);
                    newMessage.setId(recipientId);
                    MessageManager mm = new MessageManager(databaseHelper);
                    mm.sendMessage(newMessage);
                    statusLabel.setText("Message sent to " + reviewerName + ".");
                    composeStage.close();
                }
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> composeStage.close());
        
        layout.getChildren().addAll(recipientLabel, messageArea, sendButton, statusLabel, cancelButton);
        Scene scene = new Scene(layout, 400, 300);
        composeStage.setScene(scene);
        composeStage.showAndWait();
    }
}
