package com.bookbookclub.bbc_user_service.user.service;

import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.dto.ProfileUpdateRequest;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileImageService profileImageService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create(
                "test@email.com",
                "password123!",
                "oldNickname",
                "default.png"
        );
        user.updateBio("기존 자기소개");
    }

    @Test
    void 프로필_조회_성공() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = profileService.getMyProfile(1L);

        assertEquals("oldNickname", result.nickname());
    }

    @Test
    void 닉네임_수정_성공() {
        ProfileUpdateRequest request = new ProfileUpdateRequest("newNick", "기존 자기소개", null, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(false);

        UserResponse result = profileService.updateProfile(1L, request);

        assertEquals("newNick", result.nickname());
    }

    @Test
    void 자기소개_수정_성공() {
        ProfileUpdateRequest request = new ProfileUpdateRequest("oldNickname", "새로운 자기소개", null, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = profileService.updateProfile(1L, request);

        assertEquals("새로운 자기소개", user.getBio());
    }

    @Test
    void 기본_이미지로_변경_성공() {
        user.updateProfileImage("custom.png");

        ProfileUpdateRequest request = new ProfileUpdateRequest("oldNickname", "기존 자기소개", null, true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileImageService.isDefaultImage("custom.png")).thenReturn(false);
        when(profileImageService.getDefaultImageUrl()).thenReturn("default.png");

        profileService.updateProfile(1L, request);

        verify(profileImageService).delete("custom.png");
        assertEquals("default.png", user.getProfileImageUrl());
    }

    @Test
    void 새_이미지_업로드_성공() {
        // 기존 이미지가 기본 이미지가 아니라고 가정
        user.updateProfileImage("http://localhost:8080/images/old.png");

        MultipartFile mockImage = new MockMultipartFile("image", "test.png", "image/png", new byte[100]);
        ProfileUpdateRequest request = new ProfileUpdateRequest("oldNickname", "기존 자기소개", mockImage, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileImageService.isDefaultImage(user.getProfileImageUrl())).thenReturn(false);
        when(profileImageService.store(mockImage)).thenReturn("http://localhost:8080/images/new.png");

        profileService.updateProfile(1L, request);

        verify(profileImageService).delete("http://localhost:8080/images/old.png");
        verify(profileImageService).store(mockImage);
        assertEquals("http://localhost:8080/images/new.png", user.getProfileImageUrl());
    }

}

