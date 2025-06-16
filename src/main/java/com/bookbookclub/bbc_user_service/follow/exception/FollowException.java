package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.common.exception.BusinessException;

public class FollowException extends BusinessException {
    public FollowException(FollowErrorCode errorCode) {
        super(errorCode);
    }
}
