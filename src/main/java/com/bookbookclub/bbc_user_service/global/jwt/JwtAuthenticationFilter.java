package com.bookbookclub.bbc_user_service.global.jwt;


import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import com.bookbookclub.common.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 기반 인증 필터
 * - 매 요청마다 JWT 유효성 검사 및 사용자 인증 처리
 * - 블랙리스트 체크 포함
 * - 인증 실패 시 401 Unauthorized 응답 반환
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final JwtBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Authorization 헤더 없음 or 잘못된 형식 → 그냥 다음 필터로 넘김
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 블랙리스트 확인
        if (blacklistService.isBlacklisted(token)) {
            reject(response, "Token is blacklisted");
            return;
        }

        // 토큰 유효성 검사
        if (!jwtUtil.validateToken(token)) {
            reject(response, "Invalid or expired token");
            return;
        }

        // 사용자 조회
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            reject(response, "User not found");
            return;
        }

        // 인증 객체 생성 및 등록
        CustomUserDetails customUserDetails = new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRole().name()
        );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 실패 시 401 Unauthorized 응답 반환
     */
    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"success\":false,\"code\":\"UNAUTHORIZED\",\"message\":\"" + message + "\"}");
    }
}
