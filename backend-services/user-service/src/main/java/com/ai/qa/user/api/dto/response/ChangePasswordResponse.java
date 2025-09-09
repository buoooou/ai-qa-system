package com.ai.qa.user.api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 修改密码响应
 */
@Data
public class ChangePasswordResponse extends BaseResponse {

    private Long userId;
    private String username;
    private LocalDateTime changeTime;
}