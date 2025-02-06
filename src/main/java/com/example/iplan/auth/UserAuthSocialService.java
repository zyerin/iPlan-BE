package com.example.iplan.Service;

import com.example.iplan.Domain.UserSocial;
import com.example.iplan.Repository.UserSocialRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

// Google 로그인 및 사용자 정보를 Firestore 에 저장

@RequiredArgsConstructor
@Service
public class UserAuthSocialService {

    private final FirebaseAuth firebaseAuth;
    private final UserSocialRepository userRepository;

    /**
     * Google 로그인 ID 토큰을 사용하여 사용자 인증 및 Firestore에 사용자 저장.
     *
     * @param user 사용자 정보
     * @return 저장된 사용자 객체
     * @throws FirebaseAuthException 인증 실패 시 발생
     * @throws ExecutionException Firestore 저장 실패 시 발생
     * @throws InterruptedException Firestore 저장 중단 시 발생
     */
    public UserSocial SaveUser(UserSocial user) throws ExecutionException, InterruptedException {

        // Firestore에 사용자 저장
        userRepository.save(user);

        return user;
    }

    public UserSocial VerifyTokenAndGenerateUser(String idToken) throws FirebaseAuthException {
        // ID 토큰을 사용하여 Firebase에서 토큰 디코딩
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();

        return new UserSocial(uid, name, email);
    }
}
