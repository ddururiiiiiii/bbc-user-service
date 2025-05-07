package com.bookbookclub.bbc_user_service.user.dto;

import com.bookbookclub.bbc_user_service.user.domain.User;
import lombok.Getter;

/**
 * 사용자 요약 응답 DTO
 * - 피드에 좋아요를 누른 사용자 목록 조회 시 사용
 */
@Getter
public class UserSummaryResponse {

    private final Long id;
    private final String nickname;
    private final String profileImageUrl;

    private UserSummaryResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
    }

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(user);
    }
}
