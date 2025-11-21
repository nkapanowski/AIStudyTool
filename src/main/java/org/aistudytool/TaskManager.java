package org.aistudytool;

import com.google.cloud.firestore.Firestore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
        Task.Flashcard flashcard = new Task.Flashcard(question, answer, userId);
        
        Map<String, Object> flashcardData = new HashMap<>();
        flashcardData.put("id", flashcard.getId());
        flashcardData.put("question", flashcard.getQuestion());
        flashcardData.put("answer", flashcard.getAnswer());
        flashcardData.put("createdAt", flashcard.getCreatedAt());
        flashcardData.put("userId", flashcard.getUserId());
        
        db.collection("flashcards").document(flashcard.getId()).set(flashcardData);
        flashcards.add(flashcard);
        
        System.out.println("✓ Flashcard saved! Q: " + question);
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
}