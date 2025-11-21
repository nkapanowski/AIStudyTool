package org.aistudytool;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AIService {
    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-20250514";
    
    private OkHttpClient client;
    private Gson gson;
    
    public AIService() {
        // Increase timeout to 60 seconds
        this.client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }
    
    public interface AICallback {
        void onSuccess(String response);
        void onFailure(String error);
    }
    
    public void askQuestion(String question, AICallback callback) {
        new Thread(() -> {
            try {
                System.out.println("Sending request to Claude API...");
                
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", MODEL);
                requestBody.addProperty("max_tokens", 1024);
                
                JsonArray messages = new JsonArray();
                JsonObject message = new JsonObject();
                message.addProperty("role", "user");
                message.addProperty("content", question);
                messages.add(message);
                
                requestBody.add("messages", messages);
                
                RequestBody body = RequestBody.create(
                    gson.toJson(requestBody),
                    MediaType.parse("application/json")
                );
                
                Request request = new Request.Builder()
                    .url(CLAUDE_API_URL)
                    .addHeader("x-api-key", AuthService.getClaudeApiKey())
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .post(body)
                    .build();
                
                System.out.println("Making API call...");
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                
                System.out.println("Response code: " + response.code());
                
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    JsonArray content = jsonResponse.getAsJsonArray("content");
                    String text = content.get(0).getAsJsonObject().get("text").getAsString();
                    
                    System.out.println("AI Response: " + text);
                    javafx.application.Platform.runLater(() -> callback.onSuccess(text));
                } else {
                    System.err.println("API Error: " + responseBody);
                    javafx.application.Platform.runLater(() -> 
                        callback.onFailure("API Error: " + response.code() + " - " + responseBody));
                }
                
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> 
                    callback.onFailure("Failed to connect: " + e.getMessage()));
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> 
                    callback.onFailure("Error: " + e.getMessage()));
            }
        }).start();
    }
}