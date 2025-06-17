package com.bookbookclub.bbc_user_service.user.controller;

import com.bookbookclub.bbc_user_service.global.jwt.JwtBlacklistService;
import com.bookbookclub.bbc_user_service.global.jwt.JwtRefreshTokenService;
import com.bookbookclub.bbc_user_service.global.jwt.JwtUtil;
import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.dto.*;
import com.bookbookclub.bbc_user_service.user.exception.UserException;
import com.bookbookclub.bbc_user_service.user.exception.UserErrorCode;
import com.bookbookclub.bbc_user_service.user.service.AuthService;
import com.bookbookclub.common.response.ApiResponse;
import com.bookbookclub.common.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static com.bookbookclub.bbc_user_service.user.policy.UserPolicy.REFRESH_EXPIRATION_DAYS;

/**
 * 인증 관련 API를 처리하는 컨트롤러
 * - 회원가입, 로그인, 로그아웃, 토큰 재발급
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtRefreshTokenService refreshTokenService;
    private final JwtBlacklistService blacklistService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(@RequestParam @Email String email) {
        return ResponseEntity.ok(ApiResponse.success(authService.isEmailDuplicate(email)));
    }

    /**
     * 닉네임 중복 확인
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNicknameDuplicate(@RequestParam String nickname) {
        return ResponseEntity.ok(ApiResponse.success(authService.isNicknameDuplicate(nickname)));
    }

    /**
     * 로그인 - AccessToken, RefreshToken 발급
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        UserResponse user = authService.login(request);

        String accessToken = jwtUtil.createToken(user.id());
        String refreshToken = jwtUtil.createRefreshToken();
        refreshTokenService.save(user.id(), refreshToken, REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

        return ResponseEntity.ok(ApiResponse.success(new LoginResponse(accessToken, refreshToken, user)));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        if (!refreshTokenService.isValid(request.userId(), request.refreshToken())) {
            log.warn("Refresh token invalid: userId={}, token={}", request.userId(), request.refreshToken());
            throw new UserException(UserErrorCode.INVALID_TOKEN);
        }

        User user = authService.findById(request.userId());

        String newAccessToken = jwtUtil.createToken(user.getId());
        String newRefreshToken = jwtUtil.createRefreshToken();
        refreshTokenService.save(user.getId(), newRefreshToken, REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

        return ResponseEntity.ok(ApiResponse.success(new LoginResponse(newAccessToken, newRefreshToken, UserResponse.from(user))));
    }

    /**
     * 로그아웃 - RefreshToken 제거 및 AccessToken 블랙리스트 등록
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    HttpServletRequest request) {
        refreshTokenService.delete(userDetails.getUserId());

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);
            long remainingTime = jwtUtil.getRemainingExpiration(accessToken);
            blacklistService.blacklist(accessToken, remainingTime);
        }

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.withdrawUser(userDetails.getUserId());
        return ApiResponse.success("회원 탈퇴가 완료되었습니다.");
    }

}
