package org.aistudytool;

import java.util.Date;

public class Task {
    private String id;
    private String title;
    private boolean completed;
    private Date createdAt;
    private String userId;
    
    public Task() {
        // Required for Firestore
    }
    
    public Task(String title, String userId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.completed = false;
        this.createdAt = new Date();
        this.userId = userId;
    }
    
    // TASK GETTERS AND SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    // NESTED FLASHCARD CLASS
    public static class Flashcard {
        private String id;
        private String question;
        private String answer;
        private Date createdAt;
        private String userId;
        
        public Flashcard() {
            // Required for Firestore
        }
        
        public Flashcard(String question, String answer, String userId) {
            this.id = java.util.UUID.randomUUID().toString();
            this.question = question;
            this.answer = answer;
            this.createdAt = new Date();
            this.userId = userId;
        }
        
        // FLASHCARD GETTERS AND SETTERS
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
}