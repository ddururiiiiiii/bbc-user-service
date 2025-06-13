package com.bookbookclub.bbc_user_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 유저 프로필 수정 요청 DTO
 * - 닉네임
 * - 자기소개
 * - 프로필 이미지
 * - 프로필 이미지 삭제 요청 여부
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String nickname;
    private String bio;
    private MultipartFile profileImage;
    private boolean imageDeleted; // 삭제 여부만 boolean으로 판단

    public boolean isImageDeleted() {
        return imageDeleted;
    }
    public boolean hasNewImage() {
        return profileImage != null && !profileImage.isEmpty();
    }

}