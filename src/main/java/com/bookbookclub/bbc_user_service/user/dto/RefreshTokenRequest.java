package com.bookbookclub.bbc_user_service.user.dto;

import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    private Long userId;
    private String refreshToken;
}

