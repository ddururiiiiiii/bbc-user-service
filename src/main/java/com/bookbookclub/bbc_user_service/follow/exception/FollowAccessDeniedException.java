package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.common.exception.BusinessException;

/**
 * 권한 없는 사용자가 팔로우 작업을 시도할 때 발생하는 예외
 */
public class FollowAccessDeniedException extends BusinessException {
    public FollowAccessDeniedException() {
        super(FollowErrorCode.FOLLOW_ACCESS_DENIED);
    }
}
