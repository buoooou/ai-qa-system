package com.ai.qa.gateway.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileGatewayResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String role;
}
