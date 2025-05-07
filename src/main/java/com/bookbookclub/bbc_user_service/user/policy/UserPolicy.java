package com.bookbookclub.bbc_user_service.user.policy;

/**
 * 사용자 정책 상수 정의 클래스
 * - 정책 변경이 필요한 경우 이곳에서 일괄 관리
 */
public class UserPolicy {

    /** Refresh Token 유효기간 (단위: 일) */
    public static final long REFRESH_EXPIRATION_DAYS = 7;

    /** 탈퇴 후 재가입 제한 기간 (단위: 개월) */
    public static final int REJOIN_RESTRICTION_MONTHS = 6;
}
