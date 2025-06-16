package com.bookbookclub.bbc_user_service.follow.service;

import com.bookbookclub.bbc_user_service.follow.dto.FollowActionResponse;
import com.bookbookclub.bbc_user_service.follow.entity.Follow;
import com.bookbookclub.bbc_user_service.follow.exception.FollowException;
import com.bookbookclub.bbc_user_service.follow.repository.FollowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowCommandServiceTest {

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowCommandService followCommandService;

    @Test
    void 팔로우_정상_요청() {
        Long from = 1L, to = 2L;
        when(followRepository.existsByFollowerIdAndFollowingId(from, to)).thenReturn(false);

        FollowActionResponse result = followCommandService.follow(from, to);

        verify(followRepository).save(any(Follow.class));
        assertThat(result.isFollowing()).isTrue();
        assertThat(result.targetUserId()).isEqualTo(to);
    }

    @Test
    void 자기_자신에게_팔로우_요청시_예외() {
        assertThatThrownBy(() -> followCommandService.follow(1L, 1L))
                .isInstanceOf(FollowException.class);
    }

    @Test
    void 이미_팔로우한_경우_예외() {
        when(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> followCommandService.follow(1L, 2L))
                .isInstanceOf(FollowException.class);
    }

    @Test
    void 언팔로우_정상_요청() {
        Follow follow = Follow.of(1L, 2L);
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.of(follow));

        FollowActionResponse result = followCommandService.unfollow(1L, 2L);

        verify(followRepository).delete(follow);
        assertThat(result.isFollowing()).isFalse();
    }

    @Test
    void 존재하지_않는_팔로우_언팔로우시_예외() {
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followCommandService.unfollow(1L, 2L))
                .isInstanceOf(FollowException.class);
    }
}
