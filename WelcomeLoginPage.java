package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.DatabaseHelper;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
    
    private final DatabaseHelper databaseHelper;

    /**
     * Constructor to initialize the WelcomeLoginPage.
     * @param databaseHelper the database helper instance
     */
    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Displays the welcome page in the provided primary stage.
     * Navigates to a specific home page based on the user's role.
     * @param primaryStage the primary stage for the scene
     * @param user the authenticated user
     */
    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label welcomeLabel = new Label("Welcome!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to navigate to the user's respective page based on their role
        Button continueButton = new Button("Continue to your Page");
        continueButton.setOnAction(a -> {
            String role = user.getRole();
            System.out.println("User role: " + role);
            
            if (role.trim().equalsIgnoreCase("admin")) {
                new AdminHomePage(databaseHelper, user).show(primaryStage);
            } else if (role.trim().equalsIgnoreCase("reviewer")) {
                new ReviewerHomePage(databaseHelper, user).show(primaryStage);
            } else if (role.trim().equalsIgnoreCase("staff")) {
                new StaffHomePage(databaseHelper, user).show(primaryStage);
            } else if (role.trim().equalsIgnoreCase("user")) {
                new UserHomePage(databaseHelper, user).show(primaryStage);
            } else {
                // Default to user home page if role does not match any known roles.
                new UserHomePage(databaseHelper, user).show(primaryStage);
            }
        });
        
        // Button to quit the application
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit(); // Exit the JavaFX application
        });
        
        // "Invite" button for admin to generate invitation codes
        if ("admin".equalsIgnoreCase(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }
        
        layout.getChildren().addAll(welcomeLabel, continueButton, quitButton);
        Scene welcomeScene = new Scene(layout, 800, 400);
        
        // Set the scene to primary stage
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome Page");
    }
}
