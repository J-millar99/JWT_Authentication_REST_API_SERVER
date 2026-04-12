package com.example.JWT_Authentication_REST_API_SERVER.service;

import com.example.JWT_Authentication_REST_API_SERVER.Entity.User;
import com.example.JWT_Authentication_REST_API_SERVER.Repository.UserRepository;
import com.example.JWT_Authentication_REST_API_SERVER.dto.response.UserResponseDto;
import com.example.JWT_Authentication_REST_API_SERVER.exception.CustomException;
import com.example.JWT_Authentication_REST_API_SERVER.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();

        // 1. 리스트가 비어있는지 확인 후 예외 발생
        if (users.isEmpty()) {
            log.warn("전체 유저 조회 실패: 등록된 유저가 없습니다.");
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 데이터가 있을 경우 DTO 변환 진행
        List<UserResponseDto> allUsers = users.stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());

        log.info("모든 유저 조회 성공 (총 {}명)", allUsers.size());
        return allUsers;
    }
}