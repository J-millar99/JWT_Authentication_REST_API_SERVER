package com.example.JWT_Authentication_REST_API_SERVER.controller;

import com.example.JWT_Authentication_REST_API_SERVER.dto.request.UserUpdateRequestDto;
import com.example.JWT_Authentication_REST_API_SERVER.dto.response.UserResponseDto;
import com.example.JWT_Authentication_REST_API_SERVER.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ─────────────────────────────────────────
    // 내 정보 조회 GET /api/users/me
    // ─────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal UserDetails userDetails) {

        // @AuthenticationPrincipal → SecurityContext에서 인증된 유저 자동 주입
        UserResponseDto response = userService.getMyInfo(userDetails.getUsername());

        return ResponseEntity.ok(response);  // 200
    }

    // ─────────────────────────────────────────
    // 회원정보 수정 PUT /api/users/me
    // ─────────────────────────────────────────
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequestDto request) {

        UserResponseDto response =
                userService.updateMyInfo(userDetails.getUsername(), request);

        return ResponseEntity.ok(response);  // 200
    }

    // ─────────────────────────────────────────
    // 회원탈퇴 DELETE /api/users/me
    // ─────────────────────────────────────────
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @AuthenticationPrincipal UserDetails userDetails) {

        userService.deleteMyAccount(userDetails.getUsername());

        return ResponseEntity.noContent().build();  // 204
    }
}