### **User Service**
#### **项目简介**
这是一个用户管理微服务，提供用户注册、登录、信息查询等功能。

---

#### **Swagger 文档**
启动服务后，访问以下地址查看 API 文档：
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API JSON**: `http://localhost:8080/v2/api-docs`

---

#### **快速启动**
1. **依赖安装**  
   ```bash
   mvn clean install
   ```

2. **配置数据库**  
   修改 `application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/user_db
       username: root
       password: your_password
   ```

3. **启动服务**  
   ```bash
   mvn spring-boot:run
   ```

---

#### **API 文档**
| 接口           | 方法 | 路径               | 描述           |
|----------------|------|--------------------|----------------|
| 用户注册       | POST | /api/user/register | 注册新用户     |
| 用户登录       | POST | /api/user/login    | 用户登录       |
| 查询用户信息   | GET  | /api/user/{name}   | 根据用户名查询 |
| 修改昵称       | PUT  | /api/user/nick     | 更新用户昵称   |

---

#### **注意事项**
1. **密码安全**  
   - 密码使用 BCrypt 加密存储，确保安全性。

2. **字段约束**  
   - 用户名：长度 4-20 字符，仅支持字母、数字和下划线。
   - 密码：长度 8-32 字符，需包含大小写字母和数字。

3. **数据库表**  
   - 确保 `users` 表已创建，字段包括 `id`, `username`, `password`, `nick`, `create_time`, `update_time`。

---

#### **后续计划**
1. 增加邮箱验证功能。
2. 支持 OAuth2 第三方登录。
