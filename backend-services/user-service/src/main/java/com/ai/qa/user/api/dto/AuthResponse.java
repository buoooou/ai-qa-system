package com.ai.qa.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private long expiresIn;
    private UserProfile profile;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserProfile {
        private Long id;
        private String username;
        private String email;
        private String nickname;
        private String role;
    }
}