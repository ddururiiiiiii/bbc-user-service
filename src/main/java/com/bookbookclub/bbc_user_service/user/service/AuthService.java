package com.bookbookclub.bbc_user_service.user.service;

import com.bookbookclub.bbc_user_service.emailverification.exception.EmailVerificationException;
import com.bookbookclub.bbc_user_service.emailverification.service.EmailVerificationService;
import com.bookbookclub.bbc_user_service.user.config.UserProperties;
import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.dto.LoginRequest;
import com.bookbookclub.bbc_user_service.user.dto.SignupRequest;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.enums.UserStatus;
import com.bookbookclub.bbc_user_service.user.exception.*;
import com.bookbookclub.bbc_user_service.user.policy.UserPolicy;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static com.bookbookclub.common.policy.BannedWordPolicy.containsBannedWord;

/**
 * 인증 관련 서비스 클래스
 * - 회원가입
 * - 로그인
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final UserProperties userProperties;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,20}$";
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]{2,12}$";

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";


    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {

        validateEmailVerification(request.getEmail());
        validateDuplicateEmail(request.getEmail());
        validateRejoinAvailable(request.getEmail());
        validateEmailFormat(request.getEmail());;

        validateNicknameFormat(request.getNickname());
        validateDuplicateNickname(request.getNickname());

        validatePasswordFormat(request.getPassword());

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                userProperties.getDefaultProfileImageUrl()
        );
        userRepository.save(user);

        return UserResponse.from(user);
    }

    /**
     * 로그인
     * - 비밀번호 확인
     */
    public UserResponse login(LoginRequest request) {
        User user = validateUserLogin(request.getEmail(), request.getPassword());
        return UserResponse.from(user);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        user.withdraw(); // → status, withdrawnAt 변경
        redisTemplate.delete("refreshToken:" + userId);
    }

    // 이메일 중복 확인 (API 용도)
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    //닉네임 중복 확인 (API 용도)
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 유저 ID로 유저 조회
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));
    }

    // 이메일 인증 여부 검증
    private void validateEmailVerification(String email) {
        if (!emailVerificationService.isEmailVerified(email)) {
            throw new EmailVerificationException(UserErrorCode.EMAIL_VERIFICATION_INVALID_TOKEN);
        }
    }

    //이메일 중복 확인
    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AuthException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    // 탈퇴 후 재가입 제한 검증
    private void validateRejoinAvailable(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.getStatus() == UserStatus.WITHDRAWN) {
                LocalDateTime rejoinAvailableDate = user.getWithdrawnAt().plusMonths(UserPolicy.REJOIN_RESTRICTION_MONTHS);
                if (LocalDateTime.now().isBefore(rejoinAvailableDate)) {
                    throw new AuthException(UserErrorCode.REJOIN_RESTRICTED);
                }
            } else {
                throw new AuthException(UserErrorCode.DUPLICATE_EMAIL);
            }
        });
    }

    // 이메일 형식 검증
    private void validateEmailFormat(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new AuthException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    // 닉네임 형식 검증 (한글, 영문, 숫자 / 2~12자 / 공백 금지 / 금칙어 )
    private void validateNicknameFormat(String nickname) {
        if (!StringUtils.isNotBlank(nickname)) {
            throw new AuthException(UserErrorCode.INVALID_NICKNAME_FORMAT);
        }

        if (nickname.contains(" ")) {
            throw new AuthException(UserErrorCode.INVALID_NICKNAME_FORMAT);
        }

        if (!nickname.matches(NICKNAME_REGEX)) {
            throw new AuthException(UserErrorCode.INVALID_NICKNAME_FORMAT);
        }

        if (containsBannedWord(nickname)) {
            throw new AuthException(UserErrorCode.BANNED_WORD_DETECTED);
        }
    }

    //닉네임 중복 확인
    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new AuthException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    // 비밀번호 형식 검증 (8~20자 / 영문 + 숫자 + 특수문자 / 연속 또는 반복 문자 금지)
    private void validatePasswordFormat(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new AuthException(UserErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 연속 문자 (ex: abc, 123) 혹은 반복 문자 (ex: aaaa)
        if (hasSequentialChars(password) || hasRepeatedChars(password)) {
            throw new AuthException(UserErrorCode.TOO_SIMPLE_PASSWORD);
        }
    }

    // 연속 문자 검출
    private boolean hasSequentialChars(String input) {
        for (int i = 0; i < input.length() - 2; i++) {
            char c1 = input.charAt(i);
            char c2 = input.charAt(i + 1);
            char c3 = input.charAt(i + 2);
            if ((c2 - c1 == 1) && (c3 - c2 == 1)) {
                return true;
            }
        }
        return false;
    }

    // 반복 문자 검출
    private boolean hasRepeatedChars(String input) {
        for (int i = 0; i < input.length() - 2; i++) {
            char c1 = input.charAt(i);
            if (input.charAt(i + 1) == c1 && input.charAt(i + 2) == c1) {
                return true;
            }
        }
        return false;
    }

    //비밀번호 확인
    private User validateUserLogin(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new AuthException(UserErrorCode.USER_WITHDRAWN);
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthException(UserErrorCode.INVALID_PASSWORD);
        }
        return user;
    }

}
