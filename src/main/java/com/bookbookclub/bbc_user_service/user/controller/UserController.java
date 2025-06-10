package com.bookbookclub.bbc_user_service.user.controller;

import com.bookbookclub.bbc_user_service.user.service.ProfileImageService;
import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.service.UserService;
import com.bookbookclub.bbc_user_service.user.dto.*;

import com.bookbookclub.bbc_user_service.global.jwt.JwtBlacklistService;
import com.bookbookclub.bbc_user_service.global.jwt.JwtRefreshTokenService;
import com.bookbookclub.bbc_user_service.global.jwt.JwtUtil;
import com.bookbookclub.bbc_user_service.global.security.CustomUserDetails;
import com.bookbookclub.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

import static com.bookbookclub.bbc_user_service.user.policy.UserPolicy.*;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 * - 회원가입, 로그인, 내 정보 조회/수정, 로그아웃, 탈퇴
 * - 이메일/닉네임 중복 체크
 * - 프로필 이미지 업로드/삭제
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ProfileImageService profileImageService;
    private final JwtRefreshTokenService refreshTokenService;
    private final JwtBlacklistService blacklistService;
    private final JwtUtil jwtUtil;


    /**
     * 회원가입 API
     * @param request 회원가입 요청 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
        UserResponse response = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 이메일 중복 여부 확인 API
     * @param email 확인할 이메일
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(ApiResponse.success(isDuplicate));
    }

    /**
     * 닉네임 중복 여부 확인 API
     * @param nickname 확인할 닉네임
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(ApiResponse.success(isDuplicate));
    }

    /**
     * 로그인 API
     * - 로그인 성공 시 AccessToken, RefreshToken 발급
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid UserLoginRequest request) {
        UserResponse user = userService.login(request);
        // AccessToken 생성 시 사용자 정보 모두 포함
        String accessToken = jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRole()
        );
        String refreshToken = jwtUtil.createRefreshToken();

        // Redis에 RefreshToken 저장
        refreshTokenService.save(user.getId(), refreshToken, REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

        LoginResponse response = new LoginResponse(accessToken, refreshToken, user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 정보 조회 API
     * - JWT 인증 기반
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(userDetails.getUser())));
    }

    /**
     * 내 정보 수정 API
     * @param request 수정할 정보 (ex. 닉네임, 소개 등)
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.updateProfile(userDetails.getUser().getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "회원 정보가 수정되었습니다."));
    }

    /**
     * 토큰 재발급 API
     * - Refresh Token이 유효하면 새로운 Access/Refresh Token 발급
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        if (!refreshTokenService.isValid(request.getUserId(), request.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail("Refresh Token이 유효하지 않습니다"));
        }

        User user = userService.findById(request.getUserId());

        String newAccessToken = jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRole().name()
        );
        String newRefreshToken = jwtUtil.createRefreshToken();

        refreshTokenService.save(user.getId(), newRefreshToken, REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

        return ResponseEntity.ok(ApiResponse.success(
                new LoginResponse(newAccessToken, newRefreshToken, UserResponse.from(user))
        ));
    }
    /**
     * 로그아웃 API
     * - Redis에 저장된 Refresh Token 삭제
     * - Access Token은 블랙리스트에 등록 (재사용 방지)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    HttpServletRequest request) {
        refreshTokenService.delete(userDetails.getId());

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);
            long remainingTime = jwtUtil.getRemainingExpiration(accessToken);

            // Access Token을 블랙리스트에 등록하여 더 이상 사용 불가하게 함
            blacklistService.blacklist(accessToken, remainingTime);
        }

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 회원 탈퇴 API
     * - 사용자 상태를 변경하여 논리 삭제
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.withdraw(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(null, "회원 탈퇴가 완료되었습니다."));
    }

    /**
     * 프로필 이미지 업로드 API
     * - 이미지 파일을 저장 후 URL을 반환
     */
    @PostMapping("/{userId}/profile-image")
    public ApiResponse<String> uploadProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestParam("file") MultipartFile file) {
        Long userId = userDetails.getUser().getId();
        String imageUrl = profileImageService.store(file, userId);

        // 사용자 정보에 프로필 이미지 URL 업데이트
        userService.updateProfileImage(userId, imageUrl);
        return ApiResponse.success(imageUrl);
    }

    /**
     * 프로필 이미지 삭제 API
     * - 이미지 파일 삭제 및 DB 정보 null 처리
     */
    @DeleteMapping("/profile-image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "프로필 이미지가 삭제되었습니다."));
    }

}
