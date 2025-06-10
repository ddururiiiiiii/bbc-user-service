package com.bookbookclub.bbc_user_service.user.exception;

import lombok.Getter;

/**
 * 중복 닉네임 예외
 * - 닉네임 변경 또는 회원가입 시 사용
 */
@Getter
public class DuplicateNicknameException extends RuntimeException {
    private final UserErrorCode errorCode;

    public DuplicateNicknameException() {
        super(UserErrorCode.NICKNAME_DUPLICATED.getMessage());
        this.errorCode = UserErrorCode.NICKNAME_DUPLICATED;
    }
}
