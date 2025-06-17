package com.bookbookclub.bbc_user_service.user.service;

import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.dto.ProfileUpdateRequest;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.exception.UserException;
import com.bookbookclub.bbc_user_service.user.exception.UserErrorCode;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;;
import com.bookbookclub.common.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.bookbookclub.common.policy.BannedWordPolicy.containsBannedWord;
import static org.springframework.util.StringUtils.hasText;


/**
 * 사용자 프로필 관련 비즈니스 로직을 담당하는 서비스
 *
 * - 닉네임, 자기소개, 프로필 이미지 수정 기능 제공
 * - 사용자 정보 조회 기능 제공
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileImageService profileImageService;


    /**
     * 내 프로필 정보 조회
     */
    public UserResponse getMyProfile(Long userId) {
        return UserResponse.from(getUser(userId));
    }

    /**
     * 전체 프로필 수정 (닉네임, 이미지, 자기소개)
     */
    @Transactional
    public UserResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getUser(userId);

        if (hasText(request.getNickname()) && !user.getNickname().equals(request.getNickname())) {
            updateNickname(user, request.getNickname());
        }

        if (request.getBio() != null && !Objects.equals(user.getBio(), request.getBio())) {
            user.updateBio(request.getBio());
        }

        handleProfileImageUpdate(user, request);

        return UserResponse.from(user);
    }

    /**
     * FeignClient에서 사용할 유저 요약 정보 조회
     */
    public UserSummaryResponse getUserProfile(Long userId) {
        User user = getUser(userId);
        return new UserSummaryResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
    }

    // 닉네임 변경
    private void updateNickname(User user, String nickname) {
        if (containsBannedWord(nickname)) {
            throw new UserException(UserErrorCode.BANNED_WORD_DETECTED);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
        user.updateNickname(nickname);
    }

    // 자기소개 변경
    private void updateBio(User user, String bio) {
        if (containsBannedWord(bio)) {
            throw new UserException(UserErrorCode.BANNED_WORD_DETECTED);
        }
        user.updateBio(bio);
    }

    // 프로필 이미지 변경 처리
    private void handleProfileImageUpdate(User user, ProfileUpdateRequest request) {
        String currentImageUrl = user.getProfileImageUrl();

        // 1. 삭제 요청이면 → 기본 이미지로 변경
        if (request.isImageDeleted()) {
            if (!profileImageService.isDefaultImage(currentImageUrl)) {
                profileImageService.delete(currentImageUrl);
            }
            user.updateProfileImage(profileImageService.getDefaultImageUrl());
            return;
        }

        // 2. 새 이미지 업로드 → 기존 사용자 이미지 삭제 → 새 이미지 저장
        if (request.hasNewImage()) {
            String newImageUrl = profileImageService.store(request.getProfileImage());
            if (!profileImageService.isDefaultImage(currentImageUrl)) {
                profileImageService.delete(currentImageUrl);
            }
            user.updateProfileImage(newImageUrl);
        }
        // 3. 둘 다 아니라면 → 아무 것도 안 함
    }

    // 유저 조회 공통 처리
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}