package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import application.MessagePage;

/**
 * ReviewerHomePage represents the home page for reviewer users.
 * This page provides options for changing the password, accessing the review queue,
 * viewing inbox messages, and logging out.
 */
public class ReviewerHomePage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    /**
     * Constructs a ReviewerHomePage with the specified database helper and reviewer user.
     * 
     * @param databaseHelper the helper used for database operations
     * @param user the reviewer user
     */
    public ReviewerHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }
    
    /**
     * Displays the reviewer home page in the given primary stage.
     * 
     * @param primaryStage the stage on which to display the home page
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label welcomeLabel = new Label("Hello, " + user.getUserName() + " (Reviewer)!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> {
            ChangePasswordPage changePasswordPage = new ChangePasswordPage(databaseHelper, user);
            changePasswordPage.show(primaryStage);
        });
        
        Button reviewQueueButton = new Button("Review Queue");
        reviewQueueButton.setOnAction(e -> new ReviewQueuePage(databaseHelper, user, primaryStage).show());
        
        Button viewMessagesButton = new Button("View Inbox");
        viewMessagesButton.setOnAction(e -> {
            MessagePage messagePage = new MessagePage(databaseHelper, user, primaryStage);
            messagePage.show();
        });
        
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            welcomeLoginPage.show(primaryStage, user);
        });
        
        layout.getChildren().addAll(welcomeLabel, changePasswordButton, reviewQueueButton, viewMessagesButton, logoutButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer Home");
    }
}
