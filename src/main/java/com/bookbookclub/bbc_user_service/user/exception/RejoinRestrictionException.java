package com.bookbookclub.bbc_user_service.user.exception;

import lombok.Getter;

/**
 * 탈퇴 후 재가입 제한 기간 내 시도 시 발생
 */
@Getter
public class RejoinRestrictionException extends RuntimeException {
    private final UserErrorCode errorCode;

    public RejoinRestrictionException() {
        super(UserErrorCode.REJOIN_NOT_ALLOWED.getMessage());
        this.errorCode = UserErrorCode.REJOIN_NOT_ALLOWED;
    }
}
