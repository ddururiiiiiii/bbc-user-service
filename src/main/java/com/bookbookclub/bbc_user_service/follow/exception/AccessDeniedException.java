package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.bbc_user_service.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {
    private final ErrorCode errorCode;

    public AccessDeniedException(String message) {
        super(message);
        this.errorCode = ErrorCode.ACCESS_DENIED;
    }
}