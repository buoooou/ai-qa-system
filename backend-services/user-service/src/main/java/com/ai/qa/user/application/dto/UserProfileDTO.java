package com.ai.qa.user.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProfileDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String nickname;
    private final String role;
}

