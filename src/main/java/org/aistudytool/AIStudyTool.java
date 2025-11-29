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
        
        if (!question.isEmpty() && !answer.isEmpty()) {
            taskManager.addFlashcard(question, answer);
            refreshFlashcards(); 
            System.out.println("✓ Flashcard saved successfully!");
        }
    } catch (Exception e) {
        System.out.println("Error saving flashcard: " + e.getMessage());
    }
}

private VBox createFlashcardSection() {
    VBox section = new VBox(10);
    section.setPadding(new Insets(15));
    section.getStyleClass().add("panel");
    section.setMaxHeight(250);
    
    HBox header = new HBox(10);
    header.setAlignment(Pos.CENTER_LEFT);
    
    Label title = new Label("My Flashcards (" + taskManager.getFlashcards().size() + ")");
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
    
    Button refreshButton = new Button("↻");
    refreshButton.getStyleClass().add("button");
    refreshButton.setOnAction(e -> refreshFlashcards());
    
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    
    header.getChildren().addAll(title, spacer, refreshButton);
    
    ListView<Task.Flashcard> flashcardListView = new ListView<>(taskManager.getFlashcards());
    flashcardListView.setPrefHeight(200);
    flashcardListView.getStyleClass().add("task-list");
    
    flashcardListView.setCellFactory(lv -> new javafx.scene.control.ListCell<Task.Flashcard>() {
        private boolean isFlipped = false;
        
        @Override
        protected void updateItem(Task.Flashcard flashcard, boolean empty) {
            super.updateItem(flashcard, empty);
            if (empty || flashcard == null) {
                setGraphic(null);
            } else {
                isFlipped = false;
                
                VBox card = new VBox(10);
                card.setPadding(new Insets(12));
                
                // Color based on mastery level
                String borderColor = flashcard.getMasteryLevel() == 2 ? "#4CAF50" : // Green = Mastered
                                    flashcard.getMasteryLevel() == 1 ? "#FFC107" : // Yellow = Reviewing
                                    "#333333"; // Gray = New
                
                card.setStyle(
                    "-fx-background-color: #1a1a1a; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: " + borderColor + "; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-width: 2px; " +
                    "-fx-cursor: hand;"
                );
                
                // Mastery badge
                String masteryText = flashcard.getMasteryLevel() == 2 ? "✓ Mastered" :
                                   flashcard.getMasteryLevel() == 1 ? "📚 Reviewing" :
                                   "🆕 New";
                Label masteryBadge = new Label(masteryText);
                masteryBadge.setStyle(
                    "-fx-text-fill: " + (flashcard.getMasteryLevel() == 2 ? "#4CAF50" : 
                                        flashcard.getMasteryLevel() == 1 ? "#FFC107" : "#888888") + "; " +
                    "-fx-font-size: 10px; " +
                    "-fx-font-weight: bold;"
                );
                
                // Stats
                Label stats = new Label("✓ " + flashcard.getCorrectCount() + " | ✗ " + flashcard.getIncorrectCount());
                stats.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");
                
                // Question
                Label questionLabel = new Label("Q: " + flashcard.getQuestion());
                questionLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 13px;");
                questionLabel.setWrapText(true);
                
                // Answer (hidden initially)
                Label answerLabel = new Label("A: " + flashcard.getAnswer());
                answerLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
                answerLabel.setWrapText(true);
                answerLabel.setVisible(false);
                answerLabel.setManaged(false);
                
                // Flip hint
                Label flipHint = new Label("💡 Click to reveal answer");
                flipHint.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px; -fx-font-style: italic;");
                
                // Interactive buttons (shown after flip)
                HBox interactiveButtons = new HBox(10);
                interactiveButtons.setAlignment(Pos.CENTER);
                interactiveButtons.setVisible(false);
                interactiveButtons.setManaged(false);
                
                Button gotItBtn = new Button("✓ Got it!");
                gotItBtn.setStyle(
                    "-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 11px; " +
                    "-fx-padding: 5 15; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-cursor: hand;"
                );
                gotItBtn.setOnAction(e -> {
                    e.consume();
                    taskManager.updateFlashcardMastery(flashcard, true);
                    refreshFlashcards();
                });
                
                Button needReviewBtn = new Button("✗ Need Review");
                needReviewBtn.setStyle(
                    "-fx-background-color: #ff4444; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 11px; " +
                    "-fx-padding: 5 15; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-cursor: hand;"
                );
                needReviewBtn.setOnAction(e -> {
                    e.consume();
                    taskManager.updateFlashcardMastery(flashcard, false);
                    refreshFlashcards();
                });
                
                interactiveButtons.getChildren().addAll(gotItBtn, needReviewBtn);
                
                // Delete button
                Button deleteBtn = new Button("×");
                deleteBtn.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-text-fill: #ff4444; " +
                    "-fx-font-size: 18px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand;"
                );
                deleteBtn.setOnAction(e -> {
                    e.consume();
                    taskManager.deleteFlashcard(flashcard);
                    refreshFlashcards();
                });
                
                // Top row
                HBox topRow = new HBox(10);
                topRow.setAlignment(Pos.CENTER_LEFT);
                Region topSpacer = new Region();
                HBox.setHgrow(topSpacer, Priority.ALWAYS);
                topRow.getChildren().addAll(masteryBadge, stats, topSpacer, deleteBtn);
                
                card.getChildren().addAll(topRow, questionLabel, answerLabel, flipHint, interactiveButtons);
                
                // Click to flip
                card.setOnMouseClicked(event -> {
                    isFlipped = !isFlipped;
                    
                    if (isFlipped) {
                        answerLabel.setVisible(true);
                        answerLabel.setManaged(true);
                        flipHint.setVisible(false);
                        flipHint.setManaged(false);
                        interactiveButtons.setVisible(true);
                        interactiveButtons.setManaged(true);
                        card.setStyle(
                            "-fx-background-color: #252525; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-border-color: #4CAF50; " +
                            "-fx-border-radius: 8px; " +
                            "-fx-border-width: 2px; " +
                            "-fx-cursor: hand;"
                        );
                    } else {
                        answerLabel.setVisible(false);
                        answerLabel.setManaged(false);
                        flipHint.setVisible(true);
                        flipHint.setManaged(true);
                        interactiveButtons.setVisible(false);
                        interactiveButtons.setManaged(false);
                        card.setStyle(
                            "-fx-background-color: #1a1a1a; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-border-color: " + borderColor + "; " +
                            "-fx-border-radius: 8px; " +
                            "-fx-border-width: 2px; " +
                            "-fx-cursor: hand;"
                        );
                    }
                });
                
                // Hover effect
                card.setOnMouseEntered(e -> {
                    if (!isFlipped) {
                        card.setStyle(
                            "-fx-background-color: #1f1f1f; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-border-color: #4CAF50; " +
                            "-fx-border-radius: 8px; " +
                            "-fx-border-width: 2px; " +
                            "-fx-cursor: hand;"
                        );
                    }
                });
                
                card.setOnMouseExited(e -> {
                    if (!isFlipped) {
                        card.setStyle(
                            "-fx-background-color: #1a1a1a; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-border-color: " + borderColor + "; " +
                            "-fx-border-radius: 8px; " +
                            "-fx-border-width: 2px; " +
                            "-fx-cursor: hand;"
                        );
                    }
                });
                
                setGraphic(card);
            }
        }
    });
    
    section.getChildren().addAll(header, flashcardListView);
    return section;
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
}