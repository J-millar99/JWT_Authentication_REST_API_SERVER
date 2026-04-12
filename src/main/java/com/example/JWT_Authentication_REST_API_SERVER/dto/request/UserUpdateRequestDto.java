package com.example.JWT_Authentication_REST_API_SERVER.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    @Max(value = 150, message = "나이는 150 이상이어야 합니다.")
    private Integer age;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$")
    private String phone;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
