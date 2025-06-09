package com.bookbookclub.bbc_user_service.emailverification.service;


import com.bookbookclub.bbc_user_service.emailverification.entity.EmailVerification;
import com.bookbookclub.bbc_user_service.emailverification.exception.EmailVerificationLimitExceededException;
import com.bookbookclub.bbc_user_service.emailverification.repository.EmailVerificationRepository;
import com.bookbookclub.bbc_user_service.user.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 이메일 인증 비즈니스 로직 처리 서비스
 * - 인증 메일 전송
 * - 토큰 검증
 * - 실패 횟수 제한 및 Redis 연동
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    private static final int MAX_FAIL_COUNT = 5;
    private static final long FAIL_EXPIRE_MINUTES = 10;

    /**
     * 인증 메일 전송
     * - UUID 토큰 생성 후 Redis에 저장 (10분 유효)
     */
    public void sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString();
        String redisKey = "email:verify:" + token;

        redisTemplate.opsForValue().set(redisKey, email, 10, TimeUnit.MINUTES);

        String link = "http://localhost:8081/api/email/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[북북클럽] 이메일 인증");
        message.setText("이메일 인증을 위해 아래 링크를 클릭해주세요:\n" + link);

        mailSender.send(message);
    }

    /**
     * 인증 토큰 검증
     * - Redis에서 토큰 검증 후 DB에 인증 상태 저장
     * - 실패 시 시도 횟수 제한 적용
     */
    public boolean verifyEmail(String token) {
        String redisKey = "email:verify:" + token;
        String email = redisTemplate.opsForValue().get(redisKey);

        if (email == null) {
            throw new InvalidTokenException(); // 유효하지 않은 토큰 예외
        }

        checkFailLimit(email); // 실패 횟수 체크
        redisTemplate.delete(redisKey); // 사용한 토큰 제거

        Optional<EmailVerification> optional = emailVerificationRepository.findByEmail(email);

        EmailVerification verification = optional
                .map(existing -> {
                    existing.markAsVerified();
                    return existing;
                })
                .orElseGet(() -> EmailVerification.builder()
                        .email(email)
                        .verified(true)
                        .verifiedAt(LocalDateTime.now())
                        .build());

        verification.markAsVerified();
        emailVerificationRepository.save(verification);

        resetFailCount(email); // 성공 시 실패 카운트 초기화
        return true;
    }

    /**
     * 이메일 인증 여부 확인
     */
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.existsByEmailAndVerifiedIsTrue(email);
    }

    /**
     * 인증 실패 횟수 증가
     * - Redis에 저장, 일정 횟수 초과 시 예외 발생
     */
    private void checkFailLimit(String email) {
        String failKey = "email:fail:" + email;
        Long count = redisTemplate.opsForValue().increment(failKey);

        if (count == 1) {
            redisTemplate.expire(failKey, FAIL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        }

        if (count >= MAX_FAIL_COUNT) {
            throw new EmailVerificationLimitExceededException();
        }
    }

    /**
     * 인증 성공 시 실패 횟수 초기화
     */
    private void resetFailCount(String email) {
        String failKey = "email:fail:" + email;
        redisTemplate.delete(failKey);
    }
}
