package com.bookbookclub.bbc_user_service.user.exception;

import lombok.Getter;

/**
 * 사용자 정보를 찾을 수 없을 때 발생
 */
@Getter
public class UserNotFoundException extends RuntimeException {
    private final UserErrorCode errorCode;

    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND.getMessage());
        this.errorCode = UserErrorCode.USER_NOT_FOUND;
    }
}
