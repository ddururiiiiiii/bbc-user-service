package com.bookbookclub.bbc_user_service.emailverification.controller;


import com.bookbookclub.bbc_user_service.emailverification.service.EmailVerificationService;
import com.bookbookclub.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이메일 인증 관련 API 컨트롤러
 * - 인증 요청 메일 전송
 * - 인증 토큰 검증
 */
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * 이메일 인증 메일 전송 API
     */
    @PostMapping("/verify-request")
    public ResponseEntity<ApiResponse<Void>> requestEmailVerification(@RequestParam String email) {
        emailVerificationService.sendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success(null, "이메일 인증 메일이 발송되었습니다."));
    }

    /**
     * 이메일 인증 확인 API
     * - 인증 토큰 유효성 검증
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        boolean result = emailVerificationService.verifyEmail(token);
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail("유효하지 않거나 만료된 인증 링크입니다."));
        }
    }
}
