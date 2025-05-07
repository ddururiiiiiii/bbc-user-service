package com.bookbookclub.bbc_user_service.user.repository;

import com.bookbookclub.bbc_user_service.user.domain.User;

import com.bookbookclub.bbc_user_service.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //이메일로 사용자 찾기
    Optional<User> findByEmail(String email);

    //이메일 중복 확인
    boolean existsByEmail(String email);

    //닉네임 중복 확인 (API 용도)
    boolean existsByNickname(String nickname);

    /**
     * 탈퇴 상태이며, 특정 날짜 이전에 탈퇴한 사용자 조회
     * - 회원 데이터 정리 스케줄러에서 사용
     */
    List<User> findAllByStatusAndWithdrawnAtBefore(UserStatus status, LocalDateTime dateTime);
}
