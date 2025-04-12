package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * StaffChatPage provides a simple group chat interface for staff.
 * Messages typed in the input field are appended to a shared chat area.
 */
public class StaffChatPage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    public StaffChatPage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Staff Chat Room");
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(300);

        TextField messageField = new TextField();
        messageField.setPromptText("Enter message...");
        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> {
            String msg = user.getUserName() + ": " + messageField.getText().trim() + "\n";
            chatArea.appendText(msg);
            messageField.clear();
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(databaseHelper, user).show(primaryStage));

        layout.getChildren().addAll(title, chatArea, messageField, sendButton, backButton);

        primaryStage.setScene(new Scene(layout, 500, 400));
        primaryStage.setTitle("Staff Chat");
        primaryStage.show();
    }
}