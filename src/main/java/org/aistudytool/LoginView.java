package org.aistudytool;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView extends Application {
    private Stage primaryStage;
    private AuthService authService;
    
    public LoginView() {
        this.authService = new AuthService();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Scene scene = createLoginScene();
        primaryStage.setTitle("AI Study Tool - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public Scene createLoginScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0a0a0a;");
        
        Label title = new Label("AI Study Tool");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitle = new Label("Sign in to continue");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888;");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(350);
        emailField.getStyleClass().add("input");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(350);
        passwordField.getStyleClass().add("input");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        Button loginButton = new Button("Sign In");
        loginButton.setMaxWidth(350);
        loginButton.getStyleClass().add("button");
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText(), errorLabel));
        
        Button registerButton = new Button("Create Account");
        registerButton.setMaxWidth(350);
        registerButton.getStyleClass().add("button");
        registerButton.setOnAction(e -> handleRegister(emailField.getText(), passwordField.getText(), errorLabel));
        
        HBox dividerBox = new HBox(10);
        dividerBox.setAlignment(Pos.CENTER);
        dividerBox.setMaxWidth(350);
        
        Separator leftLine = new Separator();
        Separator rightLine = new Separator();
        HBox.setHgrow(leftLine, Priority.ALWAYS);
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        
        Label orLabel = new Label("OR");
        orLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
        
        dividerBox.getChildren().addAll(leftLine, orLabel, rightLine);
        
        Button googleButton = new Button("Sign in with Google");
        googleButton.setMaxWidth(350);
        googleButton.setStyle(
            "-fx-background-color: #4285f4; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        googleButton.setOnAction(e -> handleGoogleSignIn(errorLabel));
    
        googleButton.setOnMouseEntered(e -> 
            googleButton.setStyle(
                "-fx-background-color: #357ae8; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12px 20px; " +
                "-fx-background-radius: 5px; " +
                "-fx-cursor: hand;"
            )
        );
        googleButton.setOnMouseExited(e -> 
            googleButton.setStyle(
                "-fx-background-color: #4285f4; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12px 20px; " +
                "-fx-background-radius: 5px; " +
                "-fx-cursor: hand;"
            )
        );
        //key handling
        passwordField.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText(), errorLabel));
        
        root.getChildren().addAll(
            title, subtitle, 
            emailField, passwordField, errorLabel, 
            loginButton, registerButton,
            dividerBox, googleButton
        );
        
        Scene scene = new Scene(root, 500, 700);
        try {
            scene.getStylesheets().add(getClass().getResource("/CSS/studytool.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return scene;
    }
    
private void handleGoogleSignIn(Label errorLabel) {
    authService.signInWithGoogle(new AuthService.AuthCallback() {
        @Override
        public void onSuccess(User user) {
            javafx.application.Platform.runLater(() -> {
                AuthService.saveSession(user); 
                openMainApp(user);
            });
        }
        
        @Override
        public void onFailure(String error) {
            javafx.application.Platform.runLater(() -> {
                showError(errorLabel, error);
            });
        }
    });
}
    
 private void handleLogin(String email, String password, Label errorLabel) {
    if (email.isEmpty() || password.isEmpty()) {
        showError(errorLabel, "Please enter email and password");
        return;
    }
    
    authService.signIn(email, password, new AuthService.AuthCallback() {
        @Override
        public void onSuccess(User user) {
            javafx.application.Platform.runLater(() -> {
                AuthService.saveSession(user);
                openMainApp(user);
            });
        }
        
        @Override
        public void onFailure(String error) {
            javafx.application.Platform.runLater(() -> {
                showError(errorLabel, error);
            });
        }
    });
}
    
    private void handleRegister(String email, String password, Label errorLabel) {
    if (email.isEmpty() || password.isEmpty()) {
        showError(errorLabel, "Please enter email and password");
        return;
    }
    
    if (password.length() < 6) {
        showError(errorLabel, "Password must be at least 6 characters");
        return;
    }
    
    authService.signUp(email, password, new AuthService.AuthCallback() {
        @Override
        public void onSuccess(User user) {
            javafx.application.Platform.runLater(() -> {
                AuthService.saveSession(user); 
                openMainApp(user);
            });
        }
        
        @Override
        public void onFailure(String error) {
            javafx.application.Platform.runLater(() -> {
                showError(errorLabel, error);
            });
        }
    });
}
    
    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

   private void openMainApp(User user) {
        AIStudyTool.setCurrentUser(user); //set the user
        primaryStage.close(); //close login window
        
        try {
            AIStudyTool mainApp = new AIStudyTool();
            Stage mainStage = new Stage();
            mainApp.start(mainStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}