package com.bookbookclub.bbc_user_service.follow.service;

import com.bookbookclub.bbc_user_service.follow.dto.FollowResponse;
import com.bookbookclub.bbc_user_service.follow.repository.FollowRepository;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.service.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 팔로우 상태 조회, 목록 조회를 담당하는 서비스 (Query)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowQueryService {

    private final FollowRepository followRepository;
    private final UserReader userReader;

    /**
     * 팔로워 목록 조회
     */
    public List<FollowResponse> getFollowers(Long userId) {
        List<Long> followerIds = followRepository.findFollowerIdsByUserId(userId);
        return toFollowResponseList(userId, followerIds);
    }

    /**
     * 팔로잉 목록 조회
     */
    public List<FollowResponse> getFollowings(Long userId) {
        List<Long> followingIds = followRepository.findFollowingIdsByUserId(userId);
        return toFollowResponseList(userId, followingIds);
    }

    /**
     * 팔로우 여부 확인
     */
    public boolean isFollowing(Long fromUserId, Long toUserId) {
        return followRepository.existsByFollowerIdAndFollowingId(fromUserId, toUserId);
    }

    /**
     * 유저 ID 목록을 FollowResponse로 변환 (맞팔 여부 포함)
     */
    private List<FollowResponse> toFollowResponseList(Long me, List<Long> targetIds) {
        return targetIds.stream()
                .map(targetId -> {
                    UserResponse user = userReader.getUserResponse(targetId);
                    boolean isMutual = followRepository.existsByFollowerIdAndFollowingId(targetId, me);
                    return FollowResponse.from(user, isMutual);
                })
                .toList();
    }
}
