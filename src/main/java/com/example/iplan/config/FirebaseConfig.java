package com.example.iplan.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            // 환경 변수에서 파일 경로를 가져옴, 기본값은 Docker 컨테이너 내부 경로
            String firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH");
            if (firebaseConfigPath == null || firebaseConfigPath.isEmpty()) {
                firebaseConfigPath = "/app/serviceAccountKey.json"; // Docker 컨테이너 내부 경로
            }

            // Firebase 초기화
            FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
