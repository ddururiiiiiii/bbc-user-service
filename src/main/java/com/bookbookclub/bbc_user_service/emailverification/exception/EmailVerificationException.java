package com.bookbookclub.bbc_user_service.emailverification.exception;

import com.bookbookclub.common.exception.BaseErrorCode;
import com.bookbookclub.common.exception.BusinessException;

/**
 * 이메일 인증 전용 예외 클래스
 */
public class EmailVerificationException extends BusinessException {

    public EmailVerificationException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
