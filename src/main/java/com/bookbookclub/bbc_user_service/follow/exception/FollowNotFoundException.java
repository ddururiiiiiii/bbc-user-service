package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.common.exception.BusinessException;

/**
 * 언팔로우 등에서 팔로우 관계를 찾지 못했을 때 발생하는 예외
 */
public class FollowNotFoundException extends BusinessException {
    public FollowNotFoundException() {
        super(FollowErrorCode.FOLLOW_NOT_FOUND);
    }
}
