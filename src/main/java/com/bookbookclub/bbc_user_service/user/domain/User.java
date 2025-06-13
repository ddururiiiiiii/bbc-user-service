package com.bookbookclub.bbc_user_service.user.domain;

import com.bookbookclub.bbc_user_service.user.enums.AuthProvider;
import com.bookbookclub.bbc_user_service.user.enums.Role;
import com.bookbookclub.bbc_user_service.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
/**
 * 사용자(User) 엔티티
 * - 일반 회원 및 소셜 로그인 회원을 모두 포함
 * - 논리 삭제 방식으로 탈퇴 처리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 사용자 이메일 (고유, 필수) */
    @Column(unique = true, nullable = false)
    private String email;

    /** 비밀번호 (암호화 저장) */
    @Column(nullable = false)
    private String password;

    /** 닉네임 (고유, 필수) */
    @Setter
    @Column(unique = true, nullable = false)
    private String nickname;

    /** 프로필 이미지 URL (nullable) */
    @Setter
    @Column(length = 1000)
    private String profileImageUrl;

    /** 사용자 권한 (예: USER, ADMIN) */
    @Enumerated(EnumType.STRING)
    private Role role;

    /** 회원 상태 (예: ACTIVE, WITHDRAWN) */
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /** 자기소개 (선택) */
    @Setter
    @Column(length = 500)
    private String bio;

    /** 로그인 방식 (LOCAL, GOOGLE 등) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    /** 소셜 로그인 식별자 (LOCAL일 경우 "LOCAL") */
    @Column(nullable = false)
    private String providerId;

    /** 생성일 (자동 설정) */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 수정일 (자동 설정) */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /** 탈퇴일 (논리 삭제 처리 시 사용) */
    private LocalDateTime withdrawnAt;

    /**
     * 닉네임 변경
     * - 사용자 프로필에서 닉네임을 수정할 때 사용
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 자기소개 변경
     * - 사용자 프로필 소개(bio) 수정
     */
    public void updateBio(String bio) {
        this.bio = bio;
    }

    /**
     * 프로필 이미지 변경
     * - 저장된 이미지 경로를 사용자 정보에 반영
     */
    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    /**
     * 일반 회원 생성
     */
    public static User create(String email, String encodedPassword, String nickname, String profileImageUrl) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.role = Role.USER;
        user.status = UserStatus.ACTIVE;
        user.provider = AuthProvider.LOCAL;
        user.providerId = "LOCAL";
        user.profileImageUrl = profileImageUrl;
        return user;
    }

    /**
     * 소셜 로그인 회원 생성
     * - 비밀번호는 직접 입력하지 않으므로 의미 없는 문자열로 설정
     * - provider, providerId를 통해 OAuth2 제공자 식별
     * - 기본 권한(USER)과 활성 상태(ACTIVE)로 초기화됨
     */
    public static User createSocialUser(String email, String nickname, AuthProvider provider, String providerId) {
        User user = new User();
        user.email = email;
        user.password = "oauth2";
        user.nickname = nickname;
        user.role = Role.USER;
        user.status = UserStatus.ACTIVE;
        user.provider = provider;
        user.providerId = providerId;
        return user;
    }

    /**
     * 사용자 탈퇴 처리
     * - 상태를 WITHDRAWN으로 변경
     * - withdrawnAt에 현재 시간 기록
     */
    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
    }
}
