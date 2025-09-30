# AI智能问答系统 - 前端项目

## 📚 项目简介
这是一个基于Next.js构建的AI智能问答系统前端应用，集成了Google AI服务，提供流畅的智能对话体验。系统支持用户注册、登录、会话管理和个性化设置，采用现代化前端技术栈和最佳实践开发，具备良好的性能和用户体验。

## 🎯 主要功能
- 基于AI的智能问答交互
- 完整的用户认证系统
- 会话历史记录与管理
- 响应式界面设计
- 流畅的打字机效果回复
- 容器化部署支持

## 🛠️ 技术栈详解

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 核心框架 | Next.js | 15.5.3 | React框架的服务端渲染解决方案 |
| 前端视图 | React | 19.1.0 | UI组件库和前端视图框架 |
| 编程语言 | TypeScript | 5.x | 提供类型安全的JavaScript超集 |
| 样式框架 | Tailwind CSS | 4.x | 实用优先的CSS框架 |
| HTTP客户端 | Axios | 1.12.2 | API请求处理 |
| 通知系统 | react-hot-toast | 2.6.0 | 用户界面通知组件 |
| AI集成 | @ai-sdk/google | 2.0.14 | Google AI服务SDK集成 |
| AI集成 | @google/generative-ai | 0.24.1 | Google生成式AI SDK |
| AI集成 | ai | 5.0.47 | AI应用构建工具库 |
| 图标库 | lucide-react | 0.544.0 | 矢量图标库 |
| 构建工具 | Turbopack | - | Next.js的高性能构建工具 |
| 容器化 | Docker | - | 应用容器化部署 |

## 功能特性

### 用户认证系统
- ✅ 用户注册（用户名、昵称、角色设置）
- ✅ 用户登录（基于JWT认证）
- ✅ 会话管理与路由保护
- ✅ 昵称修改功能

### AI聊天功能
- ✅ 智能问答交互界面
- ✅ 模拟流式输出效果（打字机效果）
- ✅ 支持创建新会话
- ✅ 历史会话加载
- ✅ 用户友好的消息显示

### 用户体验优化
- ✅ 响应式设计，适配多种设备
- ✅ 加载状态与错误提示
- ✅ 消息发送与接收反馈
- ✅ 平滑过渡动画

## 快速开始

### 前提条件
- Node.js 18+ 环境
- npm 或 yarn 包管理器
- 已配置的后端服务

### 安装依赖
```bash
npm install
# 或
yarn install
```

### 开发环境运行
```bash
npm run dev
# 或
npx next dev --turbopack
```
项目将在 http://localhost:3000 启动开发服务器

### 构建生产版本
```bash
npm run build
# 或
npx next build --turbopack
```

### 运行生产版本
```bash
npm start
```

## 📁 项目结构详解
采用Next.js 13+的App Router架构，项目结构清晰，职责分明：

```
├── app/               # 应用主目录，基于App Router架构
│   ├── api/           # API路由处理（Next.js Server Actions）
│   │   ├── auth/      # 认证相关API
│   │   ├── chat/      # 聊天会话API (创建、删除会话)
│   │   └── users/     # 用户管理API
│   ├── chat/          # 聊天功能页面，核心交互界面
│   ├── commons/       # 共享组件和工具
│   │   ├── axios.jsx  # 标准API请求实例
│   │   └── axiosAI.jsx # AI服务专用请求实例
│   ├── login/         # 用户登录页面
│   ├── register/      # 用户注册页面
│   └── page.tsx       # 首页（默认Next.js模板）
├── public/            # 静态资源文件
├── types/             # TypeScript类型定义
│   └── ai.d.ts        # AI相关类型定义
├── utils/             # 工具函数
├── middleware.ts      # 中间件，处理路由保护和认证
├── next.config.ts     # Next.js配置文件
└── tailwind.config.js # Tailwind CSS配置文件
```

## 👨‍💻 开发流程

### 代码贡献
1. 克隆项目代码
2. 创建新的功能分支
3. 开发功能并提交代码
4. 运行 lint 检查代码规范
5. 提交Pull Request

### 开发建议
- 使用TypeScript进行类型安全开发
- 遵循React Hooks最佳实践
- 使用Tailwind CSS utility classes进行样式开发
- 组件化开发，提高代码复用性
- 合理使用Next.js的Server Components和Client Components

## ⚡ 性能优化
- 使用Next.js的静态生成和服务端渲染
- 组件懒加载和代码分割
- 优化图片加载（使用Next.js Image组件）
- 使用Turbopack提升构建性能
- 合理缓存API请求结果

