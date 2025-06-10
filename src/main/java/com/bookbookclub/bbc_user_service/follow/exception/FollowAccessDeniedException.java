package com.bookbookclub.bbc_user_service.follow.exception;


import lombok.Getter;

@Getter
public class FollowAccessDeniedException extends RuntimeException {
    private final FollowErrorCode errorCode;

    public FollowAccessDeniedException() {
        super(FollowErrorCode.FOLLOW_ACCESS_DENIED.getMessage());
        this.errorCode = FollowErrorCode.FOLLOW_ACCESS_DENIED;
    }
}