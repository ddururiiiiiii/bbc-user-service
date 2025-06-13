package com.bookbookclub.bbc_user_service.emailverification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 이메일 인증 기록 엔티티
 *
 * - 이메일 주소별 인증 여부 및 시각 저장
 * - 회원가입 전 인증 확인용으로 사용됨
 * - 인증 완료 시 `verified = true`, `verifiedAt`에 시각 저장
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 인증 요청한 이메일 */
    @Column(nullable = false, unique = true)
    private String email;

    /** 인증 성공 여부 */
    @Column(nullable = false)
    private boolean verified = false;

    /** 인증 완료 시각 (nullable) */
    private LocalDateTime verifiedAt;

    /**
     * 인증 완료 처리
     * - verified = true
     * - verifiedAt = 현재 시간
     */
    public void markAsVerified() {
        this.verified = true;
        this.verifiedAt = LocalDateTime.now();
    }

    /**
     * 초기 인증 상태 생성
     */
    public static EmailVerification create(String email) {
        EmailVerification verification = new EmailVerification();
        verification.email = email;
        verification.verified = true;
        verification.verifiedAt = LocalDateTime.now();
        return verification;
    }
}