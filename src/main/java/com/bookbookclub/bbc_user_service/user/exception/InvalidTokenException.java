package com.bookbookclub.bbc_user_service.user.exception;


import com.bookbookclub.bbc_user_service.global.exception.ErrorCode;
import lombok.Getter;

/**
 * 잘못된 또는 만료된 인증 토큰 예외
 */
@Getter
public class InvalidTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidTokenException() {
        super(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN.getMessage());
        this.errorCode = ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN;
    }
}
