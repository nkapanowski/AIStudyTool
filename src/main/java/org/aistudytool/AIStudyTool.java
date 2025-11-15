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
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AIStudyTool extends Application {

    private Timeline timeline;
    private int timeRemaining = 1500;
    private Label timerLabel;
    private Label modeLabel;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0a0a;");
        VBox left = createLeftPanel();
        VBox right = createRightPanel();

        root.setLeft(left);
        root.setCenter(right);

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/css/studytool.css").toExternalForm());

        primaryStage.setTitle("AI Study Assistant");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.show();
    }

    //creates the left panel
    private VBox createLeftPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        panel.setPrefWidth(450);
        panel.setStyle("-fx-background-color: #0a0a0a;");

        VBox tasksSection = createTasksSection();
        VBox.setVgrow(tasksSection, Priority.ALWAYS);

        VBox timerSection = createTimerSection();

        panel.getChildren().addAll(tasksSection, timerSection);
        return panel;
    }

    // @TODO add a array that takes user input and adds new task item that can be added or deleated
    private VBox createTasksSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.getStyleClass().add("panel");
        VBox.setVgrow(section, Priority.ALWAYS);

        Label title = new Label("Tasks");
        title.getStyleClass().add("title");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        TextField taskInput = new TextField();
        taskInput.setPromptText("New Task...");
        taskInput.getStyleClass().add("input");
        HBox.setHgrow(taskInput, Priority.ALWAYS);

        Button addButton = new Button("+");
        addButton.getStyleClass().addAll("button", "add-btn");

        inputBox.getChildren().addAll(taskInput, addButton);
        section.getChildren().addAll(title, inputBox);
        return section;
    }

    //creates the timer
    private VBox createTimerSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(25));
        section.setAlignment(Pos.CENTER);
        section.getStyleClass().add("panel");

        timerLabel = new Label("25:00");
        timerLabel.getStyleClass().add("timer-lbl");

        modeLabel = new Label("Study");
        modeLabel.getStyleClass().add("mode-lbl");

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button studyButton = new Button("Study");
        studyButton.getStyleClass().add("button");
        studyButton.setOnAction(e -> startTimer());

        Button breakButton = new Button("Break");
        breakButton.getStyleClass().add("button");
        breakButton.setOnAction(e -> startBreak());

        buttons.getChildren().addAll(studyButton, breakButton);
        section.getChildren().addAll(timerLabel, modeLabel, buttons);
        return section;
    }

    //starts the timer
    private void startTimer() {
        if (timeline != null) timeline.stop();
        timeRemaining = 1500;
        modeLabel.setText("Study");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText(String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60));
            if (timeRemaining <= 0) {
                timeline.stop();
                startBreak();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //starts the break timer
    private void startBreak() {
        if (timeline != null) timeline.stop();
        timeRemaining = 300;
        modeLabel.setText("Break");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText(String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60));
            if (timeRemaining <= 0) {
                timeline.stop();
                timeRemaining = 1500;
                timerLabel.setText("25:00");
                modeLabel.setText("Study");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //@TODO need to add a mode selector for flash card, Q&A, and Learn.
    //creates the right panel with input, send button, and output box
    private VBox createRightPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25, 25, 25, 3));
        panel.setStyle("-fx-background-color: #0a0a0a;");
        VBox.setVgrow(panel, Priority.ALWAYS);

        VBox chatBox = new VBox(15);
        chatBox.setPadding(new Insets(10));
        chatBox.getStyleClass().add("panel");
        VBox.setVgrow(chatBox, Priority.ALWAYS);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label chatTitle = new Label("AI Study Assistant");
        chatTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(chatTitle, spacer);

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.getStyleClass().add("chat-area");
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        chatBox.getChildren().addAll(header, chatArea);

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(12));
        inputBox.getStyleClass().add("panel");

        TextField questionInput = new TextField();
        questionInput.setPromptText("");
        questionInput.getStyleClass().add("input");
        questionInput.setPrefHeight(40);
        HBox.setHgrow(questionInput, Priority.ALWAYS);

        Button sendButton = new Button("↑");
        sendButton.getStyleClass().addAll("button", "send-btn");

        inputBox.getChildren().addAll(questionInput, sendButton);
        panel.getChildren().addAll(chatBox, inputBox);
        return panel;
    }
}