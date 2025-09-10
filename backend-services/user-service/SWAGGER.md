# Swagger 集成文档

## 概述

用户服务已成功集成Swagger API文档工具，提供完整的API接口文档和在线测试功能。

## 访问地址

启动用户服务后，可以通过以下地址访问Swagger文档：

- **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`
- **API文档JSON**: `http://localhost:8081/v2/api-docs`

## 集成的功能

### 1. 依赖配置

在 `pom.xml` 中添加了以下依赖：

```xml
<!-- Swagger dependencies -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```

### 2. 配置类

- **SwaggerConfig.java**: 配置Swagger文档的基本信息
  - API标题: "AI Q&A System - User Service API"
  - 描述: "用户服务模块API文档，提供用户注册、登录、信息管理等功能"
  - 版本: "1.0.0"
  - 扫描包: `com.ai.qa.user.api.controller`

### 3. Spring Boot 兼容性配置

在 `application.yml` 中添加了路径匹配策略：

```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
```

### 4. Controller注解

**UserController** 使用了以下Swagger注解：

- `@Api(tags = "用户管理", description = "用户相关的API接口")`
- `@ApiOperation`: 描述每个API方法的功能
- `@ApiParam`: 描述请求参数
- `@ApiResponses`: 定义可能的响应状态

### 5. DTO注解

所有DTO类都添加了详细的Swagger注解：

#### UserLoginRequest
```java
@ApiModel(description = "用户登录请求")
@ApiModelProperty(value = "用户名", required = true, example = "testuser")
```

#### UserRegisterRequest
```java
@ApiModel(description = "用户注册请求")
@ApiModelProperty(value = "昵称", required = false, example = "我的昵称")
```

#### UserResponse
```java
@ApiModel(description = "用户信息响应")
@ApiModelProperty(value = "用户ID", example = "1")
```

#### UpdateNicknameRequest
```java
@ApiModel(description = "更新昵称请求")
@ApiModelProperty(value = "新昵称", required = true, example = "新的昵称")
```

#### Response<T>
```java
@ApiModel(description = "统一响应格式")
@ApiModelProperty(value = "响应状态码", example = "200")
```

## API接口文档

### 用户管理 (User Management)

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 用户登录 | POST | `/api/users/login` | 通过用户名和密码进行登录验证 |
| 用户注册 | POST | `/api/users/register` | 注册新用户账号 |
| 更新用户昵称 | PUT | `/api/users/{userId}/nickname` | 根据用户ID更新用户昵称 |
| 根据用户ID查询用户 | GET | `/api/users/{userId}` | 通过用户ID获取用户详细信息 |
| 根据用户名查询用户 | GET | `/api/users/username/{username}` | 通过用户名获取用户详细信息 |

## 使用说明

### 1. 启动服务

```bash
cd /Users/leo/Documents/GitHub/ai-qa-system/backend-services/user-service
mvn spring-boot:run
```

### 2. 访问Swagger UI

启动服务后，打开浏览器访问: `http://localhost:8081/swagger-ui/index.html`

### 3. 在线测试

- 点击任意接口展开详细信息
- 点击 "Try it out" 按钮
- 填写请求参数
- 点击 "Execute" 执行请求
- 查看响应结果

## 示例请求

### 用户注册

```json
{
  "username": "testuser",
  "password": "password123",
  "nickname": "测试用户"
}
```

### 用户登录

```json
{
  "username": "testuser",
  "password": "password123"
}
```

### 更新昵称

```json
{
  "nickname": "新的昵称"
}
```

## 响应格式

所有接口都使用统一的响应格式：

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "userName": "testuser",
    "nickname": "测试用户",
    "createTime": "2023-01-01T10:00:00",
    "updateTime": "2023-01-01T10:00:00"
  }
}
```

## 错误响应

```json
{
  "code": 500,
  "message": "用户名或密码错误",
  "data": null
}
```

## 注意事项

1. 确保服务正常启动后再访问Swagger UI
2. 所有接口都需要正确的请求参数格式
3. 密码字段在实际环境中应该加密存储
4. 昵称字段支持中文和特殊字符
5. 时间格式为ISO 8601标准格式