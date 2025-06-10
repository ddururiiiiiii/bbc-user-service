package com.bookbookclub.bbc_user_service.user.exception;

import lombok.Getter;

/**
 * 로그인 시 비밀번호 불일치 예외
 */
@Getter
public class InvalidPasswordException extends RuntimeException {
    private final UserErrorCode errorCode;

    public InvalidPasswordException() {
        super(UserErrorCode.PASSWORD_INVALID.getMessage());
        this.errorCode = UserErrorCode.PASSWORD_INVALID;
    }
}
