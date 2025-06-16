package com.bookbookclub.bbc_user_service.user.dto;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * 리프레시 토큰 요청 DTO
 */
public record RefreshTokenRequest(

        @NotNull(message = "userId는 필수입니다.")
        Long userId,

        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken

) {}
