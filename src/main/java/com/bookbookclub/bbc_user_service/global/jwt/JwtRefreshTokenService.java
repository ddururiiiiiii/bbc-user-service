package com.bookbookclub.bbc_user_service.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * RefreshToken 저장 및 검증 서비스
 * - Redis를 이용해 사용자별 토큰 관리
 */
@Service
@RequiredArgsConstructor
public class JwtRefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final String PREFIX = "refresh:user:";

    /**
     * RefreshToken 저장
     */
    public void save(Long userId, String token, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set(PREFIX + userId, token, duration, unit);
    }

    /**
     * 저장된 RefreshToken 조회
     */
    public String get(Long userId) {
        return redisTemplate.opsForValue().get(PREFIX + userId);
    }

    /**
     * RefreshToken 삭제
     */
    public void delete(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    /**
     * RefreshToken 유효성 검증
     * - 저장된 토큰과 일치 & 실제 토큰도 유효한지 검사
     */
    public boolean isValid(Long userId, String token) {

        String saved = get(userId);
        return saved != null
                && saved.equals(token)
                && jwtUtil.validateToken(token);
    }
}

