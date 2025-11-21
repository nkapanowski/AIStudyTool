package org.aistudytool;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Check if user is already logged in
        if (AuthService.hasSession()) {
            User user = AuthService.loadSession();
            if (user != null) {
                // User is logged in, go to main app
                AIStudyTool.setCurrentUser(user);
                AIStudyTool app = new AIStudyTool();
                app.start(primaryStage);
                return;
            }
        }
        
        // No session, show login
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }
    
    public static void main(String[] args) {
        try {
            FirebaseConfig.initialize();
            Application.launch(Launcher.class, args);
        } catch (Exception e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}