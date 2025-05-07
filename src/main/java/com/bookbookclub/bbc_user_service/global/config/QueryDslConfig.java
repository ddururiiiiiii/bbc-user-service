package com.bookbookclub.bbc_user_service.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 설정 클래스
 * - JPAQueryFactory 빈 등록
 */
@Configuration
public class QueryDslConfig {

    private final EntityManager entityManager;

    public QueryDslConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        // QueryDSL의 JPAQueryFactory 빈 등록 → 레포지토리에서 주입받아 사용
        return new JPAQueryFactory(entityManager);
    }
}
