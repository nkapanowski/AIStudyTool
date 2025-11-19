package org.aistudytool;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        try {
            FirebaseConfig.initialize();
            Application.launch(AIStudyTool.class, args);
        } catch (Exception e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
