package com.bookbookclub.bbc_user_service.user.service;

import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.exception.UserException;
import com.bookbookclub.bbc_user_service.user.exception.UserErrorCode;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 유저 정보를 조회하고 DTO로 반환하는 읽기 전용 컴포넌트
 * - UserService와 분리하여 읽기 책임만 담당
 */
@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserRepository userRepository;

    /**
     * userId로 유저 조회 후 DTO 변환
     */
    public UserResponse getUserResponse(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    /**
     * userId로 유저 엔티티 직접 조회
     */
    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
