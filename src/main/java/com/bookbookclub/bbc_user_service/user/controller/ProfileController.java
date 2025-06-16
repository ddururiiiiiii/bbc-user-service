package com.bookbookclub.bbc_user_service.user.controller;

import com.bookbookclub.bbc_user_service.user.dto.ProfileUpdateRequest;
import com.bookbookclub.bbc_user_service.user.dto.UserResponse;
import com.bookbookclub.bbc_user_service.user.service.ProfileService;
import com.bookbookclub.common.response.ApiResponse;
import com.bookbookclub.common.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

/**
 * 프로필 관련 API 컨트롤러
 * - 프로필 조회
 * - 프로필 수정 (닉네임, 자기소개, 이미지 업로드 및 삭제 포함)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 내 프로필 조회 API
     */
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = profileService.getMyProfile(userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 내 프로필 수정 API
     * - 닉네임, 자기소개, 프로필 이미지 수정 및 삭제
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @Valid @ModelAttribute ProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(profileService.updateProfile(userDetails.getUserId(), request)));
    }
}
