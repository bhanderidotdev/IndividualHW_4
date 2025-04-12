package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * StaffHomePage is the main page for users with the staff role.
 * It provides navigation buttons for staff functionalities with improved layout and naming.
 */
public class StaffHomePage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    public StaffHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(15); // more spacing for cleaner look
        layout.setStyle("-fx-alignment: center; -fx-padding: 30;");

        Label welcomeLabel = new Label("Welcome, " + user.getUserName() + " (Staff)");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button highlightButton = new Button("Highlight Answer");
        highlightButton.setOnAction(e -> new StaffBadgePage(databaseHelper, user, primaryStage));

        Button internalChatButton = new Button("Internal Chat");
        internalChatButton.setOnAction(e -> new StaffChatPage(databaseHelper, user, primaryStage));

        Button helpFlagButton = new Button("Mark User for Help");
        helpFlagButton.setOnAction(e -> new StaffFlagUserPage(databaseHelper, user, primaryStage));

        Button groupMsgButton = new Button("Message User Group");
        groupMsgButton.setOnAction(e -> new GroupMessagingPage(databaseHelper, user, primaryStage));

        Button planMeetingButton = new Button("Plan Staff Meeting");
        planMeetingButton.setOnAction(e -> new MeetingSchedulerPage(databaseHelper, user, primaryStage));

        Button logoutButton = new Button("Sign Out");
        logoutButton.setOnAction(e -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));

        layout.getChildren().addAll(
            welcomeLabel,
            highlightButton,
            internalChatButton,
            helpFlagButton,
            groupMsgButton,
            planMeetingButton,
            logoutButton
        );

        Scene scene = new Scene(layout, 900, 500); // new window size
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Dashboard");
    }
}