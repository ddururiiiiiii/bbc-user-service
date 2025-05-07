package com.bookbookclub.bbc_user_service.user.scheduler;


import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.enums.UserStatus;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 탈퇴한 회원 데이터를 주기적으로 삭제하는 스케줄러
 * - 매일 새벽 3시에 실행
 * - 탈퇴일로부터 1년이 지난 사용자만 삭제
 */
@Service
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시
    @Transactional
    public void cleanUpWithdrawnUsers() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<User> usersToDelete = userRepository.findAllByStatusAndWithdrawnAtBefore(UserStatus.WITHDRAWN, oneYearAgo);
        // 영구 삭제 처리
        userRepository.deleteAll(usersToDelete);
    }
}
