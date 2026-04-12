package com.example.JWT_Authentication_REST_API_SERVER.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false) // BCrypt 암호화 시 길어지므로 기본값(255) 유지
    private String password;

    @Enumerated(EnumType.STRING) // Enum 문자열 그대로 DB 저장
    @Column(nullable = false)
    private Role role; // 직접 만든 Enum 클래스 사용

    @Column(nullable = false, length = 50)
    private String name;

    private Integer age; // Null 허용을 위해 Integer 권장

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 생성자나 빌더 패턴을 추가하면 가입 로직 구현이 편해집니다.
    @Builder
    public User(String username, String password, Role role, String name, Integer age, String phone, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.email = email;
    }

    // User.java에 추가
    public void update(String password, Integer age, String phone, String email) {
        // null이 아닌 값만 선택적으로 업데이트
        if (password != null) this.password = password;
        if (age != null) this.age = age;
        if (phone != null) this.phone = phone;
        if (email != null) this.email = email;
    }
}
