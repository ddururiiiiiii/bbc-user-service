package com.bookbookclub.bbc_user_service.global.common;


import com.bookbookclub.bbc_user_service.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공통 API 응답 포맷
 * - 성공/실패 여부
 * - 응답 데이터
 * - 메시지 및 에러코드 포함 가능
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.getMessage(), errorCode.getCode());
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message, null);
    }
}