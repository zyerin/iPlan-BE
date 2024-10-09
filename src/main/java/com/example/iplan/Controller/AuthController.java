package com.example.iplan.Controller;

import com.example.iplan.Domain.UserSocial;
import com.example.iplan.Service.UserAuthSocialService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

// 클라이언트 요청 수신
// 구글 소셜 로그인: /api/auth/google

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    /**
     * Google ID 토큰으로 로그인.
     *
     * @param idToken 클라이언트로부터 받은 ID 토큰
     * @return 성공 메시지와 사용자 정보
     */
    private final UserAuthSocialService authService;

    @Autowired
    public AuthController(UserAuthSocialService authService) {
        this.authService = authService;
    }

    @PostMapping("/google")
    public String verifyIdToken(@RequestBody Map<String, String> requestBody) {
        String idToken = requestBody.get("idToken");
        try {
            UserSocial user = authService.verifyIdTokenAndSaveUser(idToken);
            return "User authenticated and saved: " + user.getName();
        } catch (FirebaseAuthException | ExecutionException | InterruptedException e) {
            return "Error during token verification: " + e.getMessage();
        }
    }
}
