package com.bookbookclub.bbc_user_service.user.exception;

import com.bookbookclub.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 사용자 도메인 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.bookbookclub.bbc_user_service.user")
public class UserExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ApiResponse<?> handleDuplicateEmailException(DuplicateEmailException e) {
        log.warn("[DuplicateEmailException] {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ApiResponse<?> handleDuplicateNicknameException(DuplicateNicknameException e) {
        log.warn("[DuplicateNicknameException] {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ApiResponse<?> handleInvalidPasswordException(InvalidPasswordException e) {
        log.warn("[InvalidPasswordException] {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ApiResponse<?> handleInvalidTokenException(InvalidTokenException e) {
        log.warn("[InvalidTokenException] {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(RejoinRestrictionException.class)
    public ApiResponse<?> handleRejoinRestrictionException(RejoinRestrictionException e) {
        log.warn("[RejoinRestrictionException] {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<?> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("[UserNotFoundException] {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(UserWithdrawnException.class)
    public ApiResponse<?> handleUserWithdrawnException(UserWithdrawnException e) {
        log.warn("[UserWithdrawnException] {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }
}
