package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * StaffFlagUserPage allows a staff user to manually flag a user who may need help.
 * For demonstration purposes, the method flagUser sets the flagged user’s rating to a special value.
 */
public class StaffFlagUserPage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    public StaffFlagUserPage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label instruction = new Label("Enter username to flag for help:");
        TextField userField = new TextField();
        Button flagButton = new Button("Flag User");
        Label statusLabel = new Label();

        flagButton.setOnAction(e -> {
            String flagUser = userField.getText().trim();
            if (flagUser.isEmpty()) {
                statusLabel.setText("Enter a username.");
            } else {
                boolean success = databaseHelper.flagUser(flagUser);
                if (success) {
                    statusLabel.setText("User " + flagUser + " flagged for help.");
                } else {
                    statusLabel.setText("Failed to flag user " + flagUser);
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(databaseHelper, user).show(primaryStage));

        layout.getChildren().addAll(instruction, userField, flagButton, statusLabel, backButton);
        primaryStage.setScene(new Scene(layout, 400, 300));
        primaryStage.setTitle("Flag User");
        primaryStage.show();
    }
}