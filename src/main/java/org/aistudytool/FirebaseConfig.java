package org.aistudytool;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {
    private static FirebaseApp firebaseApp;
    
    public static void initialize() throws IOException {
        if (firebaseApp == null) {
            FileInputStream serviceAccount = 
                new FileInputStream("firebase_creds.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            firebaseApp = FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully");
        }
    }
    
    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance(firebaseApp);
    }
    
    public static Firestore getFirestore() {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}