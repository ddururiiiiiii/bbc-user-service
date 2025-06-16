package com.bookbookclub.bbc_user_service.user.dto;

import com.bookbookclub.bbc_user_service.user.domain.User;

/**
 * 사용자 정보를 담는 응답 DTO (Entity → DTO 변환용)
 */
public record UserResponse(
        Long id,
        String email,
        String nickname,
        String role,
        String bio,
        String profileImageUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name(),
                user.getBio(),
                user.getProfileImageUrl()
        );
    }
}
