package com.ai.qa.user.common.constants;

public interface Constants {

    String ROLE_USER = "ROLE_USER";

    String API_RES_SUCCESS  = "Success";
    String API_RES_ERROR  = "Error";

    String GLOBAL_ERROR_MSG  = "服务器内部错误。 请联系管理员。";

    String MSG_USER_LOGIN_SUCCESS = "用户认证成功。";
    String MSG_USER_LOGIN_FAIL = "用户名或密码错误。";

    String MSG_USER_REGISTER_SUCCESS = "用户注册成功。 请重新登录。";
    String MSG_USER_REGISTER_FAIL = "用户名已存在。";

    String MSG_UPDATED_NICKNAME_SUCCESS = "昵称修改成功。";
    String MSG_UPDATED_NICKNAME_FAIL = "昵称修改失败。";

    String MSG_USER_NOT_FOUND = "该用户不存在。";
}
