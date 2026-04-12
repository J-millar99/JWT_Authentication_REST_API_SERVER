package com.example.JWT_Authentication_REST_API_SERVER.dto.response;

import com.example.JWT_Authentication_REST_API_SERVER.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String name;
    private Integer age;
    private String phone;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .age(user.getAge())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
