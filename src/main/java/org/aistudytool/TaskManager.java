package org.aistudytool;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    VBox taskListContainer;
    List<HBox> taskItems;

    public TaskManager(VBox taskListContainer) {
        this.taskListContainer = taskListContainer;
        this.taskItems = new ArrayList<>();
    }

    public void addTask(TextField taskInput) {
        String taskText = taskInput.getText().trim();

        if (!taskText.isEmpty()) {
            HBox taskItem = new HBox(10);
            taskItem.setAlignment(Pos.CENTER_LEFT);
            taskItem.setPadding(new Insets(10));
            taskItem.getStyleClass().add("task-item");

            Label taskLabel = new Label(taskText);
            taskLabel.getStyleClass().add("task-label");
            taskLabel.setWrapText(true);
            taskLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(taskLabel, Priority.ALWAYS);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button deleteButton = new Button("x");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(e -> {
                taskListContainer.getChildren().remove(taskItem);
                taskItems.remove(taskItem);
            });

            taskItem.getChildren().addAll(taskLabel, spacer, deleteButton);
            taskItems.add(taskItem);
            taskListContainer.getChildren().add(taskItem);

            taskInput.clear();
        }
    }

    public void removeTask(HBox taskItem) {
        taskListContainer.getChildren().remove(taskItem);
        taskItems.remove(taskItem);
    }

    public int getTaskCount() {
        return taskItems.size();
    }

}