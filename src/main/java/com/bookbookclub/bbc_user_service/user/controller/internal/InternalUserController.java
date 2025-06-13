//package com.bookbookclub.bbc_user_service.user.controller.internal;
//
//import com.bookbookclub.bbc_user_service.user.service.UserService;
//import com.bookbookclub.common.dto.UserSummaryResponse;
//import com.bookbookclub.common.response.ApiResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 내부 시스템 통신용 사용자 API (FeignClient 대응용)
// */
//@Slf4j
//@RestController
//@RequestMapping("/api/internal/users")
//@RequiredArgsConstructor
//public class InternalUserController {
//
//    private final UserService userService;
//
//    @GetMapping("/{userId}")
//    public UserSummaryResponse getUserById(@PathVariable Long userId) {
//        return userService.getUserById(userId);
//    }
//
//    /**
//     * 여러 사용자 정보를 userId 리스트로 조회 (FeignClient용 내부 API)
//     */
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getUsersByIds(
//            @RequestParam List<Long> ids) {
//        List<UserSummaryResponse> users = userService.getUsersByIds(ids);
//        return ResponseEntity.ok(ApiResponse.success(users));
//    }
//}
