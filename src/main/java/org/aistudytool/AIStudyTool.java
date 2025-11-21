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
   private static User currentUser;
    private UserRepo userRepo;

    public static void setCurrentUser(User user) {
    currentUser = user;
}

    public AIStudyTool() {
        this.userRepo = new UserRepo();
    }

    @Override
    public void start(Stage primaryStage) {
    // Check if user is logged in
    if (currentUser == null) {
        System.err.println("ERROR: No user logged in!");
        return;
    }
    
    // Initialize TaskManager with current user
    this.taskManager = new TaskManager(currentUser.getUid());

    try {
        System.out.println("=== Creating UI for user: " + currentUser.getEmail() + " ===");
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0a0a;");
        VBox left = createLeftPanel();
        VBox right = createRightPanel();
        
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

        primaryStage.setTitle("AI Study Assistant");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("=== UI Created Successfully ===");
        
    } catch (Exception e) {
        System.out.println("ERROR IN START METHOD:");
        e.printStackTrace();
    }
}

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

        taskManager.setTaskListContainer(taskListContainer);
        taskManager.loadTasksToUI(); // Load existing tasks from database
    
        addButton.setOnAction(e -> taskManager.addTask(taskInput));
        taskInput.setOnAction(e -> taskManager.addTask(taskInput));

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

            // ADD MODE SELECTOR
    HBox modeSelector = new HBox(10);
    modeSelector.setAlignment(Pos.CENTER_RIGHT);
    
    ToggleGroup modeGroup = new ToggleGroup();
    
    ToggleButton learnMode = new ToggleButton("Learn");
    learnMode.setToggleGroup(modeGroup);
    learnMode.setSelected(true);
    learnMode.getStyleClass().add("mode-button");
    
    ToggleButton qaMode = new ToggleButton("Q&A");
    qaMode.setToggleGroup(modeGroup);
    qaMode.getStyleClass().add("mode-button");
    
    ToggleButton flashcardMode = new ToggleButton("Flashcard");
    flashcardMode.setToggleGroup(modeGroup);
    flashcardMode.getStyleClass().add("mode-button");
    
    modeSelector.getChildren().addAll(learnMode, qaMode, flashcardMode);

    header.getChildren().addAll(chatTitle, spacer, modeSelector);

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
    questionInput.setPromptText("Ask me anything about your studies...");
    questionInput.getStyleClass().add("input");
    questionInput.setPrefHeight(40);
    HBox.setHgrow(questionInput, Priority.ALWAYS);

    Button sendButton = new Button("↑");
    sendButton.getStyleClass().addAll("button", "send-btn");

    // AI SERVICE
    AIService aiService = new AIService();

    // Send button action with mode detection
    sendButton.setOnAction(e -> {
        String question = questionInput.getText().trim();
        if (!question.isEmpty()) {
            chatArea.appendText("You: " + question + "\n\n");
            questionInput.clear();
            sendButton.setDisable(true);
            
            // Get selected mode
            String mode = ((ToggleButton) modeGroup.getSelectedToggle()).getText();
            String prompt = buildPrompt(question, mode);
            
            aiService.askQuestion(prompt, new AIService.AICallback() {
    @Override
    public void onSuccess(String response) {
        chatArea.appendText("AI (" + mode + "): " + response + "\n\n");
        chatArea.appendText("─────────────────────────\n\n");
        
        // Auto-save flashcard if in Flashcard mode
        if (mode.equals("Flashcard")) {
            parseAndSaveFlashcard(response, question);
        }
        
        sendButton.setDisable(false);
        chatArea.setScrollTop(Double.MAX_VALUE);
    }
    
    @Override
    public void onFailure(String error) {
        chatArea.appendText("Error: " + error + "\n\n");
        sendButton.setDisable(false);
    }
});
        }
    });

    questionInput.setOnAction(e -> sendButton.fire());

    inputBox.getChildren().addAll(questionInput, sendButton);
    panel.getChildren().addAll(chatBox, inputBox);
    return panel;
}

private void parseAndSaveFlashcard(String response, String topic) {
    try {
        String[] parts = response.split("\n");
        String question = "";
        String answer = "";
        
        for (String line : parts) {
            if (line.trim().startsWith("Q:")) {
                question = line.substring(2).trim();
            } else if (line.trim().startsWith("A:")) {
                answer = line.substring(2).trim();
            }
        }
        
        if (!question.isEmpty() && !answer.isEmpty()) {
            taskManager.addFlashcard(question, answer); // Use taskManager instead
            System.out.println("✓ Flashcard saved successfully!");
        }
    } catch (Exception e) {
        System.out.println("Error saving flashcard: " + e.getMessage());
    }
}

// ADD THIS HELPER METHOD
private String buildPrompt(String question, String mode) {
    switch (mode) {
        case "Learn":
            return "Explain this concept in detail for a student: " + question;
        case "Q&A":
            return "Answer this question directly and concisely: " + question;
        case "Flashcard":
            return "Create a flashcard for studying. Format your response EXACTLY like this:\n\n" +
                   "Q: [Write a clear question about: " + question + "]\n" +
                   "A: [Write a concise answer]\n\n" +
                   "Keep it simple and focused on one concept.";
        default:
            return question;
    }
}
}