package com.ai.qa.user.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class UserPrincipal {
    private Long id;
    private String username;
    private String role;
}