## API接口说明

### 用户认证接口
- **登录**: POST /api/user/login
- **注册**: POST /api/user/register
- **更新昵称**: POST /api/user/updateNickName

### 聊天会话接口
- **创建新会话**: POST /api/qa/sessions
- **删除会话**: DELETE /api/qa/sessions
- **保存问答**: POST /api/qa/save
- **加载历史**: GET /api/qa/history

## 🔧 环境配置

### 环境变量
项目支持以下环境变量配置：
- `BACKEND_QA_API_URL`: 后端QA服务API地址 (默认: http://localhost:8080)
- `AI_SERVICE_URL`: AI服务API地址 (配置在axiosAI.jsx中，默认: http://localhost:8082)

### API配置文件
项目使用两个Axios实例分别连接不同的后端服务：
- `app/commons/axios.jsx`: 用于标准用户认证和管理API
- `app/commons/axiosAI.jsx`: 专门用于AI问答服务API

## 📸 项目截图

### 登录界面
![登录界面](screenshots/login.png) *登录页面截图（待添加）*

### 聊天界面
![聊天界面](screenshots/chat.png) *聊天界面截图（待添加）*

### 注册界面
![注册界面](screenshots/register.png) *注册页面截图（待添加）*

## 🚀 部署指南

### Docker容器化部署
项目包含完整的Docker支持，可通过以下步骤快速部署：

```bash
# 构建Docker镜像
docker build -t ai-qa-frontend .

# 运行Docker容器
docker run -p 3000:3000 -d ai-qa-frontend

# 或使用环境变量配置
docker run -p 3000:3000 -e BACKEND_QA_API_URL=http://your-backend-api -d ai-qa-frontend
```

### Nginx代理配置
项目提供了nginx.conf配置文件，适用于生产环境部署：

1. 先构建生产版本：`npm run build`
2. 配置nginx.conf中的路径和代理设置
3. 启动nginx服务

### Vercel部署
作为Next.js项目，也可以直接部署到Vercel：

1. 将代码推送到GitHub/GitLab等代码仓库
2. 在Vercel上导入项目
3. 配置环境变量
4. 部署并分配域名

## 🔒 安全说明
- 所有API请求使用JWT令牌进行身份验证
- 敏感信息存储在localStorage中（实际生产环境建议使用更安全的存储方式）
- 401错误自动重定向到登录页面
- 密码传输采用加密方式（依赖后端实现）

## 🎯 未来功能展望
- [ ] 文件上传与AI分析功能
- [ ] 多模态交互（支持图片、语音输入）
- [ ] 主题切换功能（明/暗模式）
- [ ] 聊天历史搜索功能
- [ ] 会话共享与协作功能
- [ ] AI回答质量评分系统
- [ ] 个性化AI助手设置

## ❓ 常见问题解答

### 登录与注册
**Q: 登录失败怎么办？**
- A: 检查用户名和密码是否正确
- 确认后端服务是否正常运行
- 清除浏览器缓存后重试

**Q: 注册时提示"用户已存在"**
- A: 尝试使用其他用户名注册
- 或联系管理员重置账户

### AI功能
**Q: AI回复不显示或显示异常**
- A: 确认AI服务地址配置是否正确
- 检查用户token是否有效
- 查看浏览器控制台是否有错误信息
- 确认后端服务是否正常响应

**Q: 如何创建新的聊天会话？**
- A: 在聊天界面点击"新建会话"按钮
- 系统会自动创建新的会话并清空当前聊天记录

### 部署与开发
**Q: 部署后无法连接到后端服务**
- A: 检查环境变量配置是否正确
- 确认后端服务IP和端口是否可访问
- 查看网络防火墙设置

**Q: 开发环境下如何调试API请求？**
- A: 使用浏览器开发者工具的Network面板
- 查看控制台输出的请求日志
- 确认请求URL和参数是否正确

## 📄 许可证
本项目采用MIT许可证开源。

## 📬 联系方式
如有问题、建议或合作意向，请联系项目维护团队：
- 邮件：contact@ai-qa-system.com
- GitHub：[https://github.com/ai-qa-system/frontend](https://github.com/ai-qa-system/frontend) (示例链接)

## 🙏 鸣谢
- 感谢Next.js团队提供的优秀框架
- 感谢Google AI团队提供的强大AI能力
- 感谢所有为项目做出贡献的开发者

---
*AI智能问答系统 - 让知识触手可及*