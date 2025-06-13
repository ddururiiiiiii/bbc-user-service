package com.bookbookclub.bbc_user_service.user.enums;

import lombok.Getter;
/**
 * 사용자 상태 열거형
 */
@Getter
public enum UserStatus {
    ACTIVE("활동 중"),
    WITHDRAWN("탈퇴");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
}
