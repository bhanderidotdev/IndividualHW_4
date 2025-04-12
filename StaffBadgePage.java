package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * StaffBadgePage allows a staff user to “superlike” an answer.
 * It requires the answer ID as input and then calls a new method in DatabaseHelper.
 */
public class StaffBadgePage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    public StaffBadgePage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label instruction = new Label("Enter Answer ID to Superlike:");
        TextField answerIdField = new TextField();
        Button superlikeButton = new Button("Superlike");
        Label statusLabel = new Label();

        superlikeButton.setOnAction(e -> {
            try {
                int answerId = Integer.parseInt(answerIdField.getText().trim());
                boolean success = databaseHelper.setSuperlike(answerId);
                if (success) {
                    statusLabel.setText("Answer " + answerId + " has been superliked!");
                } else {
                    statusLabel.setText("Failed to superlike Answer " + answerId);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid Answer ID.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(databaseHelper, user).show(primaryStage));

        layout.getChildren().addAll(instruction, answerIdField, superlikeButton, statusLabel, backButton);

        primaryStage.setScene(new Scene(layout, 400, 300));
        primaryStage.setTitle("Superlike Answer");
        primaryStage.show();
    }
}