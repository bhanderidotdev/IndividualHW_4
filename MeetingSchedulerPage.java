package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * MeetingSchedulerPage allows staff to schedule meetings or office hours.
 * It collects meeting details and then calls a method in DatabaseHelper to store the meeting.
 */
public class MeetingSchedulerPage {
    private final DatabaseHelper databaseHelper;
    private final User user;

    public MeetingSchedulerPage(DatabaseHelper databaseHelper, User user, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.user = user;

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Meeting Scheduler");
        TextField meetingTitleField = new TextField();
        meetingTitleField.setPromptText("Meeting Title");

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("Time (HH:MM)");

        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Meeting details/agenda");

        Button scheduleButton = new Button("Schedule Meeting");
        Label statusLabel = new Label();

        scheduleButton.setOnAction(e -> {
            String meetingTitle = meetingTitleField.getText().trim();
            String time = timeField.getText().trim();
            String details = detailsArea.getText().trim();
            if (meetingTitle.isEmpty() || datePicker.getValue() == null || time.isEmpty()) {
                statusLabel.setText("Meeting title, date, and time required.");
            } else {
                boolean success = databaseHelper.scheduleMeeting(meetingTitle, datePicker.getValue().toString(), time, details);
                if (success) {
                    statusLabel.setText("Meeting scheduled successfully.");
                } else {
                    statusLabel.setText("Failed to schedule meeting.");
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(databaseHelper, user).show(primaryStage));

        layout.getChildren().addAll(title, meetingTitleField, datePicker, timeField, detailsArea, scheduleButton, statusLabel, backButton);
        primaryStage.setScene(new Scene(layout, 500, 500));
        primaryStage.setTitle("Meeting Scheduler");
        primaryStage.show();
    }
}