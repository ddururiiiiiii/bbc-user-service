package com.bookbookclub.bbc_user_service.user.exception;

import com.bookbookclub.bbc_user_service.global.exception.ErrorCode;
import lombok.Getter;

/**
 * 로그인 시 비밀번호 불일치 예외
 */
@Getter
public class InvalidPasswordException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD.getMessage());
        this.errorCode = ErrorCode.INVALID_PASSWORD;
    }
}
