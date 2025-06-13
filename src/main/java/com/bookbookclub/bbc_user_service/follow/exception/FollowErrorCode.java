package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.common.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 팔로우 도메인 관련 에러 코드 정의
 */
public enum FollowErrorCode implements BaseErrorCode {

    FOLLOW_NOT_FOUND(404, "F001", "팔로우 관계를 찾을 수 없습니다."),
    ALREADY_FOLLOWING(400, "F002", "이미 팔로우한 사용자입니다."),
    FOLLOW_ACCESS_DENIED(403, "F003", "해당 팔로우 작업은 허용되지 않습니다.");

    private final int statusCode;
    private final String code;
    private final String message;

    FollowErrorCode(int statusCode, String code, String message) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
