package com.ai.qa.user.api.dto.response;

import lombok.Data;
import com.ai.qa.user.api.exception.ErrCode;;

@Data
public class Response<T> {

    private int code;
    private String message;
    private T data;

    // 预设成功响应
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage(ErrCode.SUCCESS);
        response.setData(data);
        return response;
    }

    // 预设失败响应
    public static <T> Response<T> error(int code, String message) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}
