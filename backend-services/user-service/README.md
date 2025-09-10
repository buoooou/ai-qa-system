## Fullstack 第三期homework AI问答系统 

### 1.环境

- java version "17.0.16"
- Apache Maven 3.9.11
- SpringBoot 3.5.4

### 2.项目结构

├── infrastructure
│   ├── config
│   │   ├── JwtAuthFilter.java
│   │   ├── JwtProperties.java
│   │   ├── SecurityConfig.java
│   │   ├── SwaggerConfig.java
│   │   ├── UserPwdAuthProvider.java
│   │   └── WebConfig.java
│   ├── mapper
│   │   └── UserMapper.java
│   └── security
│       ├── CurrentUser.java
│       ├── CurrentUserMethodArgumentResolver.java
│       └── CustomUserDetailsService.java
├── com.ai.qa.user
│   ├── api
│   │   ├── controller
│   │   │   └── UserController.java
│   │   └── dto
│   │       ├── LoginReqDto.java
│   │       ├── RegisterReqDto.java
│   │       ├── UpdateNicknameReqDto.java
│   │       └── UserResponseDto.java
│   ├── exception
│   │   ├── BusinessException.java
│   │   ├── ErrCode.java
│   │   └── GlobalExceptionHandler.java
│   ├── application.service
│   │   └── Impl
│   │       └── UserServiceImpl.java
│   │   └── UserService.java
│   ├── common
│   │   └── JwtUtil.java
│   ├── domain
│   │   └── entity
│   │       └── User.java
│   └── repository
│       └── UserRepository.java
└── UserServiceApplication.java

### 3.主要功能  

1.用户注册
接口: POST /api/user/register
请求参数: RegisterReqDto 包含用户名、密码等信息。
响应: 注册成功后返回 UserResponseDto，包含用户基本信息。

2.用户登录
接口: POST /api/user/login
请求参数: LoginReqDto 包含用户名和密码。
响应: 登录成功后返回 UserResponseDto 和 JWT Token。

3.更新昵称
接口: PUT /api/user/update-nickname
请求参数: UpdateNicknameReqDto 包含新的昵称。
响应: 更新成功后返回 UserResponseDto。

4.认证鉴权
使用JWT进行认证鉴权。
JwtAuthFilter 进行Token验证。
SecurityConfig 配置安全策略。
CustomUserDetailsService 提供用户详情服务。

5.异常处理
BusinessException 自定义业务异常。
ErrCode 定义错误码。
GlobalExceptionHandler 全局异常处理器，统一处理各种异常的错误信息。

## 数据表更新（加用户昵称字段）
-- ----------------------------
-- 用户表 (user)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
`username` VARCHAR(255) NOT NULL COMMENT '用户名',
`nickname` VARCHAR(50) COMMENT '用户昵称',
`password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`),
UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';