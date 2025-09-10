# JWT认证和错误处理集成文档

## 概述

用户服务已成功集成JWT认证和全局错误处理功能，提供安全的API访问控制和统一的错误响应格式。

## JWT认证功能

### 1. JWT工具类 (JwtUtil)

**位置**: `src/main/java/com/ai/qa/user/infrastructure/util/JwtUtil.java`

**主要功能**:
- 生成JWT令牌
- 验证JWT令牌
- 提取用户信息
- 检查令牌过期

**配置参数**:
```yaml
jwt:
  secret: myVerySecretKeyForJWTTokenGenerationThatShouldBeLongEnough  # JWT密钥
  expiration: 86400  # 令牌有效期（秒）
```

### 2. JWT认证过滤器 (JwtAuthenticationFilter)

**位置**: `src/main/java/com/ai/qa/user/infrastructure/security/JwtAuthenticationFilter.java`

**功能**:
- 拦截所有HTTP请求
- 验证Authorization头中的JWT令牌
- 设置Spring Security认证上下文
- 处理认证失败情况

**不需要认证的路径**:
- `/api/users/login` - 用户登录
- `/api/users/register` - 用户注册
- `/swagger-ui/**` - Swagger UI
- `/v2/api-docs` - API文档
- `/actuator/**` - 监控端点

### 3. 用户详情服务 (CustomUserDetailsService)

**位置**: `src/main/java/com/ai/qa/user/infrastructure/security/CustomUserDetailsService.java`

**功能**:
- 实现Spring Security的UserDetailsService接口
- 从数据库加载用户信息
- 为Spring Security提供用户认证信息

### 4. 安全配置 (SecurityConfig)

**位置**: `src/main/java/com/ai/qa/user/infrastructure/config/SecurityConfig.java`

**配置内容**:
- 禁用CSRF保护（适用于无状态API）
- 配置会话为无状态模式
- 设置JWT过滤器
- 配置访问权限规则
- 密码编码器配置（BCrypt）

## 错误处理功能

### 1. 错误码枚举 (ErrCode)

**位置**: `src/main/java/com/ai/qa/user/api/exception/ErrCode.java`

**定义的错误类型**:

| 错误码 | 错误类型 | 描述 |
|--------|----------|------|
| 200 | SUCCESS | 操作成功 |
| 400 | PARAM_ERROR | 参数错误 |
| 401 | UNAUTHORIZED | 未授权访问 |
| 403 | FORBIDDEN | 访问被禁止 |
| 404 | NOT_FOUND | 资源不存在 |
| 405 | METHOD_NOT_ALLOWED | 请求方法不允许 |
| 500 | SYSTEM_ERROR | 系统内部错误 |
| 1001 | USER_NOT_FOUND | 用户不存在 |
| 1002 | USERNAME_ALREADY_EXISTS | 用户名已存在 |
| 1003 | INVALID_USERNAME_OR_PASSWORD | 用户名或密码错误 |
| 1004 | USER_DISABLED | 用户已被禁用 |
| 2001 | TOKEN_EXPIRED | Token已过期 |
| 2002 | TOKEN_INVALID | Token无效 |
| 2003 | TOKEN_MISSING | Token缺失 |
| 3001 | VALIDATION_ERROR | 数据验证失败 |

### 2. 业务异常类 (BusinessException)

**位置**: `src/main/java/com/ai/qa/user/api/exception/BusinessException.java`

**功能**:
- 继承RuntimeException
- 包含错误码和错误信息
- 支持自定义错误消息

**使用方式**:
```java
// 使用预定义错误码
throw new BusinessException(ErrCode.USER_NOT_FOUND);

// 使用自定义错误消息
throw new BusinessException(ErrCode.VALIDATION_ERROR, "用户名长度必须在3-20之间");

// 使用错误码和消息
throw new BusinessException(1001, "指定的用户不存在");
```

### 3. 全局异常处理器 (GlobalExceptionHandler)

**位置**: `src/main/java/com/ai/qa/user/api/exception/GlobalExceptionHandler.java`

**处理的异常类型**:
- `BusinessException` - 业务异常
- `AuthenticationException` - 认证异常
- `BadCredentialsException` - 凭证错误
- `AccessDeniedException` - 访问拒绝
- `MethodArgumentNotValidException` - 参数验证异常
- `ConstraintViolationException` - 约束验证异常
- `IllegalArgumentException` - 非法参数异常
- `Exception` - 通用异常

## API接口更新

### 1. 登录接口

**更新内容**:
- 返回类型从`UserResponse`改为`LoginResponse`
- 包含JWT令牌、令牌类型、过期时间和用户信息

**响应示例**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "userName": "testuser",
      "nickname": "测试用户",
      "createTime": "2023-01-01T10:00:00",
      "updateTime": "2023-01-01T10:00:00"
    }
  }
}
```

### 2. 错误响应格式

**统一格式**:
```json
{
  "code": 1003,
  "message": "用户名或密码错误",
  "data": null
}
```

### 3. 受保护的API

除了登录和注册接口外，所有其他API都需要在请求头中携带JWT令牌：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 使用示例

### 1. 用户注册和登录流程

```bash
# 1. 注册用户
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "nickname": "测试用户"
  }'

# 2. 用户登录
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 3. 使用令牌访问受保护的API
curl -X GET http://localhost:8081/api/users/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 2. 错误处理示例

```bash
# 1. 用户名或密码错误
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wronguser",
    "password": "wrongpass"
  }'

# 响应:
{
  "code": 1003,
  "message": "用户名或密码错误",
  "data": null
}

# 2. 令牌无效或过期
curl -X GET http://localhost:8081/api/users/1 \
  -H "Authorization: Bearer invalid_token"

# 响应:
{
  "code": 2002,
  "message": "Token无效",
  "data": null
}

# 3. 缺少令牌
curl -X GET http://localhost:8081/api/users/1

# 响应:
{
  "code": 2003,
  "message": "Token缺失",
  "data": null
}
```

## 安全特性

### 1. 密码加密
- 使用BCrypt加密算法
- 自动生成盐值
- 密码验证时自动处理

### 2. JWT令牌安全
- HMAC SHA-256签名算法
- 可配置的密钥和过期时间
- 令牌包含用户ID和用户名信息

### 3. 会话管理
- 无状态设计，不依赖服务器会话
- 令牌自包含用户信息
- 支持水平扩展

## 配置说明

### application.yml配置

```yaml
# JWT配置
jwt:
  secret: myVerySecretKeyForJWTTokenGenerationThatShouldBeLongEnough  # 生产环境请使用更复杂的密钥
  expiration: 86400  # 24小时（秒）

# 日志配置
logging:
  level:
    com.ai.qa.user: DEBUG  # 应用日志级别
    org.springframework.security: DEBUG  # 安全日志级别
```

## 注意事项

1. **JWT密钥安全**: 生产环境中应使用更复杂的密钥，并定期轮换
2. **令牌过期处理**: 客户端需要处理令牌过期情况，实现自动刷新或重新登录
3. **HTTPS使用**: 生产环境中应使用HTTPS传输JWT令牌
4. **日志安全**: 确保敏感信息（如密码）不会记录到日志中
5. **错误信息**: 不要在错误响应中暴露敏感的系统信息

## 测试建议

1. **功能测试**: 验证登录、注册、令牌验证等核心功能
2. **安全测试**: 测试各种攻击场景，如无效令牌、过期令牌等
3. **性能测试**: 验证JWT验证的性能影响
4. **集成测试**: 确保与其他服务的集成正常工作