package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.util.List;

/**
 * The MessagePage class allows users to view their inbox and reply to messages.
 */
public class MessagePage {
    private final DatabaseHelper databaseHelper;
    private final User user;
    private final Stage primaryStage;
    private final MessageManager messageManager;
    
    /**
     * Constructor to initialize the MessagePage.
     * @param databaseHelper the database helper instance
     * @param user the current user
     * @param primaryStage the primary stage for the UI
     */
    public MessagePage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;
        this.primaryStage = primaryStage;
        this.messageManager = new MessageManager(databaseHelper);
    }
    
    /**
     * Displays the message page, showing the user's inbox and a reply interface.
     */
    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Inbox for " + user.getUserName());
        ListView<HBox> messageList = new ListView<>();
        
        refreshMessageList(messageList);
        
        // Reply area
        TextField replyField = new TextField();
        replyField.setPromptText("Enter your reply...");
        Button sendButton = new Button("Send Reply");
        Label messageLabel = new Label();
        
        sendButton.setOnAction(e -> {
            String replyText = replyField.getText().trim();
            if (!replyText.isEmpty() && replyText.length() <= 200) {
                // For simplicity, assume reply is sent to the sender of the selected message.
                HBox selectedRow = messageList.getSelectionModel().getSelectedItem();
                if (selectedRow != null) {
                    Label senderLabel = (Label) selectedRow.getChildren().get(0);
                    String sender = senderLabel.getText();
                    // Create new message: from current user to sender.
                    int recipientId = databaseHelper.getUserId(sender);
                    Message replyMessage = new Message(0, replyText, user.getUserName());
                    replyMessage.setId(recipientId);
                    messageManager.sendMessage(replyMessage);
                    messageLabel.setText("Reply sent to " + sender + "!");
                    replyField.clear();
                    refreshMessageList(messageList);
                } else {
                    messageLabel.setText("Please select a message to reply to.");
                }
            } else {
                messageLabel.setText("Invalid reply. Must be non-empty and up to 200 characters.");
            }
        });
        
        // Back button now navigates based on user's role.
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Check the user's role and navigate accordingly.
            String role = user.getRole().trim().toLowerCase();
            if (role.equals("reviewer")) {
                new ReviewerHomePage(databaseHelper, user).show(primaryStage);
            } else if (role.equals("admin")) {
                new AdminHomePage(databaseHelper, user).show(primaryStage);
            } else { // default to user home page
                new UserHomePage(databaseHelper, user).show(primaryStage);
            }
        });
        
        layout.getChildren().addAll(titleLabel, messageList, replyField, sendButton, messageLabel, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Messages");
    }
    
    /**
     * Refreshes the inbox message list.
     * @param messageList the ListView to update with current messages
     */
    private void refreshMessageList(ListView<HBox> messageList) {
        messageList.getItems().clear();
        List<Message> messages = messageManager.getMessagesForUser(user.getUserName());
        for (Message msg : messages) {
            HBox row = new HBox(10);
            // Display sender and message text.
            Label senderLabel = new Label(msg.getAuthor());
            Label textLabel = new Label(msg.getText());
            row.getChildren().addAll(senderLabel, textLabel);
            messageList.getItems().add(row);
        }
    }
}
