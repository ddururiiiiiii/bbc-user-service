package com.bookbookclub.bbc_user_service.emailverification.exception;


import lombok.Getter;

/**
 * 이메일 인증 시도 횟수 초과 예외
 * - 보안 또는 남용 방지를 위해 사용
 */
@Getter
public class EmailVerificationLimitExceededException extends RuntimeException {
    private final EmailVerificationErrorCode errorCode;

    public EmailVerificationLimitExceededException() {
        super(EmailVerificationErrorCode.EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS.getMessage());
        this.errorCode = EmailVerificationErrorCode.EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS;
    }
}
