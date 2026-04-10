package com.example.JWT_Authentication_REST_API_SERVER.controller;

import com.example.JWT_Authentication_REST_API_SERVER.dto.request.*;
import com.example.JWT_Authentication_REST_API_SERVER.dto.response.*;
import com.example.JWT_Authentication_REST_API_SERVER.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ─────────────────────────────────────────
    // 회원가입 POST /api/auth/signup
    // ─────────────────────────────────────────
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(
            @Valid @RequestBody SignUpRequestDto request) {

        UserResponseDto response = userService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)  // 201
                .body(response);
    }

    // ─────────────────────────────────────────
    // 로그인 POST /api/auth/login
    // ─────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        TokenResponseDto response = userService.login(request);

        return ResponseEntity.ok(response);  // 200
    }

    // ─────────────────────────────────────────
    // 로그아웃 POST /api/auth/logout
    // ─────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT Stateless 방식 → 클라이언트가 토큰 삭제하면 됨
        // 서버에서는 별도 처리 없이 200 응답만 반환
        return ResponseEntity.ok().build();
    }
}