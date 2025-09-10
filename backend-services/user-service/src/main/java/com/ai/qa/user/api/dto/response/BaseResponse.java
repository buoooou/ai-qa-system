package com.ai.qa.user.api.dto.response;

import lombok.Data;
import com.ai.qa.user.api.exception.ErrCode;

/**
 * 通用响应基类
 * 提供统一的成功/失败封装，便于前后端一致处理
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Data
public class BaseResponse {

    /** 成功标志 */
    private Boolean success;

    /** 提示消息 */
    private String message;

    /** 业务错误码 */
    private Integer errorCode;

    /* ---------- 静态工厂 ---------- */

    public static BaseResponse success() {
        BaseResponse r = new BaseResponse();
        r.setSuccess(true);
        r.setMessage(ErrCode.MSG_SUCCESS);
        r.setErrorCode(ErrCode.SUCCESS);
        return r;
    }

    public static BaseResponse error(String message, Integer errorCode) {
        BaseResponse r = new BaseResponse();
        r.setSuccess(false);
        r.setMessage(message);
        r.setErrorCode(errorCode);
        return r;
    }

    public static BaseResponse error(Integer errCode, String errMessage) {
        return error(errMessage, errCode);
    }

    public static BaseResponse error(Integer errCode) {
        String msg;
        switch (errCode) {
            case ErrCode.USER_NOT_FOUND:       msg = ErrCode.MSG_USER_NOT_FOUND;       break;
            case ErrCode.USER_ALREADY_EXISTS:  msg = ErrCode.MSG_USER_ALREADY_EXISTS;  break;
            case ErrCode.PASSWORD_INCORRECT:   msg = ErrCode.MSG_PASSWORD_INCORRECT;   break;
            case ErrCode.PASSWORD_MISMATCH:    msg = ErrCode.MSG_PASSWORD_MISMATCH;    break;
            default:                           msg = ErrCode.MSG_BAD_REQUEST;
        }
        return error(msg, errCode);
    }
}