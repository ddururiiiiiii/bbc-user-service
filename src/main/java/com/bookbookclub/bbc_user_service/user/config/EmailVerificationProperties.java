package com.bookbookclub.bbc_user_service.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 이메일 인증 관련 설정값을 관리하는 프로퍼티 클래스
 *
 *  application.yml 의 custom.email-verification.* 값을 바인딩함
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom.email-verification")
public class EmailVerificationProperties {

    /**
     * 인증 실패 최대 허용 횟수
     * 예: 5
     */
    private int maxFailCount;

    /**
     * 실패 TTL (인증 실패 카운트가 만료되기까지의 시간)
     * 단위: 분 / 예: 10
     */
    private long failExpireMinutes;

    /**
     * 인증 토큰 TTL (Redis에 저장된 인증 토큰의 유효 시간)
     * 단위: 분 / 예: 10
     */
    private long tokenExpireMinutes;
}