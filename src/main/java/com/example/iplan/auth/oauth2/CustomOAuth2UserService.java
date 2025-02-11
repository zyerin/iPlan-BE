package com.example.iplan.auth;

import com.example.iplan.config.jwt.JwtToken;
import com.example.iplan.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 플랫폼별 사용자 정보 추출
        String email;
        if ("naver".equals(registrationId)) {
            email = ((Map<String, Object>) attributes.get("response")).get("email").toString();
        } else if ("kakao".equals(registrationId)) {
            email = ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
        } else {
            email = attributes.get("email").toString(); // Google
        }

        Optional<Users> existingUser = userRepository.findByEmail(email);
        Users user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = Users.builder()
                    .email(email)
                    .name(email.split("@")[0])
                    .authority("CHILD") // 기본 권한 설정
                    .build();
            userRepository.save(user);
        }

        // 🔹 JWT 토큰 발급
        JwtToken jwtToken = jwtTokenProvider.generateToken(new CustomUserDetails(user));

        return new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority(user.getAuthority())),
                attributes, userNameAttributeName);
    }
}
