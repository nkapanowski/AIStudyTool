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
    
    
    public static class Flashcard {
        private String id;
        private String question;
        private String answer;
        private Date createdAt;
        private String userId;
        private int masteryLevel;
        private int correctCount;
        private int incorrectCount;
        private String setName; //  organize flashcards into sets
        
        public Flashcard() {
            // Required for Firestore
        }
        
        public Flashcard(String question, String answer, String userId) {
            this.id = java.util.UUID.randomUUID().toString();
            this.question = question;
            this.answer = answer;
            this.createdAt = new Date();
            this.userId = userId;
            this.masteryLevel = 0;
            this.correctCount = 0;
            this.incorrectCount = 0;
            this.setName = "Default"; // Always set a default
        }
        
        // OVERLOADED CONSTRUCTOR with set name
        public Flashcard(String question, String answer, String userId, String setName) {
            this.id = java.util.UUID.randomUUID().toString();
            this.question = question;
            this.answer = answer;
            this.userId = userId;
            this.setName = (setName != null && !setName.isEmpty()) ? setName : "Default";
            this.createdAt = new Date();
            this.masteryLevel = 0;
            this.correctCount = 0;
            this.incorrectCount = 0;
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
        
        public int getMasteryLevel() { return masteryLevel; }
        public void setMasteryLevel(int masteryLevel) { this.masteryLevel = masteryLevel; }
        
        public int getCorrectCount() { return correctCount; }
        public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
        
        public int getIncorrectCount() { return incorrectCount; }
        public void setIncorrectCount(int incorrectCount) { this.incorrectCount = incorrectCount; }
        
        public String getSetName() { return setName; }
        public void setSetName(String setName) { this.setName = setName; }
    }
}