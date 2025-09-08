package com.ai.qa.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一API响应格式DTO
 * 
 * 用于统一所有接口的返回格式，包含状态码、消息和数据
 * 提供静态方法快速创建成功和失败的响应
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 * @param <T> 响应数据的类型
 */
@Data                    // Lombok注解：自动生成getter、setter、toString等方法
@NoArgsConstructor       // Lombok注解：生成无参构造函数
@AllArgsConstructor      // Lombok注解：生成全参构造函数
public class ApiResponse<T> {
    
    /**
     * 响应状态码
     * 200: 成功
     * 400: 客户端错误（如参数验证失败）
     * 401: 未授权
     * 500: 服务器内部错误
     */
    private Integer code;
    
    /**
     * 响应消息
     * 用于向前端显示操作结果的描述信息
     */
    private String message;
    
    /**
     * 响应数据
     * 泛型类型，可以是任何类型的数据
     */
    private T data;
    
    /**
     * 创建成功响应（带数据）
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponse<T> 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }
    
    /**
     * 创建成功响应（带自定义消息和数据）
     * 
     * @param message 自定义成功消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponse<T> 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
    
    /**
     * 创建成功响应（仅消息，无数据）
     * 
     * @param message 成功消息
     * @param <T> 数据类型
     * @return ApiResponse<T> 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null);
    }
    
    /**
     * 创建失败响应
     * 
     * @param code 错误状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ApiResponse<T> 失败响应对象
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
    
    /**
     * 创建失败响应（默认400状态码）
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ApiResponse<T> 失败响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(400, message, null);
    }
}
