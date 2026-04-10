package com.example.JWT_Authentication_REST_API_SERVER.security;

import com.example.JWT_Authentication_REST_API_SERVER.Entity.User;
import com.example.JWT_Authentication_REST_API_SERVER.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // DB에서 username으로 유저 조회, 없으면 예외 발생
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "사용자를 찾을 수 없습니다: " + username));

        // Spring Security가 사용하는 UserDetails 객체로 변환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                // Role Enum → "ROLE_USER" / "ROLE_ADMIN" 형식으로 변환
                .authorities(List.of(new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name())))
                .build();
    }
}