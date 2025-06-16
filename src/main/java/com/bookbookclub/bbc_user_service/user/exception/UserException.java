package com.bookbookclub.bbc_user_service.user.exception;

import com.bookbookclub.common.exception.BaseErrorCode;
import com.bookbookclub.common.exception.BusinessException;

/**
 * 인증/인가 관련 예외를 통합 처리하는 클래스
 */
public class UserException extends BusinessException {
    public UserException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
