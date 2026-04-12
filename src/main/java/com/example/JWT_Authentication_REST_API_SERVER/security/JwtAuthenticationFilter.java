package com.example.JWT_Authentication_REST_API_SERVER.security;

import com.example.JWT_Authentication_REST_API_SERVER.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. 헤더에서 토큰 추출
            String token = extractToken(request);

            // 2. 토큰이 존재하고 유효한 경우에만 인증 처리
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {

                // 3. 토큰에서 username 추출
                String username = jwtUtil.getUsername(token);

                // 4. DB에서 유저 정보 로드
                UserDetails userDetails =
                        customUserDetailsService.loadUserByUsername(username);

                // 5. SecurityContext에 인증 정보 등록
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,  // 인증 후이므로 credentials는 null
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 인증 성공 - username: {}", username);
            }

        } catch (ExpiredJwtException e) {
            // 만료된 토큰 → 401 응답
            log.warn("만료된 토큰 요청: {}", e.getMessage());
            setErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
            return;
        } catch (Exception e) {
            // 그 외 유효하지 않은 토큰 → 401 응답
            log.warn("유효하지 않은 토큰 요청: {}", e.getMessage());
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
            return;
        }

        // 6. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 "Bearer " 제거 후 토큰만 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // 필터에서 JSON 에러 응답 직접 작성
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode)
            throws IOException {

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 직렬화

        String errorBody = objectMapper.writeValueAsString(
                com.example.JWT_Authentication_REST_API_SERVER.exception.ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .message(errorCode.getMessage())
                        .timestamp(java.time.LocalDateTime.now())
                        .build()
        );

        response.getWriter().write(errorBody);
    }
}