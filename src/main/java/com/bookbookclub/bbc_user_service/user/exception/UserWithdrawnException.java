package com.bookbookclub.bbc_user_service.user.exception;


import lombok.Getter;

/**
 * 탈퇴한 사용자가 로그인 시도할 때 발생
 */
@Getter
public class UserWithdrawnException extends RuntimeException {
    private final UserErrorCode errorCode;

    public UserWithdrawnException() {
        super(UserErrorCode.USER_ALREADY_WITHDRAWN.getMessage());
        this.errorCode = UserErrorCode.USER_ALREADY_WITHDRAWN;
    }
}
