package org.aistudytool;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class AIStudyTool extends Application {

    private Timeline timeline;
    private int timeRemaining = 1500;
    private Label timerLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a1a;");

        VBox left = createLeftPanel();
        VBox right = createRightPanel();

        BorderPane.setMargin(right, new Insets(0, 0, 0, 5));

        root.setLeft(left);
        root.setCenter(right);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("AI Study Assistant");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //creates the left pane
    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20, 10, 20, 20));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #1a1a1a;");

        VBox tasksSection = createTasksSection();
        VBox.setVgrow(tasksSection, Priority.ALWAYS);

        VBox timerSection = createTimerSection();

        panel.getChildren().addAll(tasksSection, timerSection);
        return panel;
    }
    //creates task section
    //TODO:Need to create a array that stores tasks that the user inputs/ add delete buttons need to get rid of tasks
    private VBox createTasksSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #444; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label title = new Label("Tasks");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Add a task...");
        taskInput.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-prompt-text-fill: #888;");

        Button addButton = new Button("Add Task");
        addButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

        VBox taskList = new VBox(10);

        section.getChildren().addAll(title, taskInput, addButton, taskList);
        return section;
    }
    //creates timer section
    private VBox createTimerSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setAlignment(Pos.CENTER);
        section.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #444; -fx-border-radius: 5; -fx-background-radius: 5;");

        timerLabel = new Label("25:00");
        timerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label modeLabel = new Label("Study");
        modeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ccc;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
        startButton.setOnAction(e -> startTimer());

        Button stopButton = new Button("Stop");
        stopButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
        stopButton.setOnAction(e -> stopTimer());

        buttons.getChildren().addAll(startButton, stopButton);

        section.getChildren().addAll(timerLabel, modeLabel, buttons);
        return section;
    }
    //sets timer to 25 minutes updates the ui if the timer goes to zero timer is set to 5 minutes.
    //TODO: need to make the modelabel update when changing from 25 minutes to 5 minutes.
    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }

        timeRemaining = 1500;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;

            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;

            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

            if (timeRemaining <= 0) {
                timeRemaining = 300;
                timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
                    int minutes2 = timeRemaining / 60;
                    int seconds2 = timeRemaining % 60;
                    timeRemaining--;
                    timerLabel.setText(String.format("%02d:%02d", minutes2, seconds2));
                }));
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        timeRemaining = 1500;
        timerLabel.setText("25:00");
    }

    //Creates the right panel with text input and output
    //TODO: need to create text bubbles for response and for user input showcasing the output to the screen
    private VBox createRightPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #1a1a1a;");

        VBox chatBox = new VBox(10);
        chatBox.setPadding(new Insets(15));
        chatBox.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #444; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: white;");
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        chatBox.getChildren().add(chatArea);
        VBox.setVgrow(chatBox, Priority.ALWAYS);

        VBox inputBox = new VBox(10);
        inputBox.setPadding(new Insets(15));
        inputBox.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #444; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField questionInput = new TextField();
        questionInput.setPromptText("...");
        questionInput.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-prompt-text-fill: #888;");

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

        inputBox.getChildren().addAll(questionInput, sendButton);

        panel.getChildren().addAll(chatBox, inputBox);
        return panel;
    }
}