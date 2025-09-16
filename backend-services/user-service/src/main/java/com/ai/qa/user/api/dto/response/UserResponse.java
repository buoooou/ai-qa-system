// com/ai/qa/user/api/dto/response/UserResponse.java
package com.ai.qa.user.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
