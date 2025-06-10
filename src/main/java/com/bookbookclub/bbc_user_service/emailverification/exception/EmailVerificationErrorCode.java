package com.bookbookclub.bbc_user_service.emailverification.exception;

import com.bookbookclub.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum EmailVerificationErrorCode implements BaseErrorCode {

    EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS("TOO_MANY_ATTEMPTS",
            "이메일 인증 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");

    private final String code;
    private final String message;

    EmailVerificationErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
