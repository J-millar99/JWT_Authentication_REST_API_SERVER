package com.example.JWT_Authentication_REST_API_SERVER.Repository;

import com.example.JWT_Authentication_REST_API_SERVER.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인 시 username으로 사용자를 조회
    Optional<User> findByUsername(String username);

    // 회원가입 시 중복 검사
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existByPhone(String phone);
}
