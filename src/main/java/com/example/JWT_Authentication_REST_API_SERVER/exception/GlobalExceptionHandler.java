package com.example.JWT_Authentication_REST_API_SERVER.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 비즈니스 예외 처리 (중복 아이디, 잘못된 비밀번호 등)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.warn("CustomException 발생: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    // @Valid 유효성 검사 실패 처리 (빈 값, 형식 오류 등)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {

        // 여러 필드 오류를 하나의 메시지로 합치기
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("유효성 검사 실패: {}", errorMessage);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .status(400)
                        .error("BAD_REQUEST")
                        .message(errorMessage)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 그 외 예상치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.builder()
                        .status(500)
                        .error("INTERNAL_SERVER_ERROR")
                        .message("서버 내부 오류가 발생했습니다.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}