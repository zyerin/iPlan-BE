package com.example.iplan.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // UserService 의 로그인 과정에서 호출 됨 -> 디비에서 해당 이메일을 가진 사용자 조회
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::createUserDetails)   // CustomUserDetails 객체 생성 후 반환
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 CustomUserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(Users users) {
        return CustomUserDetails.builder()
                .username(users.getEmail())
                .password(users.getPassword())  // 디비에서 암호화된 비밀번호 가져옴
                .role(UserRole.fromString(users.getAuthority()))  // 사용자 권한 설정 -> 문자열(child, parent)을 Enum 으로 변환
                .build();
    }

}

