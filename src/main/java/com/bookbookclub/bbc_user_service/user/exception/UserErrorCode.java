package com.bookbookclub.bbc_user_service.user.exception;

import com.bookbookclub.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 사용자 도메인 전용 에러 코드 정의
 */
@Getter
public enum UserErrorCode implements BaseErrorCode {

    // 사용자 기본 정보 관련 (U001 ~ U099)
    USER_NOT_FOUND(404, "U001", "회원을 찾을 수 없습니다."),
    USER_WITHDRAWN(403, "U002", "탈퇴한 회원입니다."),
    DUPLICATE_EMAIL(409, "U003", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(409, "U004", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(401, "U005", "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(401, "U006", "유효하지 않은 토큰입니다."),
    REJOIN_RESTRICTED(403, "U007", "재가입이 제한된 계정입니다."),
    INVALID_REFRESH_TOKEN(401, "U008", "Refresh Token이 유효하지 않습니다."),

    // 프로필 이미지 관련 (U100 ~ U199)
    INVALID_PROFILE_IMAGE_TYPE(400, "U100", "이미지 파일만 업로드 가능합니다."),
    PROFILE_IMAGE_TOO_LARGE(400, "U101", "파일 용량은 5MB 이하만 가능합니다."),
    PROFILE_IMAGE_UPLOAD_FAIL(500, "U102", "프로필 이미지 업로드에 실패했습니다."),
    PROFILE_IMAGE_DELETE_FAIL(500, "U103", "프로필 이미지 삭제에 실패했습니다."),
    INVALID_PROFILE_IMAGE_URL(400, "U104","유효하지 않은 프로필 이미지 URL입니다."),

    // 이메일 인증 관련 (U200 ~ U299)
    EMAIL_VERIFICATION_INVALID_TOKEN(400, "U200", "유효하지 않은 이메일 인증 토큰입니다."),
    EMAIL_VERIFICATION_LIMIT_EXCEEDED(429, "U201", "이메일 인증 시도 횟수가 초과되었습니다."),

    // 유효성 검증 관련 (U300 ~ U399)
    INVALID_EMAIL_FORMAT(400, "U300", "이메일 형식이 올바르지 않습니다."),
    EMAIL_DOMAIN_NOT_ALLOWED(400, "U301", "허용되지 않은 이메일 도메인입니다."),
    INVALID_NICKNAME_FORMAT(400, "U310", "닉네임은 한글/영문/숫자 2~12자 이내여야 하며, 공백은 사용할 수 없습니다."),
    BANNED_WORD_DETECTED(400, "U311", "금칙어가 포함되어 있습니다."),
    INVALID_PASSWORD_FORMAT(400, "U320", "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다."),
    TOO_SIMPLE_PASSWORD(400, "U321", "비밀번호에 연속되거나 반복되는 문자를 사용할 수 없습니다.");

    private final int statusCode;
    private final String code;
    private final String message;

    UserErrorCode(int statusCode, String code, String message) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}