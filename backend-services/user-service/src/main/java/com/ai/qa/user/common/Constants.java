package com.ai.qa.user.common;

public interface Constants {

    String ROLE_USER = "ROLE_USER";

    String MSG_RES_SUCCESS = "用户操作成功。";
    String MSG_RES_ERROR  = "用户操作失败。";

    String MSG_GLOBAL_INTERNAL_ERROR  = "服务器内部错误。 请联系管理员。";

    String MSG_USER_AUTHENTICATE_SUCCESS = "用户认证成功。";
    String MSG_USER_AUTHENTICATE_FAIL = "用户认证失败。用户名或密码错误。";
    String MSG_USER_LOGIN_SUCCESS = "用户登录成功。";

    String MSG_USER_REGISTER_SUCCESS = "用户注册成功。 请重新登录。";
    String MSG_USER_ALREADY_EXIST = "用户名已存在。";
    String MSG_USER_REGISTER_FAIL = "用户注册失败。";

    String MSG_CHANGE_PASSWORD_FAIL = "密码修改失败。";
    String MSG_OLD_PASSWORD_INCORRECT = "旧密码不正确。";

    String MSG_NICKNAME_UPDATE_SUCCESS = "昵称修改成功。";
    String MSG_NICKNAME_UPDATE_FAIL = "昵称修改失败。";

    String MSG_USER_BAD_REQUEST = "错误的请求。";
    String MSG_USER_NOT_FOUND = "该用户不存在。";

    String MSG_NICKNAME_IS_EMPTY = "昵称不能为空。";
    String MSG_NICKNAME_TOO_LONG = "昵称长度不能超过50个字符。";
    String MSG_NICKNAME_UNCHANGED = "新昵称不能与旧昵称相同。";
    String MSG_USERNAME_IS_EMPTY = "用户名不能为空。";
    String MSG_PASSWORD_IS_EMPTY = "密码不能为空。";
    String MSG_PASSWORD_CONFIRM_ERROR = "确认的密码不一致";
}
