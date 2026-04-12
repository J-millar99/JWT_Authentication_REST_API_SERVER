package com.example.JWT_Authentication_REST_API_SERVER.controller;

import com.example.JWT_Authentication_REST_API_SERVER.Entity.User;
import com.example.JWT_Authentication_REST_API_SERVER.dto.response.UserResponseDto;
import com.example.JWT_Authentication_REST_API_SERVER.service.AdminService;
import com.example.JWT_Authentication_REST_API_SERVER.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.findAllUsers());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> forceDeleteUser(@PathVariable String username) {
        log.info("관리자에 의한 계정 삭제 요청. 대상: {}", username);
        userService.deleteMyAccount(username);
        return ResponseEntity.ok("사용자 " + username + "의 계정이 삭제되었습니다.");
    }
}
