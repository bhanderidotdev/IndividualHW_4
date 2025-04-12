package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.List;


/**
 * ReviewQueuePage displays pending review items for the logged-in reviewer.
 * It presents three tabs: one for Questions, one for Main Answers, and one for Clarification Answers.
 * In each tab, the reviewer can click "Review" to add their review.
 */
public class ReviewQueuePage {
    private final DatabaseHelper databaseHelper;
    private final User reviewer;
    private final Stage primaryStage;
    
    /**
     * Constructs a ReviewQueuePage.
     * @param databaseHelper the helper used for database operations
     * @param reviewer the logged-in reviewer
     * @param primaryStage the primary stage for the UI
     */
    public ReviewQueuePage(DatabaseHelper databaseHelper, User reviewer, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.reviewer = reviewer;
        this.primaryStage = primaryStage;
    }
    
    /**
     * Displays the review queue with three tabs: Questions, Main Answers, and Clarification Answers.
     */
    public void show() {
        TabPane tabPane = new TabPane();
        Tab questionsTab = new Tab("Questions");
        Tab mainAnswersTab = new Tab("Main Answers");
        Tab subsetAnswersTab = new Tab("Clarification Answers");
        questionsTab.setClosable(false);
        mainAnswersTab.setClosable(false);
        subsetAnswersTab.setClosable(false);
        
        ListView<HBox> questionListView = new ListView<>();
        ListView<HBox> mainAnswerList = new ListView<>();
        ListView<HBox> subsetAnswerList = new ListView<>();
        
        refreshQuestionList(questionListView);
        refreshMainAnswerList(mainAnswerList);
        refreshSubsetAnswerList(subsetAnswerList);
        
        if (questionListView.getItems().isEmpty()) {
            Label emptyLabel = new Label("No questions available for review.");
            questionListView.getItems().add(new HBox(emptyLabel));
        }
        if (mainAnswerList.getItems().isEmpty()) {
            Label emptyLabel = new Label("No main answers available for review.");
            mainAnswerList.getItems().add(new HBox(emptyLabel));
        }
        if (subsetAnswerList.getItems().isEmpty()) {
            Label emptyLabel = new Label("No clarification answers available for review.");
            subsetAnswerList.getItems().add(new HBox(emptyLabel));
        }
        
        questionsTab.setContent(questionListView);
        mainAnswersTab.setContent(mainAnswerList);
        subsetAnswersTab.setContent(subsetAnswerList);
        tabPane.getTabs().addAll(questionsTab, mainAnswersTab, subsetAnswersTab);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new ReviewerHomePage(databaseHelper, reviewer).show(primaryStage));
        
        VBox layout = new VBox(10, tabPane, backButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Review Queue");
    }
    
    /**
     * Refreshes the list view with questions pending review.
     * A question is pending if there is no review by this reviewer in the questionReviews table.
     * @param listView the ListView to populate with questions
     */
    private void refreshQuestionList(ListView<HBox> listView) {
        listView.getItems().clear();
        String query = "SELECT id, text, author FROM questions " +
                       "WHERE id NOT IN (SELECT questionId FROM questionReviews WHERE reviewer = ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, reviewer.getUserName());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int questionId = rs.getInt("id");
                    String text = rs.getString("text");
                    String author = rs.getString("author");
                    
                    HBox row = new HBox(10);
                    Label questionLabel = new Label(text + " (by " + author + ")");
                    Button reviewButton = new Button("Review");
                    reviewButton.setOnAction(e -> showQuestionReviewPopup(questionId));
                    row.getChildren().addAll(questionLabel, reviewButton);
                    listView.getItems().add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Refreshes the list view with main answers pending review.
     * @param listView the ListView to populate with main answers
     */
    private void refreshMainAnswerList(ListView<HBox> listView) {
        listView.getItems().clear();
        String query = "SELECT id, text, author, questionId FROM answers " +
                       "WHERE id NOT IN (SELECT answerId FROM reviews WHERE author = ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, reviewer.getUserName());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int answerId = rs.getInt("id");
                    String text = rs.getString("text");
                    String author = rs.getString("author");
                    
                    HBox row = new HBox(10);
                    Label answerLabel = new Label(text + " (by " + author + ")");
                    Button reviewButton = new Button("Review");
                    reviewButton.setOnAction(e -> showReviewPopup(answerId));
                    row.getChildren().addAll(answerLabel, reviewButton);
                    listView.getItems().add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Refreshes the list view with clarification (subset) answers pending review.
     * @param listView the ListView to populate with clarification answers
     */
    private void refreshSubsetAnswerList(ListView<HBox> listView) {
        listView.getItems().clear();
        String query = "SELECT id, text, author, saID FROM subSetAnswers " +
                       "WHERE id NOT IN (SELECT answerId FROM reviews WHERE author = ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, reviewer.getUserName());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int answerId = rs.getInt("id");
                    String text = rs.getString("text");
                    String author = rs.getString("author");
                    
                    HBox row = new HBox(10);
                    Label answerLabel = new Label(text + " (by " + author + ")");
                    Button reviewButton = new Button("Review");
                    reviewButton.setOnAction(e -> showReviewPopup(answerId));
                    row.getChildren().addAll(answerLabel, reviewButton);
                    listView.getItems().add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a popup allowing the reviewer to add a review for a main answer or clarification answer.
     * @param answerId the ID of the answer to review
     */
    private void showReviewPopup(int answerId) {
        Stage popupStage = new Stage();
        popupStage.initOwner(primaryStage);
        popupStage.setTitle("Add Review");
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        TextField reviewField = new TextField();
        reviewField.setPromptText("Enter your review (max 500 chars)");
        Button saveButton = new Button("Save Review");
        Label statusLabel = new Label();
        
        saveButton.setOnAction(e -> {
            String reviewText = reviewField.getText().trim();
            if (!reviewText.isEmpty() && reviewText.length() <= 500) {
                Review newReview = new Review(0, reviewText, reviewer.getUserName(), answerId);
                ReviewManager rm = new ReviewManager(databaseHelper);
                rm.saveReview(newReview);
                statusLabel.setText("Review saved!");
                popupStage.close();
                show();
            } else {
                statusLabel.setText("Invalid review text.");
            }
        });
        
        layout.getChildren().addAll(new Label("Write your review:"), reviewField, saveButton, statusLabel);
        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    /**
     * Opens a popup allowing the reviewer to add a review for a question.
     * @param questionId the ID of the question to review
     */
    private void showQuestionReviewPopup(int questionId) {
        Stage popupStage = new Stage();
        popupStage.initOwner(primaryStage);
        popupStage.setTitle("Add Question Review");
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        TextField reviewField = new TextField();
        reviewField.setPromptText("Enter your review for the question (max 500 chars)");
        Button saveButton = new Button("Save Review");
        Label statusLabel = new Label();
        
        saveButton.setOnAction(e -> {
            String reviewText = reviewField.getText().trim();
            if (!reviewText.isEmpty() && reviewText.length() <= 500) {
                QuestionReview qr = new QuestionReview(0, reviewText, reviewer.getUserName(), questionId);
                QuestionReviewManager qrm = new QuestionReviewManager(databaseHelper);
                qrm.saveReview(qr);
                statusLabel.setText("Review saved!");
                popupStage.close();
                show();
            } else {
                statusLabel.setText("Invalid review text.");
            }
        });
        
        layout.getChildren().addAll(new Label("Write your review for the question:"), reviewField, saveButton, statusLabel);
        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
