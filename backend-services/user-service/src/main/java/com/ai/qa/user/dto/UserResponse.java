package com.ai.qa.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.ai.qa.user.entity.User;
import com.ai.qa.user.common.CommonUtil;

/**
 * 用户信息响应DTO
 *
 * @author David
 * @version 1.0
 * @since 2025-09-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱（脱敏）
     */
    private String email;

    /**
     * 用户状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 从User实体转换为UserResponse
     *
     * @param user 用户实体
     * @return UserResponse 用户响应DTO
     */
    public static UserResponse fromUser(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(CommonUtil.maskUsername(user.getUserName()));
        response.setEmail(CommonUtil.maskEmail(user.getEmail()));
        response.setStatus(user.getStatus());

        // 格式化时间
        if (user.getCreateTime() != null) {
            response.setCreateTime(user.getCreateTime().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
        }

        if (user.getUpdateTime() != null) {
            response.setUpdateTime(user.getUpdateTime().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
        }

        return response;
    }

    /**
     * 从User实体转换为UserResponse（不脱敏版本，用于管理员查看）
     *
     * @param user 用户实体
     * @return UserResponse 用户响应DTO
     */
    public static UserResponse fromUserWithoutMask(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUserName());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());

        // 格式化时间
        if (user.getCreateTime() != null) {
            response.setCreateTime(user.getCreateTime().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
        }

        if (user.getUpdateTime() != null) {
            response.setUpdateTime(user.getUpdateTime().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
        }

        return response;
    }
}