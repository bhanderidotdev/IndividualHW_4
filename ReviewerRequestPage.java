package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class ReviewerRequestPage {
    private final DatabaseHelper databaseHelper;
    private final User user;
    private final Stage primaryStage;
    private final ReviewerRequestManager requestManager;
    
    public ReviewerRequestPage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;
        this.primaryStage = primaryStage;
        this.requestManager = new ReviewerRequestManager(databaseHelper);
    }
    
    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Request Reviewer Role");
        Label statusLabel = new Label("Current Request Status: " + requestManager.getRequestStatus(user.getUserName()));
        Button submitButton = new Button("Submit Request");
        Button backButton = new Button("Back");
        
        submitButton.setOnAction(e -> {
            requestManager.submitRequest(user.getUserName());
            statusLabel.setText("Request submitted. Awaiting approval.");
        });
        
        backButton.setOnAction(e -> {
            UserHomePage homePage = new UserHomePage(databaseHelper, user);
            homePage.show(primaryStage);
        });
        
        layout.getChildren().addAll(titleLabel, statusLabel, submitButton, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer Request");
    }
}
