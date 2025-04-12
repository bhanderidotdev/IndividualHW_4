package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * AdminHomePage class represents the user interface for the admin user.
 * This page displays a welcome message for the admin and provides navigation buttons,
 * including a new "Staff Functions" button that shows all staff-related functionalities.
 */
public class AdminHomePage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    /**
     * Constructor to initialize the AdminHomePage.
     * @param databaseHelper the database helper instance
     * @param user the admin user
     */
    public AdminHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }
    
    /**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage the primary stage where the scene will be displayed
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to navigate to Change Password page
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> {
            ChangePasswordPage changePasswordPage = new ChangePasswordPage(databaseHelper, user);
            changePasswordPage.show(primaryStage);
        });
        
        // Button to navigate to Reviewer Requests page
        Button reviewerApprovalButton = new Button("Reviewer Requests");
        reviewerApprovalButton.setOnAction(e -> {
            InstructorReviewerApprovalPage approvalPage = new InstructorReviewerApprovalPage(databaseHelper, user, primaryStage);
            approvalPage.show();
        });
        
        // New Staff Functions button: When clicked, it opens the StaffHomePage.
        Button staffFunctionsButton = new Button("Staff Functions");
        staffFunctionsButton.setOnAction(e -> {
            // Navigate to the StaffHomePage where all staff user stories are implemented.
            new StaffHomePage(databaseHelper, user).show(primaryStage);
        });
        
        layout.getChildren().addAll(adminLabel, changePasswordButton, reviewerApprovalButton, staffFunctionsButton);
        Scene adminScene = new Scene(layout, 800, 400);
        
        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
}
