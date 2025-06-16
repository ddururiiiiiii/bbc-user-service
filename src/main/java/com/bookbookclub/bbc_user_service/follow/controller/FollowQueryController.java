package com.bookbookclub.bbc_user_service.follow.controller;

import com.bookbookclub.bbc_user_service.follow.dto.FollowResponse;
import com.bookbookclub.bbc_user_service.follow.service.FollowQueryService;
import com.bookbookclub.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팔로워/팔로잉 목록 조회 및 팔로우 여부 확인용 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowQueryController {

    private final FollowQueryService followQueryService;

    /** 팔로워 목록 조회 */
    @GetMapping("/{userId}/followers")
    public ApiResponse<List<FollowResponse>> getFollowers(@PathVariable Long userId) {
        return ApiResponse.success(followQueryService.getFollowers(userId));
    }

    /** 팔로잉 목록 조회 */
    @GetMapping("/{userId}/followings")
    public ApiResponse<List<FollowResponse>> getFollowings(@PathVariable Long userId) {
        return ApiResponse.success(followQueryService.getFollowings(userId));
    }

    /** 팔로우 여부 확인 */
    @GetMapping("/is-following")
    public ApiResponse<Boolean> isFollowing(
            @RequestParam Long fromUserId,
            @RequestParam Long toUserId
    ) {
        return ApiResponse.success(followQueryService.isFollowing(fromUserId, toUserId));
    }
}
