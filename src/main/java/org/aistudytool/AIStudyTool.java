package org.aistudytool;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AIStudyTool extends Application {
    private VBox taskListContainer;
    private TaskManager taskManager;
    private PomodoroTimer pomodoroTimer;
    private Label timerLabel;
    private Label modeLabel;
    private static User currentUser;
    private UserRepo userRepo;
    private Stage primaryStage;
    private TextArea chatArea;

    public static void setCurrentUser(User user) {
    currentUser = user;
}

    public AIStudyTool() {
        this.userRepo = new UserRepo();
    }

    @Override
    public void start(Stage primaryStage) {
    this.primaryStage = primaryStage; 
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

    //creates the right panel with input, send button, and output box and ai chatbox
    private VBox createRightPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25, 25, 25, 3));
        VBox.setVgrow(panel, Priority.ALWAYS);

        // AI Chat Section (main chat)
        VBox chatBox = createChatSection();
        VBox.setVgrow(chatBox, Priority.ALWAYS);
        
        // Only flashcard section below
        VBox flashcardSection = createFlashcardSection();
        VBox.setVgrow(flashcardSection, Priority.SOMETIMES);

        panel.getChildren().addAll(chatBox, flashcardSection);
        return panel;
    }

    private VBox createChatSection() {
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

        modeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String selectedMode = ((ToggleButton) newToggle).getText();
                loadChatHistoryForMode(selectedMode);
            }
        });

        modeSelector.getChildren().addAll(learnMode, qaMode, flashcardMode);

        Button clearChatBtn = new Button("×");
        clearChatBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #ff4444; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 0 8 0 0;"
        );
        clearChatBtn.setOnAction(e -> {
            ToggleButton selected = (ToggleButton) modeGroup.getSelectedToggle();
            String currentMode = selected.getText();
            chatArea.clear();
            try {
                com.google.cloud.firestore.Firestore db = FirebaseConfig.getFirestore();
                db.collection("chatHistory")
                  .whereEqualTo("userId", currentUser.getUid())
                  .whereEqualTo("mode", currentMode)
                  .get()
                  .get()
                  .getDocuments()
                  .forEach(doc -> doc.getReference().delete());
                System.out.println(currentMode + " chat history cleared");
            } catch (Exception ex) {
                System.out.println("Error clearing history: " + ex.getMessage());
            }
        });

        header.getChildren().addAll(chatTitle, clearChatBtn, spacer, modeSelector);

    TextArea chatArea = new TextArea();
    chatArea.setEditable(false);
    chatArea.setWrapText(true);
    chatArea.getStyleClass().add("chat-area");
    VBox.setVgrow(chatArea, Priority.ALWAYS);

    this.chatArea = chatArea;

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

    AIService aiService = new AIService();

sendButton.setOnAction(e -> {
    String question = questionInput.getText().trim();
    if (!question.isEmpty()) {
        String mode = ((ToggleButton) modeGroup.getSelectedToggle()).getText();
        
        String userMessage = "You: " + question + "\n\n";
        chatArea.appendText(userMessage);
        saveChatHistory(userMessage, mode); 
        
        questionInput.clear();
        sendButton.setDisable(true);
        
        String prompt = buildPrompt(question, mode);
        
        aiService.askQuestion(prompt, new AIService.AICallback() {
            @Override
            public void onSuccess(String response) {
                String aiMessage = "AI (" + mode + "): " + response + "\n\n" +
                                 "─────────────────────────\n\n";
                chatArea.appendText(aiMessage);
                saveChatHistory(aiMessage, mode); 
                
                if (mode.equals("Flashcard")) {
                    parseAndSaveFlashcard(response, question);
                }
                
                sendButton.setDisable(false);
                chatArea.setScrollTop(Double.MAX_VALUE);
            }
            
            @Override
            public void onFailure(String error) {
                String errorMessage = "Error: " + error + "\n\n";
                chatArea.appendText(errorMessage);
                saveChatHistory(errorMessage, mode); 
                sendButton.setDisable(false);
            }
        });
    }
});

    questionInput.setOnAction(e -> sendButton.fire());
    inputBox.getChildren().addAll(questionInput, sendButton);

    loadChatHistoryForMode(((ToggleButton) modeGroup.getSelectedToggle()).getText()); 
    
    chatBox.getChildren().addAll(header, chatArea, inputBox);
    return chatBox;
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

        // Make them final for lambda use
        final String finalQuestion = question;
        final String finalAnswer = answer;

        if (!finalQuestion.isEmpty() && !finalAnswer.isEmpty()) {
            // Create a custom dialog with ComboBox for set selection
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Add Flashcard to Set");
            dialog.setHeaderText("Choose an existing set or enter a new set name:");

            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            ComboBox<String> setComboBox = new ComboBox<>();
            setComboBox.getItems().addAll(taskManager.getFlashcardSets());
            setComboBox.setEditable(true);
            setComboBox.setPromptText("Set name");
            setComboBox.setValue("Default");

            VBox content = new VBox(10, new Label("Set name:"), setComboBox);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    return setComboBox.getEditor().getText().trim();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(setName -> {
                if (!setName.isEmpty()) {
                    taskManager.addFlashcard(finalQuestion, finalAnswer, setName);
                    refreshFlashcards();
                    System.out.println("✓ Flashcard saved to set: " + setName);
                }
            });
        }
    } catch (Exception e) {
        System.out.println("Error saving flashcard: " + e.getMessage());
    }
}

