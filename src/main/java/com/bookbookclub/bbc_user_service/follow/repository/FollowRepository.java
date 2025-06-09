package com.bookbookclub.bbc_user_service.follow.repository;


import com.bookbookclub.bbc_user_service.follow.entity.Follow;
import com.bookbookclub.bbc_user_service.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findAllByFollower(User follower);

    List<Follow> findAllByFollowing(User following);

    long countByFollower(User follower);

    long countByFollowing(User following);
}
