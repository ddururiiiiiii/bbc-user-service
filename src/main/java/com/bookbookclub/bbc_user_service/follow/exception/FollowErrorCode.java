package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum FollowErrorCode implements BaseErrorCode {

    FOLLOW_ALREADY_EXISTS("FOLLOW_ALREADY_EXISTS", "이미 팔로우한 사용자입니다."),
    FOLLOW_NOT_FOUND("FOLLOW_NOT_FOUND", "팔로우 관계를 찾을 수 없습니다."),
    FOLLOW_ACCESS_DENIED("FOLLOW_ACCESS_DENIED", "본인 계정으로만 요청할 수 없습니다.");

    private final String code;
    private final String message;

    FollowErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
