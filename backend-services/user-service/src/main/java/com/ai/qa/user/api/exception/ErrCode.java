package com.ai.qa.user.api.exception;

public final class ErrCode {

    /***
     * 成功
     */
    public static final String SUCCESS = "00000";

    /***
     * 通用业务错误
     */
    public static final String COMMON_ERROR = "A0001";
    
    /***
     * 用户相关错误
     */
    public static final String USER_NOT_FOUND = "A0101";
    public static final String USER_PASSWORD_ERROR = "A0102";
    public static final String USER_ALREADY_EXISTS = "A0103";
    public static final String USER_NOT_LOGIN = "A0104";

    /***
     * 参数校验错误
     */
    public static final String PARAM_VALIDATE_ERROR = "A0201";
}