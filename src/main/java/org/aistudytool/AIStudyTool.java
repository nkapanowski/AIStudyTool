package org.aistudytool;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AIStudyTool extends Application {

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
    //creates the left panel
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
    //creates the task section within the left panel
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
    //creates the timer section within the left panel
    private VBox createTimerSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setAlignment(javafx.geometry.Pos.CENTER);
        section.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #444; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label timerLabel = new Label("25:00");
        timerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label modeLabel = new Label("Study");
        modeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ccc;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

        Button stopButton = new Button("Stop");
        stopButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

        buttons.getChildren().addAll(startButton, stopButton);

        section.getChildren().addAll(timerLabel, modeLabel, buttons);
        return section;
    }
    //creates the right panel which only contains input and output screen for now will change later
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