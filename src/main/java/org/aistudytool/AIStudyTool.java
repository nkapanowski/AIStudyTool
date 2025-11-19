package org.aistudytool;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AIStudyTool extends Application {
    private VBox taskListContainer;
    private TaskManager taskManager;
    private PomodoroTimer pomodoroTimer;
    private Label timerLabel;
    private Label modeLabel;
    private User currentUser;
    private UserRepo userRepo;

    public AIStudyTool() {
        // Default constructor for initial launch
    }
    
    public AIStudyTool(User user) {
        this.currentUser = user;
        this.userRepo = new UserRepo();
    }

    @Override
public void start(Stage primaryStage) {
        if (currentUser == null) {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(loginView.createLoginScene());
            primaryStage.setTitle("AI Study Tool - Login");
            primaryStage.show();
            return;
        }

    try {
        System.out.println("=== Creating UI ===");
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0a0a;");
        VBox left = createLeftPanel();
        VBox right = createRightPanel();
        
        System.out.println("Left panel: " + left);
        System.out.println("Right panel: " + right);
        
        root.setLeft(left);
        root.setCenter(right);

        Scene scene = new Scene(root, 1000, 650);
        
        try {
            String css = getClass().getResource("/CSS/studytool.css").toExternalForm();
            scene.getStylesheets().add(css);
            System.out.println("CSS loaded: " + css);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String css = this.getClass().getResource("/CSS/studytool.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("AI Study Assistant");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("=== UI Created Successfully ===");
        
    } catch (Exception e) {
        System.out.println("ERROR IN START METHOD:");
        e.printStackTrace();
    }
}

    /*@Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        VBox left = createLeftPanel();
        VBox right = createRightPanel();

        root.setLeft(left);
        root.setCenter(right);

        Scene scene = new Scene(root, 1000, 650);

        try {
                String css = getClass().getResource("/CSS/studytool.css").toExternalForm();
                System.out.println("CSS URL = " + css);
                if (css == null) throw new IllegalStateException("CSS missing!");
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("CSS FAILED:");
                e.printStackTrace();
            }

            root.setStyle("-fx-background-color: #ff00ff;");  // DARK GRAY

            primaryStage.setTitle("AI Study Assistant");
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNIFIED);
            primaryStage.show();
        }*/

    //creates the left panel
    private VBox createLeftPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        panel.setPrefWidth(450);

        VBox tasksSection = createTasksSection();
        VBox.setVgrow(tasksSection, Priority.ALWAYS);

        VBox timerSection = createTimerSection();

        panel.getChildren().addAll(tasksSection, timerSection);
        return panel;
    }

    //adds tasks through user input stores them in a list
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

        taskListContainer = new VBox(10);
        taskListContainer.setPadding(new Insets(10, 0, 0, 0));

        inputBox.getChildren().addAll(taskInput, addButton); 

        ScrollPane scrollPane = new ScrollPane(taskListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        section.getChildren().addAll(title, inputBox, scrollPane);
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

        pomodoroTimer = new PomodoroTimer(timerLabel, modeLabel);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button studyButton = new Button("Study");
        studyButton.getStyleClass().add("button");
        studyButton.setOnAction(e -> pomodoroTimer.startTimer());

        Button breakButton = new Button("Break");
        breakButton.getStyleClass().add("button");
        breakButton.setOnAction(e -> pomodoroTimer.startBreak());

        buttons.getChildren().addAll(studyButton, breakButton);
        section.getChildren().addAll(timerLabel, modeLabel, buttons);
        return section;
    }

    //@TODO need to add a mode selector for flash card, Q&A, and Learn.
    //creates the right panel with input, send button, and output box
    private VBox createRightPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25, 25, 25, 3));
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