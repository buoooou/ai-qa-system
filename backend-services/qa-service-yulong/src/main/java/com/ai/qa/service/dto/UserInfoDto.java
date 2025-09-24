package com.ai.qa.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户信息DTO
 * 
 * 用于Feign Client调用user-service时接收用户信息
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
}
