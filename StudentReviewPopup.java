package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.util.List;

/**
 * StudentReviewPopup displays the reviews for a specific answer.
 */
public class StudentReviewPopup {
    private final DatabaseHelper databaseHelper;
    private final int answerId;
    
    /**
     * Constructs a StudentReviewPopup.
     * @param databaseHelper the database helper instance
     * @param answerId the ID of the answer for which reviews are displayed
     */
    public StudentReviewPopup(DatabaseHelper databaseHelper, int answerId) {
        this.databaseHelper = databaseHelper;
        this.answerId = answerId;
    }
    
    /**
     * Displays a popup window listing the reviews for the answer.
     */
    public void show() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Reviews for Answer ID: " + answerId);
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Reviews:");
        ListView<String> reviewList = new ListView<>();
        
        ReviewManager rm = new ReviewManager(databaseHelper);
        List<Review> reviews = rm.getReviewsForAnswer(answerId);
        if (reviews.isEmpty()) {
            reviewList.getItems().add("No reviews available.");
        } else {
            for (Review r : reviews) {
                reviewList.getItems().add(r.toString());
            }
        }
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        
        layout.getChildren().addAll(titleLabel, reviewList, closeButton);
        Scene scene = new Scene(layout, 400, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
