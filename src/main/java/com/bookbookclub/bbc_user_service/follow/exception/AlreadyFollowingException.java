package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.bbc_user_service.follow.exception.FollowErrorCode;

import lombok.Getter;

@Getter
public class AlreadyFollowingException extends RuntimeException {
    private final FollowErrorCode errorCode;

    public AlreadyFollowingException() {
        super(FollowErrorCode.FOLLOW_NOT_FOUND.getMessage());
        this.errorCode = FollowErrorCode.FOLLOW_NOT_FOUND;
    }
}
