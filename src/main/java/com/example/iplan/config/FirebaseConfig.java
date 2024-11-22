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

    /**
     * Firebase 서비스 전반의 기본적인 초기화 역할을 한다.
     * Firebase SDK의 모든 서비스(Auth, Firestore, Storage)를 구성하고 애플리케이션에서 사용할 수 있도록 함
     * @return
     * @throws IOException
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/iplan-firebase.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    /**
     * FirebaseAuth를 Spring Context에 빈으로 등록
     * Firebase 인증 서비스를 사용하는 데 필요한 객체
     * 사용자의 인증과 관련된 작업(로그인, 로그아웃, 토큰 검증, 사용자 관리 등) 처리
     * @return
     */
    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            return FirebaseAuth.getInstance(firebaseApp());
        } catch (IOException e) {
            throw new RuntimeException("Firebase initialization error", e);
        }
    }

    /**
     * Firebase Firestore 데이터베이스에 접근하는 데 필요한 객체
     * @return
     * @throws IOException
     */
    @Bean
    public Firestore firestore() throws IOException {
        return FirestoreClient.getFirestore(firebaseApp());
    }
}
