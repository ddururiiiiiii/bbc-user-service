package com.bookbookclub.bbc_user_service.follow.dto;

import com.bookbookclub.bbc_user_service.user.dto.UserResponse;

/**
 * 팔로워/팔로잉 유저 목록 조회 시 응답 DTO
 */
public record FollowResponse(

        /** 사용자 ID */
        Long userId,

        /** 사용자 닉네임 */
        String nickname,

        /** 프로필 이미지 URL */
        String profileImageUrl,

        /** 맞팔 여부 (상호 팔로우 여부) */
        boolean isMutual

) {
    public static FollowResponse from(UserResponse user, boolean isMutual) {
        return new FollowResponse(
                user.id(),
                user.nickname(),
                user.profileImageUrl(),
                isMutual
        );
    }
}
