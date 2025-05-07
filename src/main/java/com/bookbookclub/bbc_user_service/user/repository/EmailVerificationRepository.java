package com.bookbookclub.bbc_user_service.user.repository;

import com.bookbookclub.bbc_user_service.user.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 이메일 인증 엔티티 Repository
 */
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 이메일 기준 단건 조회
    Optional<EmailVerification> findByEmail(String email);

    // 인증 여부 확인
    boolean existsByEmailAndVerifiedIsTrue(String email);
}
