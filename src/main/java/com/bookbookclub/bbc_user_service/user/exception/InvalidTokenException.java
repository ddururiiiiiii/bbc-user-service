package com.bookbookclub.bbc_user_service.user.exception;


import lombok.Getter;

/**
 * 잘못된 또는 만료된 인증 토큰 예외
 */
@Getter
public class InvalidTokenException extends RuntimeException {
    private final UserErrorCode errorCode;

    public InvalidTokenException() {
        super(UserErrorCode.TOKEN_INVALID.getMessage());
        this.errorCode = UserErrorCode.TOKEN_INVALID;
    }
}
