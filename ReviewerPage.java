package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import javafx.stage.Modality;
import java.util.List;
import databasePart1.DatabaseHelper;

/**
 * The ReviewerPage class allows users to view a specific users reviews
 */
public class ReviewerPage {
    private final ReviewManager reviewManager;
    private final AnswerManager  answerManager;
    private final User user; //user of the viewer
    private final String reviewer;
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private Question previousQuestion;
    private ListView<HBox> reviewList;

    public ReviewerPage(User user, String reviewer, DatabaseHelper databaseHelper, Stage primaryStage, Question q) {
        reviewManager = new ReviewManager(databaseHelper);
        this.answerManager = new AnswerManager(databaseHelper);
        this.user = user;	
        this.reviewer = reviewer;
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        this.previousQuestion = q;
    }

    // Displays the question management UI.
    // Allows users to submit, edit, delete, and manage answers for questions.
    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("View reviews by"+ user.getUserName());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label rating = new Label("Rating: "+ reviewManager.getRatingFromUser(reviewer));
        
        reviewList = new ListView<>();
        refreshReviewList();
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            AnswerPage answerPage = new AnswerPage(answerManager, user, databaseHelper, primaryStage, previousQuestion);
            answerPage.show();
        });
        
        layout.getChildren().addAll(titleLabel,rating, reviewList, backButton);
        
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviews");
    }

    
    // Refreshes the list of questions displayed in the UI.
    private void refreshReviewList() {
        reviewList.getItems().clear();
        List<Review> reviews = reviewManager.getReviewsFromUser(reviewer);
        for (Review r : reviews) {
            HBox row = new HBox(10);
            Label reviewLabel = new Label(r.getText());
            // Make question label click-able
            reviewLabel.setOnMouseClicked(event -> {
            	// ? could go to answer?
            	
            });
            
            row.getChildren().addAll(reviewLabel);
            reviewList.getItems().add(row);
        }
    }

    // Displays a popup window to edit an existing question.
	// Only allows the original author to make changes.
    /*private void showEditPopup(Question question) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Edit Question");

        VBox layout = new VBox(10);
        TextField editField = new TextField(question.getText());
        Button saveButton = new Button("Save");

        saveButton.setOnAction(e -> {
            String newText = editField.getText().trim();
            if (!newText.isEmpty() && newText.length() <= 500) {
                if (questionManager.editQuestion(question.getId(), newText, user.getUserName())) {
                    refreshQuestionList();
                    popupStage.close();
                }
            }
        });

        layout.getChildren().addAll(new Label("Edit your question:"), editField, saveButton);
        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }*/
}