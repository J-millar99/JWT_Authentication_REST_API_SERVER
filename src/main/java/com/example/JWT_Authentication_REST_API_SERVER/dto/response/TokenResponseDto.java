package com.example.JWT_Authentication_REST_API_SERVER.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String tokenType;   // Bearer

    public static TokenResponseDto of(String accessToken) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }
}
