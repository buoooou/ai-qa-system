package com.ai.qa.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 问答请求DTO
 * 
 * 用于接收前端传来的问答请求信息
 * 包含用户ID和问题内容，以及数据验证注解
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
@Data                    // Lombok注解：自动生成getter、setter、toString等方法
@NoArgsConstructor       // Lombok注解：生成无参构造函数
@AllArgsConstructor      // Lombok注解：生成全参构造函数
public class QaRequest {
    
    /**
     * 用户ID
     * 
     * 验证规则：
     * - 不能为空
     * - 用于标识是哪个用户提出的问题
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 用户问题
     * 
     * 验证规则：
     * - 不能为空或空白字符
     * - 长度在1-2000个字符之间（考虑到问题的复杂性）
     */
    @NotBlank(message = "问题内容不能为空")
    @Size(min = 1, max = 2000, message = "问题长度必须在1-2000个字符之间")
    private String question;
    
    /**
     * 会话上下文（可选）
     * 
     * 用于保持对话的连续性，可以包含之前的对话历史
     * 这个字段是可选的，如果不提供则表示开始新的对话
     */
    private String context;
}
