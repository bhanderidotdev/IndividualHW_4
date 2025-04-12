package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.util.List;

/**
 * ReviewerReviewPage allows a reviewer to view, edit, and delete their own reviews.
 */
public class ReviewerReviewPage {
    private final DatabaseHelper databaseHelper;
    private final User user;
    private final ReviewManager reviewManager;
    private final Stage primaryStage;
    private ListView<HBox> reviewList;
    
    /**
     * Constructor to initialize ReviewerReviewPage.
     * @param databaseHelper the database helper instance
     * @param user the reviewer user
     * @param primaryStage the primary stage for the UI
     */
    public ReviewerReviewPage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;
        this.primaryStage = primaryStage;
        this.reviewManager = new ReviewManager(databaseHelper);
    }
    
    /**
     * Displays the page listing the reviewer's reviews with options to edit or delete.
     */
    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Your Reviews");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        reviewList = new ListView<>();
        refreshReviewList();
        
        // Back button returns to ReviewerHomePage.
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new ReviewerHomePage(databaseHelper, user).show(primaryStage);
        });
        
        layout.getChildren().addAll(titleLabel, reviewList, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your Reviews");
    }
    
    /**
     * Refreshes the list of reviews displayed.
     */
    private void refreshReviewList() {
        reviewList.getItems().clear();
        List<Review> reviews = reviewManager.getReviewsFromUser(user.getUserName());
        for (Review r : reviews) {
            HBox row = new HBox(10);
            Label reviewLabel = new Label(r.getText());
            
            // Edit button: shows a popup to edit the review.
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> showEditPopup(r));
            
            // Delete button: removes the review.
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                boolean deleted = reviewManager.deleteReview(r.getId(), user.getUserName(), false);
                if (deleted) {
                    refreshReviewList();
                }
            });
            
            row.getChildren().addAll(reviewLabel, editButton, deleteButton);
            reviewList.getItems().add(row);
        }
    }
    
    /**
     * Displays a popup window to edit a review.
     * @param review the review to edit
     */
    private void showEditPopup(Review review) {
        Stage popupStage = new Stage();
        popupStage.initOwner(primaryStage);
        popupStage.setTitle("Edit Review");
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        TextField editField = new TextField(review.getText());
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String newText = editField.getText().trim();
            if (!newText.isEmpty() && newText.length() <= 500) {
                boolean updated = reviewManager.updateReview(review.getId(), newText, user.getUserName());
                if (updated) {
                    refreshReviewList();
                    popupStage.close();
                }
            }
        });
        layout.getChildren().addAll(new Label("Edit your review:"), editField, saveButton);
        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
