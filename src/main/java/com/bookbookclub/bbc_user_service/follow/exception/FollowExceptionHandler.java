package com.bookbookclub.bbc_user_service.follow.exception;

import com.bookbookclub.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 팔로우 관련 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.bookbookclub.bbc_user_service.follow")
public class FollowExceptionHandler {

    /**
     * 이미 팔로우한 경우 예외 처리
     */
    @ExceptionHandler(AlreadyFollowingException.class)
    public ApiResponse<?> handleAlreadyFollowing(AlreadyFollowingException e) {
        log.warn("Already following user: {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    /**
     * 팔로우 관계가 존재하지 않는 경우 예외 처리
     */
    @ExceptionHandler(FollowNotFoundException.class)
    public ApiResponse<?> handleFollowNotFound(FollowNotFoundException e) {
        log.warn("Follow relationship not found: {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    /**
     * 본인 외 접근 불가 예외 처리
     */
    @ExceptionHandler(FollowAccessDeniedException.class)
    public ApiResponse<?> handleAccessDenied(FollowAccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }
}
