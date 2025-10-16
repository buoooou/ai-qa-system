# 🚀 EC2 部署后验证清单

## 1. 🔍 基础服务检查

### 1.1 检查容器状态
```bash
# 登录 EC2 后执行
docker compose -f docker-compose.prod.yml ps

# 确保所有容器状态为 Up 或 healthy
```

### 1.2 检查网络连接
```bash
# 检查 Docker 网络
docker network ls | grep ai-qa-net

# 检查容器间网络连接
docker exec user-service-fyb ping mysql
docker exec qa-service-fyb ping mysql
docker exec api-gateway-fyb ping user-service-fyb
docker exec ai-qa-frontend ping api-gateway-fyb
```

### 1.3 检查日志
```bash
# 查看各服务日志
docker compose -f docker-compose.prod.yml logs -f [service-name]

# 关键服务日志检查
docker logs ai-qa-frontend 2>&1 | grep -i error
docker logs api-gateway-fyb 2>&1 | grep -i error
```

## 2. 🌐 网络连通性测试

### 2.1 从 EC2 本地测试
```bash
# 测试 API Gateway
curl -X GET http://localhost:8083/actuator/health

# 测试 User Service（通过 Gateway）
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"test@example.com","password":"password"}'

# 测试前端
curl -I http://localhost:3000
```

### 2.2 从外部浏览器测试
- 前端: http://[EC2公网IP]:3000
- API Gateway: http://[EC2公网IP]:8083/swagger-ui.html

## 3. 🔐 认证功能验证

### 3.1 检查环境变量
```bash
# 检查前端容器环境变量
docker exec ai-qa-frontend env | grep NEXT_PUBLIC_GATEWAY_URL
docker exec ai-qa-frontend env | grep AUTH_SECRET
docker exec ai-qa-frontend env | grep NODE_ENV

# 应该输出：
# NEXT_PUBLIC_GATEWAY_URL=http://[EC2公网IP]:8083
# AUTH_SECRET=b9a8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c3b2a1f0e9d8c7b6a5f4e3d2c1b0a9f8
# NODE_ENV=production
```

### 3.2 测试登录流程
1. 打开浏览器访问: http://[EC2公网IP]:3000
2. 应自动重定向到登录页: http://[EC2公网IP]:3000/login
3. 输入用户名/密码登录
4. 登录成功后应跳转到聊天页面
5. 检查右上角显示的用户名（不是 "guest"）

### 3.3 检查 Cookie
- 浏览器开发者工具 → Application → Cookies
- 检查是否有 `__Secure-next-auth.session-token` (生产环境)
- 检查 Cookie 的 Secure 和 HttpOnly 标志

## 4. 📊 关键配置点

### 4.1 GitHub Actions 已修复的问题
✅ **NEXT_PUBLIC_GATEWAY_URL**: 从 `http://api-gateway-fyb:8083` 改为 `http://${EC2_HOST}:8083`

### 4.2 NextAuth 生产环境配置
✅ **useSecureCookies**: 生产环境设置为 true
✅ **Cookie secure**: 生产环境启用
✅ **trustHost**: 启用以信任代理主机

### 4.3 CORS 配置
✅ **Gateway**: 已配置允许所有来源 (`setAllowedOriginPattern("*")`)

## 5. 🚨 常见问题排查

### 5.1 登录后仍显示 guest
**症状**: 登录成功但页面显示用户为 guest
**原因**: NextAuth session 回调问题
**解决方案**:
1. 检查浏览器控制台错误
2. 清除浏览器 Cookie 和 localStorage
3. 重启前端容器: `docker restart ai-qa-frontend`

### 5.2 API 请求失败
**症状**: 前端无法调用后端 API
**原因**: NEXT_PUBLIC_GATEWAY_URL 配置错误
**解决方案**:
1. 确认值为 EC2 公网 IP:8083
2. 检查 EC2 安全组是否开放 3000 和 8083 端口

### 5.3 Cookie 无法设置
**症状**: 登录后立即被踢出
**原因**: HTTPS/HTTP Cookie 配置问题
**解决方案**:
1. HTTP 环境确保 useSecureCookies: false
2. HTTPS 环境确保 useSecureCookies: true
3. 检查 AUTH_SECRET 是否设置

### 5.4 CORS 错误
**症状**: 浏览器控制台显示 CORS 错误
**原因**: 跨域配置问题
**解决方案**:
1. 检查 Gateway 的 CORS 配置
2. 确认请求来源端口是否正确

## 6. 🔧 必要的修复命令

### 6.1 更新环境变量（如需）
```bash
# 停止服务
docker compose -f docker-compose.prod.yml down

# 编辑 .env 文件
vim ~/ai-qa-system/.env

# 重新启动
docker compose -f docker-compose.prod.yml up -d
```

### 6.2 重新构建部署
```bash
# 清理旧容器和镜像
docker compose -f docker-compose.prod.yml down --remove-orphans
docker system prune -f --volumes

# 重新部署
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## 7. 📝 监控建议

### 7.1 设置日志监控
```bash
# 实时查看关键日志
docker logs -f ai-qa-frontend 2>&1 | grep -E "(error|Error|ERROR)"
docker logs -f api-gateway-fyb 2>&1 | grep -E "(error|Error|ERROR)"
```

### 7.2 健康检查脚本
```bash
#!/bin/bash
# 创建健康检查脚本
while true; do
    curl -f http://localhost:3000 > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "$(date): Frontend is healthy"
    else
        echo "$(date): Frontend is down!"
    fi
    sleep 30
done
```

## 8. ✅ 部署成功标准

- [ ] 所有容器状态为 Up
- [ ] 前端页面可正常访问
- [ ] 登录功能正常
- [ ] 登录后不显示 guest
- [ ] 可以进入聊天页面
- [ ] API 调用正常（无 CORS 错误）
- [ ] Cookie 正确设置
- [ ] 日志无严重错误

---

**重要提醒**:
1. 确保 EC2 安全组开放 3000 和 8083 端口
2. 确保浏览器使用 HTTP/HTTPS 与环境变量匹配
3. 如遇问题，优先检查 Docker 日志和环境变量配置