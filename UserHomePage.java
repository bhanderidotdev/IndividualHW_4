package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * This page displays a welcome message and provides navigation to various functionalities.
 */
public class UserHomePage {
    private final DatabaseHelper databaseHelper;
    private final User user;
    private final QuestionManager questionManager;

    public UserHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
        this.questionManager = new QuestionManager(databaseHelper);
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label userLabel = new Label("Hello, " + user.getUserName() + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to navigate to Change Password page.
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> {
            ChangePasswordPage changePasswordPage = new ChangePasswordPage(databaseHelper, user);
            changePasswordPage.show(primaryStage);
        });
       
        // Button to navigate to Question Management page.
        Button manageQuestionsButton = new Button("Manage Questions");
        manageQuestionsButton.setOnAction(e -> {
            QuestionPage questionPage = new QuestionPage(questionManager, user, databaseHelper, primaryStage);
            questionPage.show();
        });
        
        // Button to navigate to Search Q&A page.
        Button searchButton = new Button("Search Q&A");
        searchButton.setOnAction(e -> {
            SearchPage searchPage = new SearchPage(user, databaseHelper, primaryStage);
            searchPage.show();
        });
        
        // New button: View Inbox for messages.
        Button viewMessagesButton = new Button("View Inbox");
        viewMessagesButton.setOnAction(e -> {
            MessagePage messagePage = new MessagePage(databaseHelper, user, primaryStage);
            messagePage.show();
        });
        
        // New button: Request Reviewer Role.
        Button reviewerRequestButton = new Button("Request Reviewer Role");
        reviewerRequestButton.setOnAction(e -> {
            ReviewerRequestPage requestPage = new ReviewerRequestPage(databaseHelper, user, primaryStage);
            requestPage.show();
        });
        
        layout.getChildren().addAll(userLabel, changePasswordButton, manageQuestionsButton, searchButton, viewMessagesButton, reviewerRequestButton);
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Home");
    }
}
