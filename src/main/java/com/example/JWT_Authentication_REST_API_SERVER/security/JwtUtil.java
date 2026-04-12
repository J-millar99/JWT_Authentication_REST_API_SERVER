package com.example.JWT_Authentication_REST_API_SERVER.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    // application.yml 값 주입
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        // secret key를 Base64 디코딩하여 HMAC-SHA 키 생성
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // ─────────────────────────────────────────
    // 토큰 생성
    // ─────────────────────────────────────────

    // Access Token 생성
    public String generateAccessToken(String username, String role) {
        return buildToken(username, role, accessTokenExpiration);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String username) {
        return buildToken(username, null, refreshTokenExpiration);
    }

    // 공통 토큰 빌드 메서드
    private String buildToken(String username, String role, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .subject(username)                  // 토큰 주체 (username)
                .issuedAt(now)                      // 발급 시간
                .expiration(expiryDate)             // 만료 시간
                .signWith(secretKey);               // 서명

        // role이 있을 경우에만 claim 추가 (Access Token만 role 포함)
        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    // ─────────────────────────────────────────
    // 토큰 파싱
    // ─────────────────────────────────────────

    // 토큰에서 username 추출
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // 토큰에서 role 추출
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // 토큰 만료 시간 추출
    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    // ─────────────────────────────────────────
    // 토큰 검증
    // ─────────────────────────────────────────

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 파싱 성공 시 유효한 토큰
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰입니다: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT 서명이 올바르지 않습니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다: {}", e.getMessage());
        }
        return false;
    }

    // 토큰 만료 여부만 별도 체크 (Refresh Token 재발급 시 활용)
    public boolean isTokenExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // ─────────────────────────────────────────
    // 내부 파싱 메서드
    // ─────────────────────────────────────────

    // Claims 파싱 (서명 검증 포함)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)  // 서명 검증 키 설정
                .build()
                .parseSignedClaims(token)
                .getPayload();          // Claims 반환
    }
}