package com.bookbookclub.bbc_user_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {}
