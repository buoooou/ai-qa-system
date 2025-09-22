// UserDTO.java（应用层数据传输对象）
package com.ai.qa.user.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}