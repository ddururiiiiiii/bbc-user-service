package com.bookbookclub.bbc_user_service.follow.service;

import com.bookbookclub.bbc_user_service.follow.dto.FollowActionResponse;
import com.bookbookclub.bbc_user_service.follow.entity.Follow;
import com.bookbookclub.bbc_user_service.follow.exception.FollowErrorCode;
import com.bookbookclub.bbc_user_service.follow.exception.FollowException;
import com.bookbookclub.bbc_user_service.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 팔로우/언팔로우 등 상태 변경을 담당하는 서비스 (Command)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FollowCommandService {

    private final FollowRepository followRepository;

    /**
     * 팔로우 요청
     */
    public FollowActionResponse follow(Long followerId, Long followingId) {
        validateFollowable(followerId, followingId);

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new FollowException(FollowErrorCode.ALREADY_FOLLOWING);
        }

        Follow follow = Follow.of(followerId, followingId);
        followRepository.save(follow);

        return new FollowActionResponse(followingId, true);
    }

    /**
     * 언팔로우 요청
     */
    public FollowActionResponse unfollow(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new FollowException(FollowErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
        return new FollowActionResponse(followingId, false);
    }

    /**
     * 자기 자신을 팔로우하는 경우 예외 처리
     */
    private void validateFollowable(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new FollowException(FollowErrorCode.FOLLOW_ACCESS_DENIED);
        }
    }
}
