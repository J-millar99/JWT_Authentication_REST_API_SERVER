package com.example.JWT_Authentication_REST_API_SERVER.service;

import com.example.JWT_Authentication_REST_API_SERVER.Entity.Role;
import com.example.JWT_Authentication_REST_API_SERVER.Entity.User;
import com.example.JWT_Authentication_REST_API_SERVER.Repository.UserRepository;
import com.example.JWT_Authentication_REST_API_SERVER.dto.request.*;
import com.example.JWT_Authentication_REST_API_SERVER.dto.response.*;
import com.example.JWT_Authentication_REST_API_SERVER.exception.*;
import com.example.JWT_Authentication_REST_API_SERVER.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // ─────────────────────────────────────────
    // 회원가입
    // ─────────────────────────────────────────
    @Transactional
    public UserResponseDto signUp(SignUpRequestDto request) {

        // 1. 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        // 2. 비밀번호 암호화 후 User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt 암호화
                .role(Role.USER)      // 기본 역할은 USER
                .name(request.getName())
                .age(request.getAge())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        // 3. DB 저장
        User savedUser = userRepository.save(user);
        log.info("회원가입 성공 - username: {}", savedUser.getUsername());

        return UserResponseDto.from(savedUser);
    }

    // ─────────────────────────────────────────
    // 로그인
    // ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto request) {

        try {
            // 1. AuthenticationManager로 username + password 검증
            // 내부적으로 CustomUserDetailsService.loadUserByUsername() 호출 후
            // BCrypt로 비밀번호 비교까지 자동 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 2. 인증 성공 → UserDetails에서 username, role 추출
            String username = authentication.getName();
            String role = authentication.getAuthorities()
                    .iterator().next()
                    .getAuthority()
                    .replace("ROLE_", ""); // "ROLE_USER" → "USER"

            // 3. Access Token 발급
            String accessToken = jwtUtil.generateAccessToken(username, role);
            log.info("로그인 성공 - username: {}", username);

            return TokenResponseDto.of(accessToken);

        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        } catch (DisabledException | LockedException e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    // ─────────────────────────────────────────
    // 회원정보 조회
    // ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserResponseDto.from(user);
    }

    // ─────────────────────────────────────────
    // 회원정보 수정
    // ─────────────────────────────────────────
    @Transactional
    public UserResponseDto updateMyInfo(String username, UserUpdateRequestDto request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이메일 변경 시 중복 검사 (본인 이메일 제외)
        if (request.getEmail() != null
                && !request.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 전화번호 변경 시 중복 검사 (본인 전화번호 제외)
        if (request.getPhone() != null
                && !request.getPhone().equals(user.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        // User 엔티티 업데이트 (변경 감지 - Dirty Checking)
        user.update(
                request.getPassword() != null
                        ? passwordEncoder.encode(request.getPassword()) : null,
                request.getAge(),
                request.getPhone(),
                request.getEmail()
        );

        log.info("회원정보 수정 성공 - username: {}", username);

        return UserResponseDto.from(user);
    }

    // ─────────────────────────────────────────
    // 회원탈퇴
    // ─────────────────────────────────────────
    @Transactional
    public void deleteMyAccount(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == Role.ADMIN) {
            log.warn("관리자 계정 삭제 시도 거부.");
            throw new CustomException(ErrorCode.CANNOT_DELETE_ADMIN);
        }

        userRepository.delete(user);
        log.info("회원탈퇴 성공 - username: {}", username);
    }
}