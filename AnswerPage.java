package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.util.List;
import databasePart1.DatabaseHelper;

/**
 * The AnswerPage class allows users to create, view, edit, and delete answers for a specific question.
 * Students can also view reviews for each answer using the "View Reviews" button.
 */
public class AnswerPage {
    private final AnswerManager answerManager;
    private final User user;
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final Question question;
    private ListView<HBox> answerList;

    public AnswerPage(AnswerManager answerManager, User user, DatabaseHelper databaseHelper, Stage primaryStage, Question question) {
        this.answerManager = answerManager;
        this.user = user;
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        this.question = question;
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("Manage Answers for: " + question.getText());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField answerField = new TextField();
        answerField.setPromptText("Enter your answer...");

        Button submitButton = new Button("Submit Answer");
        Label messageLabel = new Label();

        answerList = new ListView<>();
        refreshAnswerList();

        submitButton.setOnAction(e -> {
            String text = answerField.getText().trim();
            if (!text.isEmpty() && text.length() <= 500) {
                Answer newAnswer = answerManager.createAnswer(answerManager.getAnswersForQuestion(question.getId()).size() + 1, text, user.getUserName(), question.getId());
                if (newAnswer != null) {
                    answerManager.saveAnswer(newAnswer);
                    refreshAnswerList();
                    messageLabel.setText("Answer added successfully!");
                }
                answerField.clear();
            } else {
                messageLabel.setText("Invalid answer. Ensure it's not empty and under 500 characters.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            QuestionPage questionPage = new QuestionPage(new QuestionManager(databaseHelper), user, databaseHelper, primaryStage);
            questionPage.show();
        });

        layout.getChildren().addAll(titleLabel, answerField, submitButton, messageLabel, answerList, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Answer Management");
        primaryStage.toFront();
    }

    private void refreshAnswerList() {
        answerList.getItems().clear();
        List<Answer> answers = answerManager.getAnswersForQuestion(question.getId());
        for (Answer a : answers) {
            HBox row = new HBox(10);

            String displayText = a.getText();
            Label answerLabel;
            if (a.isSuperlike()) {
                displayText += "  ⭐️ Highlighted by Staff";
                answerLabel = new Label(displayText);
                answerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a5d84;");
            } else {
                answerLabel = new Label(displayText);
            }

            Button editButton = new Button("Edit");
            editButton.setDisable(!a.getAuthor().equals(user.getUserName()));
            editButton.setOnAction(e -> showEditPopup(a));

            Button deleteButton = new Button("Delete");
            boolean isAdmin = "admin".equals(user.getRole());
            deleteButton.setDisable(!a.getAuthor().equals(user.getUserName()) && !isAdmin);
            deleteButton.setOnAction(e -> {
                if (answerManager.deleteAnswer(a.getId(), user.getUserName(), isAdmin)) {
                    refreshAnswerList();
                }
            });

            Button viewReviewsButton = new Button("View Reviews");
            viewReviewsButton.setOnAction(e -> {
                StudentReviewPopup reviewPopup = new StudentReviewPopup(databaseHelper, a.getId());
                reviewPopup.show();
            });

            Button viewReviewerButton = new Button("See Reviewer");
            User author = new User(a.getAuthor(), databaseHelper);
            viewReviewerButton.setDisable(!author.getRole().equals("reviewer"));
            viewReviewerButton.setOnAction(e -> {
                ReviewerPage reviewPage = new ReviewerPage(user, a.getAuthor(), databaseHelper, primaryStage, question);
                reviewPage.show();
            });

            row.getChildren().addAll(answerLabel, editButton, deleteButton, viewReviewsButton, viewReviewerButton);
            answerList.getItems().add(row);
        }
    }

    private void showEditPopup(Answer answer) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Edit Answer");

        VBox layout = new VBox(10);
        TextField editField = new TextField(answer.getText());
        Button saveButton = new Button("Save");

        saveButton.setOnAction(e -> {
            String newText = editField.getText().trim();
            if (!newText.isEmpty() && newText.length() <= 500) {
                if (answerManager.updateAnswer(answer.getId(), newText, user.getUserName())) {
                    refreshAnswerList();
                    popupStage.close();
                }
            }
        });

        layout.getChildren().addAll(new Label("Edit your answer:"), editField, saveButton);
        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
