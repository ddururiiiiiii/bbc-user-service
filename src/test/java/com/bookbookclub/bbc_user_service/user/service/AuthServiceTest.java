package com.bookbookclub.bbc_user_service.user.service;

import com.bookbookclub.bbc_user_service.emailverification.service.EmailVerificationService;
import com.bookbookclub.bbc_user_service.user.config.UserProperties;
import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.dto.LoginRequest;
import com.bookbookclub.bbc_user_service.user.dto.SignupRequest;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.exception.UserException;
import com.bookbookclub.bbc_user_service.user.exception.UserErrorCode;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserProperties userProperties;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User activeUser;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("test@email.com", "Password913!", "nickname");
        loginRequest = new LoginRequest("test@email.com", "Password913!");
        activeUser = User.create(
                "test@email.com",
                "encodedPassword",
                "nickname",
                "default.png"
        );
    }

    @Test
    void 회원가입_성공() {
        when(emailVerificationService.isEmailVerified(signupRequest.email())).thenReturn(true);
        when(userRepository.existsByEmail(signupRequest.email())).thenReturn(false);
        when(userRepository.findByEmail(signupRequest.email())).thenReturn(Optional.empty());
        when(userRepository.existsByNickname(signupRequest.nickname())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.password())).thenReturn("encodedPassword");
        when(userProperties.getDefaultProfileImageUrl()).thenReturn("default.png");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authService.signup(signupRequest);

        assertEquals("nickname", response.nickname());
    }

    @Test
    void 로그인_성공() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches(loginRequest.password(), activeUser.getPassword())).thenReturn(true);

        UserResponse response = authService.login(loginRequest);

        assertEquals("nickname", response.nickname());
    }

    @Test
    void 로그인_실패_비밀번호_불일치() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongPassword", activeUser.getPassword())).thenReturn(false);

        assertThrows(UserException.class, () ->
                authService.login(new LoginRequest(loginRequest.email(), "wrongPassword"))
        );
    }

    @Test
    void 회원_탈퇴_성공() {
        // given
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);

        // when
        authService.withdrawUser(userId);

        // then

        verify(userRepository).findById(userId);
        verify(mockUser).withdraw();
        verify(redisTemplate).delete("refreshToken:" + userId);
    }

    @Test
    void 회원_탈퇴_실패_유저없음() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.withdrawUser(userId))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());

        verify(userRepository).findById(userId);
        verifyNoInteractions(redisTemplate);
    }
}