private VBox createFlashcardSection() {
    VBox section = new VBox(10);
    section.setPadding(new Insets(15));
    section.getStyleClass().add("panel");
    section.setMaxHeight(400);

    HBox header = new HBox(10);
    header.setAlignment(Pos.CENTER_LEFT);

    Label title = new Label("My Flashcards");
    title.getStyleClass().add("title");

    // --- SINGLE SET SELECTION DROPDOWN ---
    ComboBox<String> setComboBox = new ComboBox<>();
    setComboBox.getItems().addAll(taskManager.getFlashcardSets());
    setComboBox.setEditable(true);
    setComboBox.setPromptText("Select or add a set");
    if (!taskManager.getFlashcardSets().isEmpty()) {
        setComboBox.setValue(taskManager.getFlashcardSets().get(0));
    }

    Button uploadButton = new Button("📁 Upload");
    uploadButton.getStyleClass().add("button");
    uploadButton.setOnAction(e -> showUploadDialog());

    Button refreshButton = new Button("↻");
    refreshButton.getStyleClass().add("button");
    refreshButton.setOnAction(e -> refreshFlashcards());

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    header.getChildren().addAll(title, setComboBox, spacer, uploadButton, refreshButton);

    VBox flashcardList = new VBox(15);
    flashcardList.setPadding(new Insets(10));

    // Helper to refresh flashcards for selected set
    Runnable updateFlashcardList = () -> {
        flashcardList.getChildren().clear();
        String selectedSet = setComboBox.getValue();
        if (selectedSet != null) {
            for (Task.Flashcard flashcard : taskManager.getFlashcards()) {
                if (selectedSet.equals(flashcard.getSetName())) {
                    flashcardList.getChildren().add(createFlashcardCard(flashcard));
                }
            }
        }
    };

    setComboBox.setOnAction(e -> updateFlashcardList.run());

    // Initial load
    updateFlashcardList.run();

    ScrollPane scrollPane = new ScrollPane(flashcardList);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: transparent;");

    section.getChildren().setAll(header, scrollPane);
    return section;
}

// Helper to create a flashcard card with editable set ComboBox
private VBox createFlashcardCard(Task.Flashcard flashcard) {
    VBox card = new VBox(10);
    card.setPadding(new Insets(12));
    card.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 8px; -fx-border-color: #333; -fx-border-radius: 8px; -fx-border-width: 2px;");

    // Status label for mastery/review
    Label statusLabel = new Label();
    statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFD600; -fx-font-size: 14px;");
    updateStatusLabel(statusLabel, flashcard);

    Label questionLabel = new Label("Q: " + flashcard.getQuestion());
    questionLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 13px;");
    questionLabel.setWrapText(true);

    Label answerLabel = new Label("A: " + flashcard.getAnswer());
    answerLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
    answerLabel.setWrapText(true);
    answerLabel.setVisible(false);

    // "Click to reveal answer" hint with lightbulb
    Label flipHint = new Label("\uD83D\uDCA1 Click to reveal answer");
    flipHint.setStyle("-fx-text-fill: #a4a39eff; -fx-font-size: 12px; -fx-font-style: italic;");

    // Mastery/Review buttons 
    Button correctBtn = new Button("✔ Got it!");
    correctBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 4 16 4 16;");
    correctBtn.setOnAction(e -> {
        flashcard.incrementCorrectCount();
        flashcard.updateMasteryLevel();
        updateStatusLabel(statusLabel, flashcard);
        taskManager.saveFlashcard(flashcard);
    });

    Button wrongBtn = new Button("✗ Need Review");
    wrongBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 4 16 4 16;");
    wrongBtn.setOnAction(e -> {
        flashcard.incrementIncorrectCount();
        flashcard.updateMasteryLevel();
        updateStatusLabel(statusLabel, flashcard);
        taskManager.saveFlashcard(flashcard);
    });

    HBox buttonRow = new HBox(10, correctBtn, wrongBtn);
    buttonRow.setAlignment(Pos.CENTER);

    Button deleteBtn = new Button("×");
    deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;");
    deleteBtn.setOnAction(e -> {
        taskManager.deleteFlashcard(flashcard);
        refreshFlashcards();
    });

    card.getChildren().addAll(statusLabel, questionLabel, flipHint, answerLabel, buttonRow, deleteBtn);

    card.setOnMouseClicked(event -> {
        answerLabel.setVisible(!answerLabel.isVisible());
    });

    return card;
}

