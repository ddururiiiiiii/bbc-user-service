package com.bookbookclub.bbc_user_service.api.internal;

import com.bookbookclub.bbc_user_service.user.service.ProfileService;
import com.bookbookclub.common.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/users")
public class InternalUserController {

    private final ProfileService profileService;

    @GetMapping("/{userId}")
    public UserSummaryResponse getUserInfo(@PathVariable Long userId) {
        return profileService.getUserProfile(userId);
    }

    @GetMapping
    public List<UserSummaryResponse> getUsersByIds(@RequestParam("ids") List<Long> userIds) {
        return userIds.stream()
                .map(profileService::getUserProfile)
                .collect(Collectors.toList());
    }

}
