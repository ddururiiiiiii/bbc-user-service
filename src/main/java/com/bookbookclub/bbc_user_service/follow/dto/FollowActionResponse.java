package com.bookbookclub.bbc_user_service.follow.dto;

/**
 * 팔로우/언팔로우 요청 후 응답 DTO
 */
public record FollowActionResponse(

        /** 팔로우 대상 유저 ID */
        Long targetUserId,

        /** 현재 로그인 유저가 해당 유저를 팔로우 중인지 여부 */
        boolean isFollowing

) {
    public static FollowActionResponse from(Long targetUserId, boolean isFollowing) {
        return new FollowActionResponse(targetUserId, isFollowing);
    }
}
