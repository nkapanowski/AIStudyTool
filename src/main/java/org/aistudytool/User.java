package org.aistudytool;

import java.util.List;
import java.util.ArrayList;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private List<String> tasks;
    private int totalStudyMinutes;
    
    public User() {
        this.tasks = new ArrayList<>();
        this.totalStudyMinutes = 0;
    }
    
    public User(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.tasks = new ArrayList<>();
        this.totalStudyMinutes = 0;
    }
    
    // Getters and setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public List<String> getTasks() { return tasks; }
    public void setTasks(List<String> tasks) { this.tasks = tasks; }
    
    public int getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(int totalStudyMinutes) { 
        this.totalStudyMinutes = totalStudyMinutes; 
    }
}