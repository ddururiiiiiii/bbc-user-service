package com.bookbookclub.bbc_user_service.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 설정 클래스
 * - @CreatedDate, @LastModifiedDate 등 활성화
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
