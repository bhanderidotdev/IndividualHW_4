package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class StaffFunctionsPage {

    private final DatabaseHelper databaseHelper;

    public StaffFunctionsPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;

        if (!user.getRole().equalsIgnoreCase("staff")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText(null);
            alert.setContentText("Access Denied. You are not a staff member.");
            alert.showAndWait();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Staff Functions");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Staff Tools and Features");

        Button reviewQueueButton = new Button("Review Queue");
        Button flagUserButton = new Button("Flag User");
        Button scheduleMeetingButton = new Button("Schedule Meeting");
        Button groupMessageButton = new Button("Group Messaging");
        Button staffChatButton = new Button("Staff Chat");

        reviewQueueButton.setOnAction(e -> new ReviewQueuePage(databaseHelper, user, new Stage()));
        flagUserButton.setOnAction(e -> new StaffFlagUserPage(databaseHelper, user, new Stage()));
        scheduleMeetingButton.setOnAction(e -> new MeetingSchedulerPage(databaseHelper, user, new Stage()));
        groupMessageButton.setOnAction(e -> new GroupMessagingPage(databaseHelper, user, new Stage()));
        staffChatButton.setOnAction(e -> new StaffChatPage(databaseHelper, user, new Stage()));

        layout.getChildren().addAll(
            titleLabel,
            reviewQueueButton,
            flagUserButton,
            scheduleMeetingButton,
            groupMessageButton,
            staffChatButton
        );

        Scene scene = new Scene(layout, 400, 350);
        stage.setScene(scene);
        stage.show();
    }
}