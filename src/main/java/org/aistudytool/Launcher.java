package org.aistudytool;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        if (AuthService.hasSession()) {
            User user = AuthService.loadSession();
            if (user != null) {
                UserRepo userRepo = new UserRepo();
                User firestoreUser = userRepo.getUserById(user.getUid());
                
                if (firestoreUser != null) {
                    AIStudyTool.setCurrentUser(user);
                    AIStudyTool app = new AIStudyTool();
                    app.start(primaryStage);
                    return;
                } else {
                    System.out.println("User no longer exists in database. Clearing session...");
                    AuthService.clearSession();
                }
            }
        }
        
        //no session, show login
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