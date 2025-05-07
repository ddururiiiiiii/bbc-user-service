package com.bookbookclub.bbc_user_service.user.exception;

import com.bookbookclub.bbc_user_service.global.exception.ErrorCode;
import lombok.Getter;

/**
 * 중복 닉네임 예외
 * - 닉네임 변경 또는 회원가입 시 사용
 */
@Getter
public class DuplicateNicknameException extends RuntimeException {
    private final ErrorCode errorCode;

    public DuplicateNicknameException() {
        super(ErrorCode.DUPLICATE_NICKNAME.getMessage());
        this.errorCode = ErrorCode.DUPLICATE_NICKNAME;
    }
}
