package com.bookbookclub.bbc_user_service.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 정보 수정 요청 DTO
 * - 닉네임, 자기소개 수정
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
    private String nickname;

    @Size(max = 500, message = "자기소개는 500자 이내로 입력해주세요.")
    private String bio;
}
