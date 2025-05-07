package com.bookbookclub.bbc_user_service.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * AccessToken 블랙리스트 관리 서비스
 * - Redis를 이용하여 토큰 무효화 관리
 */
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final String PREFIX = "blacklist:access:";

    /**
     * 토큰을 블랙리스트에 등록
     * @param token accessToken
     * @param expirationMillis 남은 유효시간(ms)
     */
    public void blacklist(String token, long expirationMillis) {
        redisTemplate.opsForValue().set(PREFIX + token, "logout", expirationMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 블랙리스트 등록 여부 확인
     */
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }
}
