package com.bookbookclub.bbc_user_service.global.exception;


import com.bookbookclub.bbc_user_service.global.common.ApiResponse;
import com.bookbookclub.bbc_user_service.user.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 * - 각 예외 상황에 맞는 HTTP 상태코드 및 응답 구조 반환
 * - ApiResponse.fail(...) 구조 사용
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailNotVerified(EmailNotVerifiedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(UserWithdrawnException.class)
    public ResponseEntity<ApiResponse<Void>> handleWithdrawn(UserWithdrawnException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPassword(InvalidPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(RejoinRestrictionException.class)
    public ResponseEntity<ApiResponse<Void>> handleRejoinRestricted(RejoinRestrictionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateNickname(DuplicateNicknameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(EmailVerificationLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailVerificationLimit(EmailVerificationLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllException(HttpServletRequest request, Exception ex) throws Exception {
        String uri = request.getRequestURI();

        // Swagger 관련 요청은 글로벌 예외 핸들러에서 제외 (스프링 기본 처리로 넘김)
        if (uri.contains("/v3/api-docs") || uri.contains("/swagger-ui")) {
            throw ex;  // 이게 핵심! 그냥 다시 던져야 해
        }

        ex.printStackTrace(); // 운영환경에서는 로거로 변경
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }


}
