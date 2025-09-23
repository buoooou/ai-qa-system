package com.ai.qa.user.api.exception;

/**
 * 错误码常量类
 * 
 * 定义用户服务中使用的所有错误码和错误消息
 * 采用分类管理，便于维护和扩展
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
public final class ErrCode {
    
    /**
     * 私有构造函数，防止实例化
     */
    private ErrCode() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // ==================== 通用状态码 ====================
    
    /**
     * 成功
     */
    public static final int SUCCESS = 200;
    public static final String SUCCESS_MSG = "操作成功";
    
    /**
     * 系统错误
     */
    public static final int SYSTEM_ERROR = 500;
    public static final String SYSTEM_ERROR_MSG = "系统内部错误";
    
    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = 400;
    public static final String PARAM_ERROR_MSG = "参数错误";
    
    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;
    public static final String UNAUTHORIZED_MSG = "未授权访问";
    
    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;
    public static final String FORBIDDEN_MSG = "禁止访问";
    
    /**
     * 资源未找到
     */
    public static final int NOT_FOUND = 404;
    public static final String NOT_FOUND_MSG = "资源未找到";
    
    // ==================== 用户相关错误码 (1000-1999) ====================
    
    /**
     * 用户不存在
     */
    public static final int USER_NOT_FOUND = 1001;
    public static final String USER_NOT_FOUND_MSG = "用户不存在";
    
    /**
     * 用户名已存在
     */
    public static final int USERNAME_EXISTS = 1002;
    public static final String USERNAME_EXISTS_MSG = "用户名已存在";
    
    /**
     * 邮箱已存在
     */
    public static final int EMAIL_EXISTS = 1003;
    public static final String EMAIL_EXISTS_MSG = "邮箱已存在";
    
    /**
     * 密码错误
     */
    public static final int PASSWORD_ERROR = 1004;
    public static final String PASSWORD_ERROR_MSG = "密码错误";
    
    /**
     * 用户名格式错误
     */
    public static final int USERNAME_FORMAT_ERROR = 1005;
    public static final String USERNAME_FORMAT_ERROR_MSG = "用户名格式错误，必须是4-20位字母数字下划线";
    
    /**
     * 密码格式错误
     */
    public static final int PASSWORD_FORMAT_ERROR = 1006;
    public static final String PASSWORD_FORMAT_ERROR_MSG = "密码格式错误，至少8位且包含字母和数字";
    
    /**
     * 邮箱格式错误
     */
    public static final int EMAIL_FORMAT_ERROR = 1007;
    public static final String EMAIL_FORMAT_ERROR_MSG = "邮箱格式错误";
    
    /**
     * 确认密码不匹配
     */
    public static final int PASSWORD_CONFIRM_ERROR = 1008;
    public static final String PASSWORD_CONFIRM_ERROR_MSG = "确认密码不匹配";
    
    /**
     * 用户已被禁用
     */
    public static final int USER_DISABLED = 1009;
    public static final String USER_DISABLED_MSG = "用户已被禁用";
    
    /**
     * 旧密码错误
     */
    public static final int OLD_PASSWORD_ERROR = 1010;
    public static final String OLD_PASSWORD_ERROR_MSG = "旧密码错误";
    
    /**
     * 新密码不能与旧密码相同
     */
    public static final int SAME_PASSWORD_ERROR = 1011;
    public static final String SAME_PASSWORD_ERROR_MSG = "新密码不能与旧密码相同";
    
    // ==================== 认证相关错误码 (2000-2999) ====================
    
    /**
     * 登录失败
     */
    public static final int LOGIN_FAILED = 2001;
    public static final String LOGIN_FAILED_MSG = "登录失败";
    
    /**
     * 登录过期
     */
    public static final int LOGIN_EXPIRED = 2002;
    public static final String LOGIN_EXPIRED_MSG = "登录已过期，请重新登录";
    
    /**
     * Token无效
     */
    public static final int INVALID_TOKEN = 2003;
    public static final String INVALID_TOKEN_MSG = "Token无效";
    
    /**
     * Token已过期
     */
    public static final int TOKEN_EXPIRED = 2004;
    public static final String TOKEN_EXPIRED_MSG = "Token已过期";
    
    /**
     * 权限不足
     */
    public static final int INSUFFICIENT_PERMISSIONS = 2005;
    public static final String INSUFFICIENT_PERMISSIONS_MSG = "权限不足";
    
    // ==================== 业务相关错误码 (3000-3999) ====================
    
    /**
     * 注册失败
     */
    public static final int REGISTER_FAILED = 3001;
    public static final String REGISTER_FAILED_MSG = "注册失败";
    
    /**
     * 更新用户信息失败
     */
    public static final int UPDATE_USER_FAILED = 3002;
    public static final String UPDATE_USER_FAILED_MSG = "更新用户信息失败";
    
    /**
     * 修改密码失败
     */
    public static final int CHANGE_PASSWORD_FAILED = 3003;
    public static final String CHANGE_PASSWORD_FAILED_MSG = "修改密码失败";
    
    /**
     * 用户状态变更失败
     */
    public static final int CHANGE_USER_STATUS_FAILED = 3004;
    public static final String CHANGE_USER_STATUS_FAILED_MSG = "用户状态变更失败";
    
    // ==================== 数据库相关错误码 (4000-4999) ====================
    
    /**
     * 数据库连接失败
     */
    public static final int DATABASE_CONNECTION_ERROR = 4001;
    public static final String DATABASE_CONNECTION_ERROR_MSG = "数据库连接失败";
    
    /**
     * 数据库操作失败
     */
    public static final int DATABASE_OPERATION_ERROR = 4002;
    public static final String DATABASE_OPERATION_ERROR_MSG = "数据库操作失败";
    
    /**
     * 数据完整性约束违反
     */
    public static final int DATA_INTEGRITY_VIOLATION = 4003;
    public static final String DATA_INTEGRITY_VIOLATION_MSG = "数据完整性约束违反";
    
    // ==================== 外部服务相关错误码 (5000-5999) ====================
    
    /**
     * 外部服务调用失败
     */
    public static final int EXTERNAL_SERVICE_ERROR = 5001;
    public static final String EXTERNAL_SERVICE_ERROR_MSG = "外部服务调用失败";
    
    /**
     * 服务不可用
     */
    public static final int SERVICE_UNAVAILABLE = 5002;
    public static final String SERVICE_UNAVAILABLE_MSG = "服务暂时不可用";
    
    /**
     * 服务超时
     */
    public static final int SERVICE_TIMEOUT = 5003;
    public static final String SERVICE_TIMEOUT_MSG = "服务调用超时";
    
    // ==================== 工具方法 ====================
    
    /**
     * 根据错误码获取错误消息
     * 
     * @param code 错误码
     * @return String 错误消息
     */
    public static String getMessageByCode(int code) {
        switch (code) {
            case SUCCESS: return SUCCESS_MSG;
            case SYSTEM_ERROR: return SYSTEM_ERROR_MSG;
            case PARAM_ERROR: return PARAM_ERROR_MSG;
            case UNAUTHORIZED: return UNAUTHORIZED_MSG;
            case FORBIDDEN: return FORBIDDEN_MSG;
            case NOT_FOUND: return NOT_FOUND_MSG;
            
            case USER_NOT_FOUND: return USER_NOT_FOUND_MSG;
            case USERNAME_EXISTS: return USERNAME_EXISTS_MSG;
            case EMAIL_EXISTS: return EMAIL_EXISTS_MSG;
            case PASSWORD_ERROR: return PASSWORD_ERROR_MSG;
            case USERNAME_FORMAT_ERROR: return USERNAME_FORMAT_ERROR_MSG;
            case PASSWORD_FORMAT_ERROR: return PASSWORD_FORMAT_ERROR_MSG;
            case EMAIL_FORMAT_ERROR: return EMAIL_FORMAT_ERROR_MSG;
            case PASSWORD_CONFIRM_ERROR: return PASSWORD_CONFIRM_ERROR_MSG;
            case USER_DISABLED: return USER_DISABLED_MSG;
            case OLD_PASSWORD_ERROR: return OLD_PASSWORD_ERROR_MSG;
            case SAME_PASSWORD_ERROR: return SAME_PASSWORD_ERROR_MSG;
            
            case LOGIN_FAILED: return LOGIN_FAILED_MSG;
            case LOGIN_EXPIRED: return LOGIN_EXPIRED_MSG;
            case INVALID_TOKEN: return INVALID_TOKEN_MSG;
            case TOKEN_EXPIRED: return TOKEN_EXPIRED_MSG;
            case INSUFFICIENT_PERMISSIONS: return INSUFFICIENT_PERMISSIONS_MSG;
            
            case REGISTER_FAILED: return REGISTER_FAILED_MSG;
            case UPDATE_USER_FAILED: return UPDATE_USER_FAILED_MSG;
            case CHANGE_PASSWORD_FAILED: return CHANGE_PASSWORD_FAILED_MSG;
            case CHANGE_USER_STATUS_FAILED: return CHANGE_USER_STATUS_FAILED_MSG;
            
            case DATABASE_CONNECTION_ERROR: return DATABASE_CONNECTION_ERROR_MSG;
            case DATABASE_OPERATION_ERROR: return DATABASE_OPERATION_ERROR_MSG;
            case DATA_INTEGRITY_VIOLATION: return DATA_INTEGRITY_VIOLATION_MSG;
            
            case EXTERNAL_SERVICE_ERROR: return EXTERNAL_SERVICE_ERROR_MSG;
            case SERVICE_UNAVAILABLE: return SERVICE_UNAVAILABLE_MSG;
            case SERVICE_TIMEOUT: return SERVICE_TIMEOUT_MSG;
            
            default: return "未知错误";
        }
    }
}
