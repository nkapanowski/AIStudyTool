package org.aistudytool;

import com.google.cloud.firestore.Firestore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

public class TaskManager {
    private Firestore db;
    private ObservableList<Task> tasks;
    private ObservableList<Task.Flashcard> flashcards;
    private String userId;
    private VBox taskListContainer;

public TaskManager(String userId) {
        this.db = FirebaseConfig.getFirestore();
        this.tasks = FXCollections.observableArrayList();
        this.flashcards = FXCollections.observableArrayList();
        this.userId = userId;
        loadTasks();
        loadFlashcards();
    }
    public void setTaskListContainer(VBox container) {
        this.taskListContainer = container;
    }

    public void addTask(String title) {
        Task task = new Task(title, userId);
        
        // Save to Firestore
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("id", task.getId());
        taskData.put("title", task.getTitle());
        taskData.put("completed", task.isCompleted());
        taskData.put("createdAt", task.getCreatedAt());
        taskData.put("userId", task.getUserId());
        
        db.collection("tasks").document(task.getId()).set(taskData);
        tasks.add(task);
        
        // Add to UI if container exists
        if (taskListContainer != null) {
            addTaskToUI(task);
        }
    }
    
    // Add task from TextField 
    public void addTask(TextField taskInput) {
        String taskText = taskInput.getText().trim();
        if (!taskText.isEmpty()) {
            addTask(taskText);
            taskInput.clear();
        }
    }
    
    // Create task UI element
    private void addTaskToUI(Task task) {
        HBox taskItem = new HBox(10);
        taskItem.setAlignment(Pos.CENTER_LEFT);
        taskItem.setPadding(new Insets(10));
        taskItem.getStyleClass().add("task-item");
        
        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(task.isCompleted());
        checkbox.setOnAction(e -> toggleTask(task));
        
        Label taskLabel = new Label(task.getTitle());
        taskLabel.getStyleClass().add("task-label");
        taskLabel.setWrapText(true);
        taskLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(taskLabel, Priority.ALWAYS);
        
        if (task.isCompleted()) {
            taskLabel.setStyle("-fx-text-fill: #888888; -fx-strikethrough: true;");
        }
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button deleteButton = new Button("×");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> {
            deleteTask(task);
            taskListContainer.getChildren().remove(taskItem);
        });
        
