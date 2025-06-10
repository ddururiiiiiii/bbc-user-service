package com.bookbookclub.bbc_user_service.user.exception;

import lombok.Getter;

/**
 * 중복 이메일 예외
 * - 회원가입 시 중복 이메일 검사에 사용
 */
@Getter
public class DuplicateEmailException extends RuntimeException {
    private final UserErrorCode errorCode;

    public DuplicateEmailException() {
        super(UserErrorCode.EMAIL_DUPLICATED.getMessage());
        this.errorCode = UserErrorCode.EMAIL_DUPLICATED;
    }
}
