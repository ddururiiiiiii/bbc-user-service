package com.bookbookclub.bbc_user_service.user.service;


import com.bookbookclub.bbc_user_service.user.dto.UserLoginRequest;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.dto.UserSignupRequest;
import com.bookbookclub.bbc_user_service.user.dto.UserUpdateRequest;
import com.bookbookclub.bbc_user_service.user.enums.UserStatus;
import com.bookbookclub.bbc_user_service.user.exception.*;
import com.bookbookclub.bbc_user_service.user.policy.UserPolicy;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import com.bookbookclub.bbc_user_service.user.domain.User;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 * - 회원가입, 로그인, 회원정보 수정, 탈퇴 등
 */
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Value("${custom.user.default-profile-image}")
    private String defaultProfileImageUrl;

    /**
     * 회원가입
     * - 이메일 인증 확인
     * - 재가입 제한 확인 (탈퇴 후 6개월 제한)
     * - 닉네임 중복 확인
     * - 기본 프로필 이미지 설정
     */
    public UserResponse signup(UserSignupRequest request){

        validateEmailVerification(request.getEmail());
        validateRejoinAvailable(request.getEmail());
        validateDuplicateNickname(request.getNickname());

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );
        user.setProfileImageUrl(defaultProfileImageUrl);
        userRepository.save(user);

        return UserResponse.from(user);
    }

    // 이메일 인증 여부 검증
    private void validateEmailVerification(String email) {
        if (!emailVerificationService.isEmailVerified(email)) {
            throw new EmailNotVerifiedException();
        }
    }

    //이메일 중복 확인
    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }

    //닉네임 중복 확인
    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }
    }

    // 탈퇴 후 재가입 제한 검증
    private void validateRejoinAvailable(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.getStatus() == UserStatus.WITHDRAWN) {
                LocalDateTime rejoinAvailableDate = user.getWithdrawnAt().plusMonths(UserPolicy.REJOIN_RESTRICTION_MONTHS);
                if (LocalDateTime.now().isBefore(rejoinAvailableDate)) {
                    throw new RejoinRestrictionException();
                }
            } else {
                throw new DuplicateEmailException(); // ACTIVE 계정일 경우
            }
        });
    }

    // 이메일 중복 확인 (API 용도)
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    //닉네임 중복 확인 (API 용도)
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    //로그인
    public UserResponse login(UserLoginRequest request){
        User user = validateUserLogin(request.getEmail(), request.getPassword());
        return UserResponse.from(user);
    }

    //비밀번호 확인
    private User validateUserLogin(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new UserWithdrawnException();
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        return user;
    }

    /**
     * 사용자 프로필 수정
     * - 닉네임 변경 시 중복 여부 확인
     * - JPA 변경 감지에 의해 자동 업데이트
     */
    @Transactional
    public void updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 닉네임이 바뀌었을 때만 중복 검사를 수행
        if (!user.getNickname().equals(request.getNickname())) {
            validateDuplicateNickname(request.getNickname());
        }

        user.setNickname(request.getNickname());
        user.setBio(request.getBio());
        // save 호출 안 해도 됨 → JPA가 변경감지로 UPDATE 수행
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.withdraw();
    }

    /**
     * 프로필 이미지 경로 수정
     */
    @Transactional
    public void updateProfileImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        user.setProfileImageUrl(imageUrl);
    }

    /**
     * 프로필 이미지 삭제
     * - 기본 이미지로 되돌림
     * - (추후 실제 파일 삭제 로직도 적용 가능)
     */
    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 기본 이미지가 아니라면 삭제 로직 수행 가능 (지금은 생략)
        if (!user.getProfileImageUrl().equals(defaultProfileImageUrl)) {
            // 나중에 실제 파일 삭제 로직 넣을 수 있음
            // profileImageService.delete(user.getProfileImageUrl());
        }

        // 기본 이미지로 되돌림
        user.setProfileImageUrl(defaultProfileImageUrl);
    }
}