        taskItem.getChildren().addAll(checkbox, taskLabel, spacer, deleteButton);
        taskListContainer.getChildren().add(taskItem);
    }
    
    // Delete task from Firestore
    public void deleteTask(Task task) {
        db.collection("tasks").document(task.getId()).delete();
        tasks.remove(task);
    }
    
    // Toggle task completion
    public void toggleTask(Task task) {
        task.setCompleted(!task.isCompleted());
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("completed", task.isCompleted());
        
        db.collection("tasks").document(task.getId()).update(updates);
    }
    
    // Load tasks from Firestore
    private void loadTasks() {
        try {
            db.collection("tasks")
                .whereEqualTo("userId", userId)
                .get()
                .get()
                .getDocuments()
                .forEach(doc -> {
                    Task task = doc.toObject(Task.class);
                    tasks.add(task);
                });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    // Load tasks into UI
    public void loadTasksToUI() {
        if (taskListContainer != null) {
            taskListContainer.getChildren().clear();
            for (Task task : tasks) {
                addTaskToUI(task);
            }
        }
    }
    
    public ObservableList<Task> getTasks() {
        return tasks;
    }
    
    public int getTaskCount() {
        return tasks.size();
    }

     public void addFlashcard(String question, String answer) {
    addFlashcard(question, answer, "Default"); // Use default set
}

    public void deleteFlashcard(Task.Flashcard flashcard) {
        db.collection("flashcards").document(flashcard.getId()).delete();
        flashcards.remove(flashcard);
    }

    public void loadFlashcards() {
        try {
            db.collection("flashcards")
                .whereEqualTo("userId", userId)
                .get()
                .get()
                .getDocuments()
                .forEach(doc -> {
                    Task.Flashcard flashcard = doc.toObject(Task.Flashcard.class);
                    flashcards.add(flashcard);
                });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Task.Flashcard> getFlashcards() {
        return flashcards;
    }

    public void updateFlashcardMastery(Task.Flashcard flashcard, boolean correct) {
        if (correct) {
            flashcard.setCorrectCount(flashcard.getCorrectCount() + 1);
            if (flashcard.getCorrectCount() >= 3) {
                flashcard.setMasteryLevel(2); // Mastered
            } else {
                flashcard.setMasteryLevel(1); // Reviewing
            }
        } else {
            flashcard.setIncorrectCount(flashcard.getIncorrectCount() + 1);
            flashcard.setMasteryLevel(0); // Reviewing
        }
        saveFlashcard(flashcard);
    }
    
    // Get unique set names
    public List<String> getFlashcardSets() {
        return flashcards.stream()
            .map(Task.Flashcard::getSetName)
            .filter(setName -> setName != null && !setName.isEmpty()) // Add null check
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    // Get flashcards by set
    public ObservableList<Task.Flashcard> getFlashcardsBySet(String setName) {
        return flashcards.filtered(fc -> fc.getSetName().equals(setName));
    }

    // Add flashcard with set name
    public void addFlashcard(String question, String answer, String setName) {
        Task.Flashcard flashcard = new Task.Flashcard(question, answer, userId, setName);
        
        Map<String, Object> flashcardData = new HashMap<>();
        flashcardData.put("id", flashcard.getId());
        flashcardData.put("question", flashcard.getQuestion());
        flashcardData.put("answer", flashcard.getAnswer());
        flashcardData.put("createdAt", flashcard.getCreatedAt());
        flashcardData.put("userId", flashcard.getUserId());
        flashcardData.put("masteryLevel", flashcard.getMasteryLevel());
        flashcardData.put("correctCount", flashcard.getCorrectCount());
        flashcardData.put("incorrectCount", flashcard.getIncorrectCount());
        flashcardData.put("setName", flashcard.getSetName()); // ADD THIS
        
        db.collection("flashcards").document(flashcard.getId()).set(flashcardData);
        flashcards.add(flashcard);
        
        System.out.println("✓ Flashcard saved to set: " + setName);
    }

    // Process uploaded file and generate flashcards using AI
    public void processUploadedFile(File file, String setName, AIService aiService, Runnable onComplete) {
        try {
            String content = "";
            String fileName = file.getName().toLowerCase();
            
            // Extract text based on file type
            if (fileName.endsWith(".txt")) {
                content = Files.readString(file.toPath());
            } 
            else if (fileName.endsWith(".pdf")) {
                content = extractTextFromPDF(file);
            } 
            else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                content = extractTextFromWord(file);
            } 
            else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
                content = extractTextFromPowerPoint(file);
            }
            else {
                System.out.println("Unsupported file type: " + fileName);
                return;
            }
            
            // Limit content size (Claude API has token limits)
            if (content.length() > 10000) {
                content = content.substring(0, 10000);
            }
            
            // Create AI prompt to generate flashcards
            String prompt = "Based on this study material, generate 10 flashcards in this exact format:\n\n" +
                           "Q: [Question]\n" +
                           "A: [Answer]\n\n" +
                           "Material:\n" + content;
            
            // Call AI to generate flashcards
            aiService.askQuestion(prompt, new AIService.AICallback() {
                @Override
                public void onSuccess(String response) {
                    parseAndSaveMultipleFlashcards(response, setName);
                    if (onComplete != null) {
                        javafx.application.Platform.runLater(onComplete);
                    }
                }
                
                @Override
                public void onFailure(String error) {
                    System.out.println("Error generating flashcards: " + error);
                }
            });
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ADD THESE HELPER METHODS
    private String extractTextFromPDF(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromWord(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String extractTextFromPowerPoint(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file);
         XMLSlideShow ppt = new XMLSlideShow(fis)) {
    
        StringBuilder text = new StringBuilder();
        
        // Extract text from each slide
        ppt.getSlides().forEach(slide -> {
            slide.getShapes().forEach(shape -> {
                if (shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
                    org.apache.poi.xslf.usermodel.XSLFTextShape textShape = 
                        (org.apache.poi.xslf.usermodel.XSLFTextShape) shape;
                    String shapeText = textShape.getText();
                    if (shapeText != null && !shapeText.isEmpty()) {
                        text.append(shapeText).append("\n");
                    }
                }
            });
        });
        
        return text.toString();
    }
}
    
    // Parse multiple flashcards from AI response
    private void parseAndSaveMultipleFlashcards(String response, String setName) {
        String[] lines = response.split("\n");
        String currentQuestion = "";
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Q:")) {
                currentQuestion = line.substring(2).trim();
            } else if (line.startsWith("A:") && !currentQuestion.isEmpty()) {
                String answer = line.substring(2).trim();
                addFlashcard(currentQuestion, answer, setName);
                currentQuestion = "";
            }
        }
    }

    public Firestore getDb() {
        return db;
    }

    public void saveFlashcard(Task.Flashcard flashcard) {
        Map<String, Object> flashcardData = new HashMap<>();
        flashcardData.put("id", flashcard.getId());
        flashcardData.put("question", flashcard.getQuestion());
        flashcardData.put("answer", flashcard.getAnswer());
        flashcardData.put("createdAt", flashcard.getCreatedAt());
        flashcardData.put("userId", flashcard.getUserId());
        flashcardData.put("masteryLevel", flashcard.getMasteryLevel());
        flashcardData.put("correctCount", flashcard.getCorrectCount());
        flashcardData.put("incorrectCount", flashcard.getIncorrectCount());
        flashcardData.put("setName", flashcard.getSetName());

        db.collection("flashcards").document(flashcard.getId()).set(flashcardData);
    }
}