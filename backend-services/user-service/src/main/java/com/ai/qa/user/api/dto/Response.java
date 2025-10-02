package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一响应格式
 */
@Data
@Schema(description = "统一响应格式")
public class Response<T> {
// TODO: 序列化时忽略null字段
	//	@JsonInclude(JsonInclude.Include.NON_NULL) // 仅在data不为null时序列化
	//	private T data;

    @Schema(description = "响应码", example = "200")
    private Integer code;

    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "时间戳", example = "1694419200000")
    private Long timestamp;

    public Response() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    public static <T> Response<T> success(String message, T data) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(Integer code, String message) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static <T> Response<T> error(String message) {
        return error(500, message);
    }
}
