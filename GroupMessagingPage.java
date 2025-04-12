package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * GroupMessagingPage allows staff to create groups and send a message to multiple users.
 * (For this implementation, the functionality is simulated by printing to the console.)
 */
public class GroupMessagingPage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    public GroupMessagingPage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Group Messaging");

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Enter group name");

        TextField membersField = new TextField();
        membersField.setPromptText("Enter comma-separated usernames");

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter message to group");

        Button sendButton = new Button("Send Group Message");
        Label statusLabel = new Label();

        sendButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            String members = membersField.getText().trim();
            String message = messageArea.getText().trim();

            if (groupName.isEmpty() || members.isEmpty() || message.isEmpty()) {
                statusLabel.setText("All fields required.");
            } else {
                // Simulate group messaging by printing the message details.
                System.out.println("Group: " + groupName + ", Members: " + members + ", Message: " + message);
                statusLabel.setText("Group message sent.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(databaseHelper, user).show(primaryStage));

        layout.getChildren().addAll(title, groupNameField, membersField, messageArea, sendButton, statusLabel, backButton);

        primaryStage.setScene(new Scene(layout, 500, 400));
        primaryStage.setTitle("Group Messaging");
        primaryStage.show();
    }
}