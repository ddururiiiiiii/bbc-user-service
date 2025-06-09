package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.bbc_user_service.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyFollowingException extends RuntimeException {
    private final ErrorCode errorCode;

    public AlreadyFollowingException() {
        super(ErrorCode.ALREADY_FOLLOWING.getMessage());
        this.errorCode = ErrorCode.ALREADY_FOLLOWING;
    }
}
