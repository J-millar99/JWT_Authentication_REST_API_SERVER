package com.example.JWT_Authentication_REST_API_SERVER.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 추상 클래스를 상속받는 엔티티들이 아래 필드를 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 포함
public abstract class BaseTimeEntity {

    @CreatedDate // 데이터 생성 시 자동 저장
    @Column(updatable = false) // 수정 시에는 건드리지 않음
    private LocalDateTime createdAt;

    @LastModifiedDate // 데이터 수정 시 자동 저장
    private LocalDateTime updatedAt;
}
