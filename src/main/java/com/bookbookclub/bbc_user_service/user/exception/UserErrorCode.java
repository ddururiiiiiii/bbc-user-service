package com.bookbookclub.bbc_user_service.user.exception;

import com.bookbookclub.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "존재하지 않는 사용자입니다."),
    USER_ALREADY_WITHDRAWN("USER_ALREADY_WITHDRAWN", "탈퇴한 사용자입니다."),
    EMAIL_DUPLICATED("EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."),
    NICKNAME_DUPLICATED("NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."),
    PASSWORD_INVALID("PASSWORD_INVALID", "비밀번호가 일치하지 않습니다."),
    TOKEN_INVALID("TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    REJOIN_NOT_ALLOWED("REJOIN_NOT_ALLOWED", "재가입이 제한된 사용자입니다.");

    private final String code;
    private final String message;
}