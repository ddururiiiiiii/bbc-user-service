package com.bookbookclub.bbc_user_service.emailverification.service;


import com.bookbookclub.bbc_user_service.emailverification.entity.EmailVerification;
import com.bookbookclub.bbc_user_service.emailverification.repository.EmailVerificationRepository;
import com.bookbookclub.bbc_user_service.user.config.EmailVerificationProperties;
import com.bookbookclub.bbc_user_service.emailverification.exception.EmailVerificationException;
import com.bookbookclub.bbc_user_service.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 이메일 인증 비즈니스 로직 처리 서비스
 * - 인증 메일 전송
 * - 토큰 검증 및 인증 상태 저장
 * - 실패 횟수 제한 관리 (Redis)
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailVerificationProperties emailProperties;

    /**
     * 인증 메일 전송
     * - UUID 토큰 생성 후 Redis에 저장
     */
    public void sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString();
        String redisKey = "email:verify:" + token;
        redisTemplate.opsForValue().set(
                redisKey,
                email,
                emailProperties.getTokenExpireMinutes(),
                TimeUnit.MINUTES
        );

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
     */
    public boolean verifyEmail(String token) {
        String redisKey = "email:verify:" + token;
        String email = redisTemplate.opsForValue().get(redisKey);

        if (email == null) {
            throw new EmailVerificationException(UserErrorCode.EMAIL_VERIFICATION_INVALID_TOKEN);
        }

        checkFailLimit(email);
        redisTemplate.delete(redisKey);

        Optional<EmailVerification> optional = emailVerificationRepository.findByEmail(email);

        EmailVerification verification = optional
                .map(existing -> {
                    existing.markAsVerified();
                    return existing;
                })
                .orElseGet(() -> EmailVerification.create(email));

        emailVerificationRepository.save(verification);
        resetFailCount(email);

        return true;
    }

    /**
     * 이메일 인증 여부 확인
     */
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.existsByEmailAndVerifiedIsTrue(email);
    }

    // 실패 횟수 확인 및 제한
    private void checkFailLimit(String email) {
        String failKey = "email:fail:" + email;
        Long count = redisTemplate.opsForValue().increment(failKey);

        if (count == 1) {
            redisTemplate.expire(failKey, emailProperties.getFailExpireMinutes(), TimeUnit.MINUTES);
        }

        if (count >= emailProperties.getMaxFailCount()) {
            throw new EmailVerificationException(UserErrorCode.EMAIL_VERIFICATION_LIMIT_EXCEEDED);
        }
    }

    // 실패 횟수 초기화
    private void resetFailCount(String email) {
        String failKey = "email:fail:" + email;
        redisTemplate.delete(failKey);
    }

}