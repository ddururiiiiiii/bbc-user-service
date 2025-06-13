package com.bookbookclub.bbc_user_service.global.exception;

import com.bookbookclub.common.exception.BusinessException;
import com.bookbookclub.common.exception.GlobalErrorCode;
import com.bookbookclub.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


/**
 * 시스템 전역 공통 예외 처리 핸들러
 *
 * - 모든 도메인에서 발생하는 예외를 단일한 형식으로 응답한다.
 * - BusinessException을 기반으로 각 도메인별 에러코드를 통일된 구조로 리턴한다.
 * - Validation 오류나 예기치 못한 에러도 공통 처리한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 도메인 비즈니스 로직 중 발생한 커스텀 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.error("[비즈니스 예외 발생]", e);
        return ResponseEntity
                .status(e.getErrorCode().getStatusCode())
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    /**
     * @Valid 유효성 검증 실패 처리 (DTO 필드 등)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("[유효성 검증 실패] {}", e.getMessage());

        return ResponseEntity
                .status(GlobalErrorCode.INVALID_INPUT_VALUE.getStatusCode())
                .body(ApiResponse.error(GlobalErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * @RequestParam 등 단일 파라미터 유효성 검증 실패 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("[단일 파라미터 검증 실패] {}", e.getMessage());

        return ResponseEntity
                .status(GlobalErrorCode.INVALID_INPUT_VALUE.getStatusCode())
                .body(ApiResponse.error(GlobalErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * 예상하지 못한 서버 내부 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("[서버 내부 오류 발생]", e);

        return ResponseEntity
                .status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatusCode())
                .body(ApiResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException e) {
        log.warn("[404 Not Found] {}", e.getMessage());

        return ResponseEntity
                .status(GlobalErrorCode.NOT_FOUND.getStatusCode())
                .body(ApiResponse.error(GlobalErrorCode.NOT_FOUND));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("[405 Method Not Allowed] {}", e.getMessage());

        return ResponseEntity
                .status(GlobalErrorCode.METHOD_NOT_ALLOWED.getStatusCode())
                .body(ApiResponse.error(GlobalErrorCode.METHOD_NOT_ALLOWED));
    }
}