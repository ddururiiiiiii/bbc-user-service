package com.bookbookclub.bbc_user_service.follow.service;


import com.bookbookclub.bbc_user_service.follow.dto.FollowActionResponse;
import com.bookbookclub.bbc_user_service.follow.dto.FollowResponse;
import com.bookbookclub.bbc_user_service.follow.entity.Follow;
import com.bookbookclub.bbc_user_service.follow.exception.AlreadyFollowingException;
import com.bookbookclub.bbc_user_service.follow.exception.FollowNotFoundException;
import com.bookbookclub.bbc_user_service.follow.repository.FollowRepository;
import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.exception.UserNotFoundException;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import com.bookbookclub.common.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /**
     * 팔로우
     */
    public FollowActionResponse follow(Long followerId, Long followingId) {
        User follower = findUserById(followerId);
        User following = findUserById(followingId);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new AlreadyFollowingException();
        }

        Follow follow = Follow.create(follower, following);
        followRepository.save(follow);

        long followerCount = followRepository.countByFollowing(following);

        return new FollowActionResponse(follow.getId(), "팔로우 완료", followerCount);
    }

    /**
     * 언팔로우
     */
    public FollowActionResponse unfollow(Long followerId, Long followingId) {
        User follower = findUserById(followerId);
        User following = findUserById(followingId);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(FollowNotFoundException::new);

        followRepository.delete(follow);

        long followerCount = followRepository.countByFollowing(following);

        return new FollowActionResponse(follow.getId(), "언팔로우 완료", followerCount);
    }

    /**
     * 팔로워 목록
     */
    public List<FollowResponse> getFollowers(Long userId) {
        User user = findUserById(userId);
        return followRepository.findAllByFollowing(user).stream()
                .map(f -> new FollowResponse(
                        f.getId(),
                        UserSummaryResponse.of(
                                f.getFollower().getId(),
                                f.getFollower().getNickname(),
                                f.getFollower().getProfileImageUrl()
                        )))
                .toList();
    }

    /**
     * 팔로잉 목록
     */
    public List<FollowResponse> getFollowings(Long userId) {
        User user = findUserById(userId);
        return followRepository.findAllByFollower(user).stream()
                .map(f -> new FollowResponse(
                        f.getId(),
                        UserSummaryResponse.of(
                                f.getFollowing().getId(),
                                f.getFollowing().getNickname(),
                                f.getFollowing().getProfileImageUrl()
                        )))
                .toList();
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }
}
