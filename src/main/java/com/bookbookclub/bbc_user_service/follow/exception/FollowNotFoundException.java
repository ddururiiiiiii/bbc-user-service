package com.bookbookclub.bbc_user_service.follow.exception;

import lombok.Getter;

@Getter
public class FollowNotFoundException extends RuntimeException {
    private final FollowErrorCode errorCode;

    public FollowNotFoundException() {
        super(FollowErrorCode.FOLLOW_NOT_FOUND.getMessage());
        this.errorCode = FollowErrorCode.FOLLOW_NOT_FOUND;
    }
}