// Add this helper method to update the status label
private void updateStatusLabel(Label label, Task.Flashcard flashcard) {
    String status = flashcard.getMasteryLevel() == 2 ? "Mastered" : "Reviewing";
    label.setText("📚 " + status + "   ✓ " + flashcard.getCorrectCount() + " | ✗ " + flashcard.getIncorrectCount());
}
private void refreshFlashcards() {
    BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
    VBox rightPanel = createRightPanel();
    root.setCenter(rightPanel);
}

// ADD THIS HELPER METHOD
private String buildPrompt(String question, String mode) {
    switch (mode) {
        case "Learn":
            return "Explain this concept in detail for a student: " + question;
        case "Q&A":
            String topic = "";
            if (!taskManager.getTasks().isEmpty()) {
                topic = taskManager.getTasks().get(0).getTitle();
            }
            return "TOPIC: " + topic + "\n\n" +
                    "User's response: " + question + "\n\n" +
                    "If this is the first message generate a practice question about " + topic + ". " +
                    "or evaluate their answer and generate the NEXT question about " + topic + ". " +
                    "Never ask for the topic and only display in unicode";
        case "Flashcard":
            return "Create a flashcard for studying. Format your response EXACTLY like this:\n\n" +
                   "Q: [Write a clear question about: " + question + "]\n" +
                   "A: [Write a concise answer]\n\n" +
                   "Keep it simple and focused on one concept.";
        default:
            return question;
    }
}

private void saveChatHistory(String message, String mode) {
    try {
        java.util.Map<String, Object> chatData = new java.util.HashMap<>();
        chatData.put("userId", currentUser.getUid());
        chatData.put("message", message);
        chatData.put("mode", mode); // ADD MODE
        chatData.put("timestamp", new java.util.Date());
        
        com.google.cloud.firestore.Firestore db = FirebaseConfig.getFirestore();
        db.collection("chatHistory")
          .document(currentUser.getUid() + "_" + System.currentTimeMillis())
          .set(chatData);
    } catch (Exception e) {
        System.out.println("Error saving chat: " + e.getMessage());
    }
}

private void loadChatHistoryForMode(String mode) {
    if (chatArea == null) return;
    
    chatArea.clear(); // Clear before loading
    
    try {
        com.google.cloud.firestore.Firestore db = FirebaseConfig.getFirestore();
        db.collection("chatHistory")
          .whereEqualTo("userId", currentUser.getUid())
          .whereEqualTo("mode", mode)
          .orderBy("timestamp", com.google.cloud.firestore.Query.Direction.ASCENDING)
          .limit(50)
          .get()
          .get()
          .getDocuments()
          .forEach(doc -> {
              String message = doc.getString("message");
              if (message != null) {
                  javafx.application.Platform.runLater(() -> 
                      chatArea.appendText(message)
                  );
              }
          });
    } catch (Exception e) {
        System.out.println("Error loading chat history: " + e.getMessage());
    }
}

private void showUploadDialog() {
    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
    fileChooser.setTitle("Upload Study Material");
    fileChooser.getExtensionFilters().addAll(
        new javafx.stage.FileChooser.ExtensionFilter("All Supported Files", 
            "*.txt", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx"),
        new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"),
        new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
        new javafx.stage.FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
        new javafx.stage.FileChooser.ExtensionFilter("PowerPoint", "*.ppt", "*.pptx"),
        new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*")
    );
    
    java.io.File file = fileChooser.showOpenDialog(primaryStage);
    
    if (file != null) {
        // Prompt for set name
        String defaultName = file.getName().replaceAll("\\.(txt|pdf|doc|docx|ppt|pptx)$", "");
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(defaultName);
        dialog.setTitle("Create Flashcard Set");
        dialog.setHeaderText("Name your flashcard set:");
        dialog.setContentText("Set name:");
        
        dialog.showAndWait().ifPresent(setName -> {
            System.out.println("Processing file: " + file.getName());
            System.out.println("Creating flashcard set: " + setName);
            
            AIService aiService = new AIService();
            taskManager.processUploadedFile(file, setName, aiService, () -> {
                refreshFlashcards();
                showAlert("Success", "Flashcards generated from " + file.getName() + "!");
            });
        });
    }
}

private void showAlert(String title, String message) {
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}