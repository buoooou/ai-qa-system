# 🚀 HTTP 生产环境部署指南

## ⚠️ 重要说明

当前配置专为 **HTTP 生产环境** 设计（无 HTTPS、无域名）。当您后续升级到 HTTPS 和域名时，需要更新相关配置。

## 📋 前置条件

1. **EC2 实例**已创建并可访问
2. **安全组**已开放以下端口：
   - `3000`：前端访问
   - `8083`：API Gateway（用于 Swagger 和调试）
3. **GitHub Secrets**已配置：
   - `EC2_HOST`: EC2 公网 IP 地址
   - `EC2_USERNAME`: SSH 用户名（通常是 `ec2-user` 或 `ubuntu`）
   - `SSH_PRIVATE_KEY`: EC2 SSH 私钥
   - `DOCKERHUB_USERNAME`: Docker Hub 用户名
   - `DOCKERHUB_TOKEN`: Docker Hub 访问令牌
   - `NACOS_SERVER_ADDR`: Nacos 服务器地址
   - `GEMINI_API_KEY`: Gemini API 密钥
   - `JWT_SECRET`: JWT 签名密钥

## 🔧 关键配置说明

### 1. Cookie 配置（HTTP 环境）

由于使用 HTTP，所有 Cookie 相关配置都设置为非安全模式：

```typescript
// auth.config.ts
useSecureCookies: false, // HTTP 环境必须为 false
cookies: {
  sessionToken: {
    name: "next-auth.session-token", // 不能使用 __Secure- 前缀
    options: {
      secure: false, // HTTP 环境必须为 false
      // ...
    }
  }
}
```

### 2. Middleware 配置

```typescript
// middleware.ts
token = await getToken({
  req: request,
  secret: process.env.AUTH_SECRET,
  secureCookie: false, // 必须为 false
  cookieName: "next-auth.session-token",
});
```

### 3. 环境变量

GitHub Actions 自动设置：
```bash
NEXT_PUBLIC_GATEWAY_URL=http://${EC2_HOST}:8083
AUTH_SECRET=b9a8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c3b2a1f0e9d8c7b6a5f4e3d2c1b0a9f8
NODE_ENV=production
```

## 🚀 部署步骤

### 1. 推送代码到 main 分支

```bash
git add .
git commit -m "Configure for HTTP production deployment"
git push origin main
```

### 2. GitHub Actions 自动执行

GitHub Actions 将自动：
1. 构建所有 Docker 镜像
2. 推送到 Docker Hub
3. SSH 连接到 EC2
4. 部署所有服务

### 3. 验证部署

#### 3.1 检查服务状态
```bash
# SSH 到 EC2
ssh -i your-key.pem ec2-user@YOUR_EC2_IP

# 检查容器状态
docker compose -f ~/ai-qa-system/docker-compose.prod.yml ps
```

#### 3.2 测试访问
- 前端：http://YOUR_EC2_IP:3000
- API Gateway Swagger：http://YOUR_EC2_IP:8083/swagger-ui.html

#### 3.3 测试登录流程
1. 访问 http://YOUR_EC2_IP:3000
2. 自动重定向到登录页
3. 输入用户名/密码登录
4. 成功后应跳转到聊天页面
5. 右上角应显示用户名（不是 "guest"）

## 🔍 故障排查

### 问题 1：登录后停留在登录页面

**原因**：Cookie 配置问题
**解决方案**：
```bash
# 1. 检查前端容器环境变量
docker exec ai-qa-frontend env | grep NEXT_PUBLIC_GATEWAY_URL

# 2. 检查日志
docker logs ai-qa-frontend 2>&1 | grep -i "session\|cookie\|auth"

# 3. 清除浏览器缓存
# - 打开开发者工具
# - Application → Storage → Clear storage
# - 或使用无痕窗口测试
```

### 问题 2：API 调用失败

**原因**：NEXT_PUBLIC_GATEWAY_URL 错误
**检查**：
```bash
# 确认值为 EC2 公网 IP，不是容器名
echo $NEXT_PUBLIC_GATEWAY_URL
# 应该输出：http://YOUR_EC2_IP:8083
```

### 问题 3：CORS 错误

**原因**：跨域配置问题
**解决方案**：Gateway 已配置允许所有来源，确保请求使用正确的 URL。

## 📊 监控命令

```bash
# 实时查看前端日志
docker logs -f ai-qa-frontend

# 查看所有服务状态
docker compose -f ~/ai-qa-system/docker-compose.prod.yml ps

# 查看特定服务日志
docker logs api-gateway-fyb 2>&1 | grep -E "(error|Error|ERROR)"

# 测试服务连通性
curl -X GET http://localhost:8083/actuator/health
```

## 🔄 未来升级到 HTTPS

当您准备好使用 HTTPS 和域名时，需要更新：

1. **NextAuth 配置** (auth.config.ts)：
```typescript
useSecureCookies: true,
cookies: {
  sessionToken: {
    name: "__Secure-next-auth.session-token",
    options: {
      secure: true,
      // ...
    }
  }
}
```

2. **Middleware 配置**：
```typescript
secureCookie: true,
cookieName: "__Secure-next-auth.session-token",
```

3. **环境变量**：
```bash
NEXT_PUBLIC_GATEWAY_URL=https://your-domain.com:8083
```

## ✅ 成功标准

- [ ] 所有容器运行正常
- [ ] 前端可通过 HTTP 访问
- [ ] 登录功能正常工作
- [ ] 登录后能进入聊天页面
- [ ] 用户显示正确的用户名
- [ ] API 调用正常
- [ ] 无 CORS 错误

---

**重要提醒**：
- 当前配置**仅适用于 HTTP 环境**
- 生产数据请使用强密码
- 定期更新依赖和镜像
- 监控日志确保服务正常