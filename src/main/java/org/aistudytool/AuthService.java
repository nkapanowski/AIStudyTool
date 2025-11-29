package org.aistudytool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static final String FIREBASE_API_KEY = "APIKEY";
    private static final String GOOGLE_CLIENT_ID = "CLIENTID";
    private static final String CLAUDE_API_KEY = "CLAUDEAPIKEY";       
    private static final String AUTH_DOMAIN = "aistudytool-d7a8a.firebaseapp.com";
    private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;
    private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;
    private static final String SESSION_FILE = "session.properties";


    private Gson gson = new Gson();
    private UserRepo userRepo = new UserRepo();

    public static void saveSession(User user) {
    Properties props = new Properties();
    props.setProperty("userId", user.getUid());
    props.setProperty("email", user.getEmail());
    props.setProperty("displayName", user.getDisplayName());
    
    try (FileOutputStream out = new FileOutputStream(SESSION_FILE)) {
        props.store(out, "User Session");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static User loadSession() {
    File sessionFile = new File(SESSION_FILE);
    if (!sessionFile.exists()) {
        return null;
    }
    
    Properties props = new Properties();
    try (FileInputStream in = new FileInputStream(SESSION_FILE)) {
        props.load(in);
        
        String userId = props.getProperty("userId");
        String email = props.getProperty("email");
        String displayName = props.getProperty("displayName");
        
        User user = new User();
        user.setUid(userId);
        user.setEmail(email);
        user.setDisplayName(displayName);
        
        return user;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

public static void clearSession() {
    File sessionFile = new File(SESSION_FILE);
    if (sessionFile.exists()) {
        sessionFile.delete();
    }
}

public static boolean hasSession() {
    return new File(SESSION_FILE).exists();
}
    
    public void signIn(String email, String password, AuthCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("email", email);
                requestBody.addProperty("password", password);
                requestBody.addProperty("returnSecureToken", true);
                
                String response = makeRequest(SIGN_IN_URL, requestBody);
                JsonObject responseJson = gson.fromJson(response, JsonObject.class);
                
            if (responseJson.has("error")) {
                JsonObject error = responseJson.getAsJsonObject("error");
                String errorMessage = error.get("message").getAsString();
                
                if (errorMessage.contains("INVALID_LOGIN_CREDENTIALS") || 
                    errorMessage.contains("INVALID_PASSWORD") ||
                    errorMessage.contains("EMAIL_NOT_FOUND")) {
                    callback.onFailure("Invalid email or password");
                } else if (errorMessage.contains("USER_DISABLED")) {
                    callback.onFailure("This account has been disabled");
                } else {
                    callback.onFailure("Login failed: " + errorMessage);
                }
                return;
            }

                String uid = responseJson.get("localId").getAsString();
                
                User user = userRepo.getUser(uid);
                if (user == null) {
                    user = new User(uid, email, email.split("@")[0]);
                    userRepo.saveUser(user);
                }
                
                callback.onSuccess(user);
                
            } catch (Exception e) {
                callback.onFailure("Invalid email or password");
            }
        });
    }
    
    public void signUp(String email, String password, AuthCallback callback) {
    CompletableFuture.runAsync(() -> {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("email", email);
            requestBody.addProperty("password", password);
            requestBody.addProperty("returnSecureToken", true);
            
            String response = makeRequest(SIGN_UP_URL, requestBody);
            JsonObject responseJson = gson.fromJson(response, JsonObject.class);
            
            // Check for error in response
            if (responseJson.has("error")) {
                JsonObject error = responseJson.getAsJsonObject("error");
                String errorMessage = error.get("message").getAsString();
                
                // Make error messages user-friendly
                if (errorMessage.contains("EMAIL_EXISTS")) {
                    callback.onFailure("This email is already registered. Please sign in instead.");
                } else if (errorMessage.contains("WEAK_PASSWORD")) {
                    callback.onFailure("Password should be at least 6 characters.");
                } else if (errorMessage.contains("INVALID_EMAIL")) {
                    callback.onFailure("Invalid email address.");
                } else {
                    callback.onFailure("Registration failed: " + errorMessage);
                }
                return;
            }
            
            String uid = responseJson.get("localId").getAsString();
            
            User user = new User(uid, email, email.split("@")[0]);
            userRepo.saveUser(user);
            
            callback.onSuccess(user);
            
        } catch (Exception e) {
            callback.onFailure("Registration failed: " + e.getMessage());
        }
    });
}
    public void signInWithGoogle(AuthCallback callback) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Sign in with Google");
        
        WebView webView = new WebView();
        webView.setPrefSize(500, 600);
        
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
               "client_id=" + GOOGLE_CLIENT_ID + "&" +
               "redirect_uri=https://" + AUTH_DOMAIN + "/__/auth/handler&" +
               "response_type=id_token&" +
               "scope=openid%20email%20profile&" +
               "prompt=select_account&" +
               "nonce=12345";
        
        webView.getEngine().load(authUrl);
        
        webView.getEngine().locationProperty().addListener((obs, oldUrl, newUrl) -> {
            if (newUrl.contains("id_token=")) {
                int start = newUrl.indexOf("id_token=") + 9;
                int end = newUrl.indexOf("&", start);
                if (end == -1) end = newUrl.length();
                String token = newUrl.substring(start, end);
                
                verifyGoogleToken(token, callback);
                popupStage.close();
            }
        });

            Scene scene = new Scene(webView);
            popupStage.setScene(scene);
            popupStage.show();
    }
    
private void verifyGoogleToken(String idToken, AuthCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                final String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + FIREBASE_API_KEY;
                
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("postBody", "id_token=" + idToken + "&providerId=google.com");
                requestBody.addProperty("requestUri", "http://localhost");
                requestBody.addProperty("returnIdpCredential", true);
                requestBody.addProperty("returnSecureToken", true);
                
                String response = makeRequest(url, requestBody);
                JsonObject responseJson = gson.fromJson(response, JsonObject.class);
                
                String uid = responseJson.get("localId").getAsString();
                String email = responseJson.get("email").getAsString();
                String displayName = responseJson.has("displayName") ? 
                    responseJson.get("displayName").getAsString() : email.split("@")[0];
                
                User user = userRepo.getUser(uid);
                if (user == null) {
                    user = new User(uid, email, displayName);
                    userRepo.saveUser(user);
                }
                
                final User finaluser = user;
                javafx.application.Platform.runLater(() -> callback.onSuccess(finaluser));
                
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> 
                    callback.onFailure("Google sign-in failed: " + e.getMessage()));
            }
        });
    }
    
    private String makeRequest(String url, JsonObject body) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(gson.toJson(body)));
            
            try (CloseableHttpResponse response = client.execute(post)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }

    public static String getClaudeApiKey() {
        return CLAUDE_API_KEY;
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
}