package com.bookbookclub.bbc_user_service.follow.controller;

import com.bookbookclub.bbc_user_service.follow.dto.FollowActionResponse;
import com.bookbookclub.bbc_user_service.follow.service.FollowCommandService;
import com.bookbookclub.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.bookbookclub.common.security.CustomUserDetails;


/**
 * 팔로우/언팔로우 요청을 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowCommandController {

    private final FollowCommandService followCommandService;

    /** 팔로우 요청 */
    @PostMapping("/{targetUserId}")
    public ApiResponse<FollowActionResponse> follow(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long targetUserId
    ) {
        return ApiResponse.success(followCommandService.follow(userId, targetUserId));
    }

    /** 언팔로우 요청 */
    @DeleteMapping("/{targetUserId}")
    public ApiResponse<FollowActionResponse> unfollow(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long targetUserId
    ) {
        return ApiResponse.success(followCommandService.unfollow(userId, targetUserId));
    }
}
