package com.bookbookclub.bbc_user_service.follow.service;

import com.bookbookclub.bbc_user_service.follow.dto.FollowResponse;
import com.bookbookclub.bbc_user_service.follow.repository.FollowRepository;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.service.UserReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowQueryServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private FollowQueryService followQueryService;

    private UserResponse user10;
    private UserResponse user11;
    private UserResponse user20;

    @BeforeEach
    void setUp() {
        user10 = new UserResponse(10L, "a@a.com", "닉네임1", "USER", "소개", "img1");
        user11 = new UserResponse(11L, "b@b.com", "닉네임2", "USER", "소개", "img2");
        user20 = new UserResponse(20L, "c@c.com", "닉네임3", "USER", "소개", "img3");
    }

    @Test
    void 팔로워_목록_조회() {
        Long userId = 1L;
        List<Long> followers = List.of(10L, 11L);

        when(followRepository.findFollowerIdsByUserId(userId)).thenReturn(followers);
        when(userReader.getUserResponse(10L)).thenReturn(user10);
        when(userReader.getUserResponse(11L)).thenReturn(user11);
        when(followRepository.existsByFollowerIdAndFollowingId(anyLong(), anyLong()))
                .thenAnswer(invocation -> {
                    Long from = invocation.getArgument(0);
                    Long to = invocation.getArgument(1);
                    return from.equals(11L) && to.equals(1L); // 11번만 맞팔 처리
                });

        List<FollowResponse> result = followQueryService.getFollowers(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId()).isEqualTo(10L);
        assertThat(result.get(0).isMutual()).isFalse();
        assertThat(result.get(1).userId()).isEqualTo(11L);
        assertThat(result.get(1).isMutual()).isTrue();
    }

    @Test
    void 팔로잉_목록_조회() {
        Long userId = 1L;
        List<Long> followings = List.of(20L);

        when(followRepository.findFollowingIdsByUserId(userId)).thenReturn(followings);
        when(userReader.getUserResponse(20L)).thenReturn(user20);
        when(followRepository.existsByFollowerIdAndFollowingId(20L, 1L)).thenReturn(true); // 맞팔

        List<FollowResponse> result = followQueryService.getFollowings(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(20L);
        assertThat(result.get(0).isMutual()).isTrue();
    }

    @Test
    void 팔로우_여부_조회() {
        when(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(true);

        boolean result = followQueryService.isFollowing(1L, 2L);

        assertThat(result).isTrue();
    }
}
