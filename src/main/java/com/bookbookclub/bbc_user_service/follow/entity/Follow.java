package com.bookbookclub.bbc_user_service.follow.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 간 팔로우 관계를 나타내는 엔티티
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    /** 팔로우 고유 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 팔로우를 거는 사용자 ID (팔로워) */
    @Column(nullable = false)
    private Long followerId;

    /** 팔로우 대상 사용자 ID (팔로잉) */
    @Column(nullable = false)
    private Long followingId;

    /**
     * Follow 엔티티 생성 정적 팩토리 메서드
     */
    public static Follow of(Long followerId, Long followingId) {
        Follow follow = new Follow();
        follow.followerId = followerId;
        follow.followingId = followingId;
        return follow;
    }
}

