package com.example.iplan.config;

import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final FirebaseAuth firebaseAuth;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                // 기본 설정인 Session 방식을 사용하지 않고 JWT를 사용하기 위해 STATELESS로 처리
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //addFilterBefore({등록할 필터}, {특정 필터})
                //-> 특정 필터 앞에 등록할 필터를 추가
                .addFilterBefore(new JwtTokenFilter(firebaseAuth), UsernamePasswordAuthenticationFilter.class)
                .cors(withDefaults())
                //기본 CORS 구성 사용

                .csrf(csrf -> csrf.disable())
                //Fluent API 방식을 이용해 CSRF 비 활성화

                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                                .anyRequest().authenticated()
                )
                //모든 인증 되지 않은 요청을 허락 -> 로그인 하지 않아도 모든 페이지 접근 가능

                .logout(logout -> logout.disable());

        return http.build();
    }
}


