package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.common.exception.BusinessException;

/**
 * 이미 팔로우 상태에서 중복 요청 시 발생하는 예외
 */
public class AlreadyFollowingException extends BusinessException {
    public AlreadyFollowingException() {
        super(FollowErrorCode.ALREADY_FOLLOWING);
    }
}
