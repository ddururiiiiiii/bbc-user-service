package com.bookbookclub.bbc_user_service.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 비즈니스 로직에서 사용하는 에러 코드 정의 enum
 * - 예외 코드 문자열 + 사용자 메시지 구성
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다."),
    USER_WITHDRAWN("USER_WITHDRAWN", "탈퇴한 사용자입니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    REJOIN_RESTRICTED("REJOIN_RESTRICTED", "탈퇴 후 6개월 이내에는 재가입할 수 없습니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 가입된 이메일입니다."),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다."),
    EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS("TOO_MANY_ATTEMPTS", "이메일 인증 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_EMAIL_VERIFICATION_TOKEN("INVALID_EMAIL_VERIFICATION_TOKEN", "인증 토큰이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "알 수 없는 서버 오류가 발생했습니다."),
    FEED_NOT_FOUND("FEED_NOT_FOUND", "피드를 찾을 수 없습니다."),
    ALREADY_LIKED("ALREADY_LIKED", "이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_FOUND("LIKE_NOT_FOUND", "좋아요 기록이 존재하지 않습니다."),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED("COMMENT_ACCESS_DENIED", "본인이 작성한 댓글만 삭제할 수 있습니다."),
    FEED_BLINDED("FEED_BLINDED", "블라인드 처리된 게시글입니다."),
    ALREADY_REPORTED("ALREADY_REPORTED", "이미 신고한 피드입니다."),
    DUPLICATE_ISBN("DUPLICATE_ISBN", "이미 등록된 ISBN입니다."),
    ALREADY_FOLLOWING("ALREADY_FOLLOWING", "이미 팔로우한 사용자입니다."),
    FOLLOW_NOT_FOUND("FOLLOW_NOT_FOUND", "팔로우 관계를 찾을 수 없습니다."),
    ACCESS_DENIED("ACCESS_DENIED", "본인 계정으로만 요청할 수 있습니다.");

    private final String code;
    private final String message;
}
