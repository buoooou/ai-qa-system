package com.ai.qa.service.common;

public interface Constants {

    String ROLE_USER = "ROLE_USER";

    String MSG_RES_SUCCESS = "问答成功。";
    String MSG_RES_ERROR  = "问答处理失败。";

    String MSG_GLOBAL_INTERNAL_ERROR  = "服务器内部错误。 请联系管理员。";
    String MSG_FETCH_QA_HISTORY_FAIL  = "查询用户问答历史失败。";
    String MSG_QA_HISTORY_NOT_FOUND  = "问答记录不存在。";
    String MSG_DELETE_QA_HISTORY_FAIL  = "删除用户问答历史失败。";

    String MSG_USER_BAD_REQUEST = "错误的请求。";

    String MSG_GLOBAL_SERVICE_UNAVAILABLE  = "用户服务暂时不可用。";

    String MSG_BAD_REQUEST_USERID_EMPTY = "用户ID不能为空";
    String MSG_BAD_REQUEST_SESSIONID_EMPTY = "会话ID不能为空";
    String MSG_BAD_REQUEST_QUESTION_EMPTY = "问题不能为空";
    String MSG_BAD_REQUEST_ANSWER_EMPTY = "回答不能为空";
}
