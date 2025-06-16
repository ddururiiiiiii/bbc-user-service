package com.bookbookclub.bbc_user_service.follow.repository;

import com.bookbookclub.bbc_user_service.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Follow 엔티티를 관리하는 JPA 리포지토리
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /** 팔로우 관계 존재 여부 확인 */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /** 팔로우 관계 단건 조회 */
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /** 특정 유저를 팔로우하는 사람들의 ID 목록 조회 (팔로워 목록) */
    @Query("SELECT f.followerId FROM Follow f WHERE f.followingId = :userId")
    List<Long> findFollowerIdsByUserId(Long userId);

    /** 특정 유저가 팔로우하고 있는 대상들의 ID 목록 조회 (팔로잉 목록) */
    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :userId")
    List<Long> findFollowingIdsByUserId(Long userId);
}
