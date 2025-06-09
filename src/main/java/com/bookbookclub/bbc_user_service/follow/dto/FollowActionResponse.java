package com.bookbookclub.bbc_user_service.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowActionResponse {
    private Long followId;       // 팔로우 관계 ID
    private String message;      // 응답 메시지 ("팔로우 완료", "언팔로우 완료")
    private Long followerCount;  // 현재 대상의 팔로워 수 (선택)
}
