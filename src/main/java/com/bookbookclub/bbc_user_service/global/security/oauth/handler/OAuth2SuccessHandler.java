package com.bookbookclub.bbc_user_service.global.security.oauth.handler;

import com.bookbookclub.common.dto.TokenResponse;
import com.bookbookclub.bbc_user_service.global.jwt.JwtRefreshTokenService;
import com.bookbookclub.bbc_user_service.global.jwt.JwtUtil;
import com.bookbookclub.bbc_user_service.global.security.CustomUserDetails;
import com.bookbookclub.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static com.bookbookclub.bbc_user_service.user.policy.UserPolicy.*;


/**
 * OAuth2 로그인 성공 시 처리 핸들러
 * - JWT 발급 및 Redis에 RefreshToken 저장
 * - JSON 형식으로 토큰 응답 반환
 */
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final JwtRefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // accessToken, refreshToken 생성 후 JSON 응답
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // JWT 발급
        String accessToken = jwtUtil.createToken(
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getNickname(),
                userDetails.getProfileImageUrl(),
                userDetails.getRole()
        );
        String refreshToken = jwtUtil.createRefreshToken();

        // Redis 저장
        refreshTokenService.save(userDetails.getId(), refreshToken, REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

        // 응답
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.success(new TokenResponse(accessToken, refreshToken))
        ));
    }
}
