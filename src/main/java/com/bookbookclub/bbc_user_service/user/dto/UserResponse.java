package com.bookbookclub.bbc_user_service.user.dto;

import com.bookbookclub.bbc_user_service.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보를 담는 응답 DTO
 * - Entity(User) → DTO로 변환
 */
@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String role;
    private String bio;
    private String profileImageUrl;

    public static UserResponse from(User user) {
        // User 엔티티 → 응답 DTO로 변환
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
