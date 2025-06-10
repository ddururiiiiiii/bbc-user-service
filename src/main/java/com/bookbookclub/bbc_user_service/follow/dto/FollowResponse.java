package com.bookbookclub.bbc_user_service.follow.dto;


import com.bookbookclub.common.dto.UserSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponse {
    private Long followId;
    private UserSummaryResponse user;
}
