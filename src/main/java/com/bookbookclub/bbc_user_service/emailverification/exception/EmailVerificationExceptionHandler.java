package com.bookbookclub.bbc_user_service.emailverification.exception;

import com.bookbookclub.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 이메일 인증 관련 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.bookbookclub.bbc_user_service.emailverification")
public class EmailVerificationExceptionHandler {

    /**
     * 이메일 인증 미완료 예외 처리
     */
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ApiResponse<?> handleEmailNotVerified(EmailNotVerifiedException e) {
        log.warn("Email not verified: {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    /**
     * 이메일 인증 시도 횟수 초과 예외 처리
     */
    @ExceptionHandler(EmailVerificationLimitExceededException.class)
    public ApiResponse<?> handleLimitExceeded(EmailVerificationLimitExceededException e) {
        log.warn("Email verification limit exceeded: {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }
}
