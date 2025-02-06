package com.example.iplan.auth;

import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

// 클라이언트 요청 수신
// 구글 소셜 로그인: /api/auth/google

@Tag(name = "구글 로그인 컨트롤러", description = "구글 로그인 토큰을 이용하여 Firebase Authorization을 통해 로그인한다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/social")
public class AuthController {


    /**
     * Google ID 토큰으로 로그인.
     *
     * @param idToken 클라이언트로부터 받은 ID 토큰
     * @return 성공 메시지와 사용자 정보
     */
    private final UserAuthSocialService authService;
    private final UserSocialRepository userSocialRepository;

    @Operation(summary = "구글 로그인", description = "구글 로그인으로 발급된 ID Token을 통해 파이어베이스 인증을 실시한다.",
            parameters = {@Parameter(name = "idToken", description = "구글 로그인으로 발급된 Id토큰", example = "{ \"idToken\": \"dfslkg\" }")})
    @PostMapping("/google")
    public String verifyIdToken (@RequestBody Map<String, String> requestBody) {

        String idToken = requestBody.get("idToken");

        try {
            UserSocial user = authService.VerifyTokenAndGenerateUser(idToken);
            if(userSocialRepository.findByFields(Map.of("id", user.getId())) == null){
                authService.SaveUser(user);
            }
            return "User authenticated and saved: " + user.getName();
        } catch (FirebaseAuthException | ExecutionException | InterruptedException e) {
            return "Error during token verification: " + e.getMessage();
        }

    }
}
