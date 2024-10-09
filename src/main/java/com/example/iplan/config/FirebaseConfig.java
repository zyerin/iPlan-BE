package com.example.iplan.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/iplan-firebase.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth getFirebaseAuth() {
        try {
            return FirebaseAuth.getInstance(firebaseApp());
        } catch (IOException e) {
            throw new RuntimeException("Firebase initialization error", e);
        }
    }

    @Bean
    public Firestore firestore() throws IOException {
        return FirestoreClient.getFirestore(firebaseApp());
    }
}
