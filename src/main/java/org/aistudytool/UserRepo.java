package org.aistudytool;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserRepo {
    private Firestore db;
    
    public UserRepo() {
        this.db = FirebaseConfig.getFirestore();
    }
    
    @SuppressWarnings("null")
    public User getUser(String uid) {
        if (uid == null || uid.isEmpty()) {
            return null;
        }
        
        try {
            DocumentSnapshot document = db.collection("users").document(uid).get().get();
            if (document.exists()) {
                return document.toObject(User.class);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings("null")
    public void saveUser(User user) {
        if (user == null || user.getUid() == null) {
            return;
        }
        
        String uid = user.getUid();
        String email = user.getEmail();
        String displayName = user.getDisplayName();
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("email", email != null ? email : "");
        userData.put("displayName", displayName != null ? displayName : "");
        userData.put("tasks", user.getTasks() != null ? user.getTasks() : new java.util.ArrayList<>());
        userData.put("totalStudyMinutes", user.getTotalStudyMinutes());
        
        db.collection("users").document(uid).set(userData);
    }
    
    public void updateUser(User user) {
        saveUser(user);
    }
}