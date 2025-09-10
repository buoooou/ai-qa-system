package com.ai.qa.user.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "统一响应格式")
@Data
public class Response<T> {
    
    @ApiModelProperty(value = "响应状态码", example = "200")
    private int code;
    
    @ApiModelProperty(value = "响应消息", example = "Success")
    private String message;
    
    @ApiModelProperty(value = "响应数据")
    private T data;

    public Response() {}

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "Success", data);
    }

    public static <T> Response<T> success() {
        return new Response<>(200, "Success", null);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }
}
