package com.ai.qa.user.api.dto;

import com.ai.qa.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户响应 DTO
 */
@Data
@Schema(description = "用户响应信息")
public class UserResponseDTO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "昵称", example = "小明")
    private String nickname;

    @Schema(description = "创建时间", example = "2025-09-12T10:00:00")
    private String createTime;

    @Schema(description = "修改时间", example = "2025-09-12T10:30:00")
    private String updateTime;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.createTime = user.getCreateTime().toString();
        this.updateTime = user.getUpdateTime().toString();
    }
}
