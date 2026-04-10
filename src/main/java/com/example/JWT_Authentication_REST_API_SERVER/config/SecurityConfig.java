package com.example.JWT_Authentication_REST_API_SERVER.config;

import com.example.JWT_Authentication_REST_API_SERVER.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 방식에서는 불필요)
            .csrf(AbstractHttpConfigurer::disable)

            // Form 로그인 비활성화 (REST API 방식)
            .formLogin(AbstractHttpConfigurer::disable)

            // HTTP Basic 인증 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)

            // 세션 사용 안 함 (JWT = Stateless)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 경로별 인가 설정
            .authorizeHttpRequests(auth -> auth
                // 회원가입, 로그인은 인증 없이 허용
                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // ADMIN 전용 경로 (추후 필요 시 사용)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )

            // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 비밀번호 암호화 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean (Service에서 로그인 시 사용)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}