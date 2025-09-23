package com.ai.qa.user.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应格式
 * 
 * 用于用户服务的统一API响应格式，包含状态码、消息和数据
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    
    /**
     * 响应状态码
     * 200: 成功
     * 400: 客户端错误
     * 500: 服务器错误
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private Long timestamp;
    
    /**
     * 构造函数 - 包含所有字段
     */
    public Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功响应 - 带数据
     * 
     * @param data 响应数据
     * @return Response<T> 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应 - 带消息和数据
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @return Response<T> 成功响应
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }
    
    /**
     * 成功响应 - 仅消息
     * 
     * @param message 响应消息
     * @return Response<Object> 成功响应
     */
    public static Response<Object> success(String message) {
        return new Response<>(200, message, null);
    }
    
    /**
     * 失败响应 - 带错误码和消息
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return Response<T> 失败响应
     */
    public static <T> Response<T> error(Integer code, String message) {
        return new Response<>(code, message, null);
    }
    
    /**
     * 失败响应 - 仅消息（默认400错误码）
     * 
     * @param message 错误消息
     * @return Response<T> 失败响应
     */
    public static <T> Response<T> error(String message) {
        return new Response<>(400, message, null);
    }
    
    /**
     * 服务器错误响应
     * 
     * @param message 错误消息
     * @return Response<Object> 服务器错误响应
     */
    public static Response<Object> serverError(String message) {
        return new Response<>(500, message, null);
    }
    
    /**
     * 参数错误响应
     * 
     * @param message 错误消息
     * @return Response<Object> 参数错误响应
     */
    public static Response<Object> badRequest(String message) {
        return new Response<>(400, message, null);
    }
    
    /**
     * 未授权响应
     * 
     * @param message 错误消息
     * @return Response<Object> 未授权响应
     */
    public static Response<Object> unauthorized(String message) {
        return new Response<>(401, message, null);
    }
    
    /**
     * 禁止访问响应
     * 
     * @param message 错误消息
     * @return Response<Object> 禁止访问响应
     */
    public static Response<Object> forbidden(String message) {
        return new Response<>(403, message, null);
    }
    
    /**
     * 资源未找到响应
     * 
     * @param message 错误消息
     * @return Response<T> 资源未找到响应
     */
    public static <T> Response<T> notFound(String message) {
        return new Response<>(404, message, null);
    }
    
    /**
     * 判断响应是否成功
     * 
     * @return boolean true-成功，false-失败
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
    
    /**
     * 判断响应是否失败
     * 
     * @return boolean true-失败，false-成功
     */
    public boolean isError() {
        return !isSuccess();
    }
}
