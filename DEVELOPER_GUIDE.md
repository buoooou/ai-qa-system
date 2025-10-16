# AI QA System - 开发者指南

## 快速导航

- [环境搭建](#一、环境搭建)
- [代码规范](#二、代码规范)
- [开发流程](#三、开发流程)
- [API 文档](#四、api-文档)
- [调试技巧](#五、调试技巧)
- [常见问题](#六、常见问题)

## 一、环境搭建

### 1.1 前置要求

- **Java**: JDK 17 或更高版本
- **Node.js**: 18.x 或更高版本
- **Docker**: 20.10 或更高版本
- **IDE**: IntelliJ IDEA 或 VS Code

### 1.2 开发工具安装

#### IntelliJ IDEA 配置

```xml
<!-- .idea/misc.xml -->
<component name="ProjectRootManager" version="2" languageLevel="JDK_17" project-jdk-name="17">
  <output url="file://$PROJECT_DIR$/out" />
</component>
```

插件推荐：
- Lombok Plugin
- Spring Boot Helper
- Docker Plugin
- .env files support

#### VS Code 配置

```json
// .vscode/settings.json
{
  "java.home": "/path/to/jdk-17",
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.debug.settings.onBuildFailureProcess": "auto",
  "spring-boot.ls.checkJVM": false,
  "docker.docker.defaultStackPath": "docker-compose.yml"
}
```

推荐扩展：
- Extension Pack for Java
- Spring Boot Extension Pack
- Docker
- ESLint
- Prettier

### 1.3 克隆并配置项目

```bash
# 克隆项目
git clone https://github.com/<your-org>/ai-qa-system.git
cd ai-qa-system

# 后端编译
cd backend-services
mvn clean compile

# 前端安装依赖
cd ../ai-chatbot
pnpm install
```

### 1.4 启动开发环境

#### 方式一：使用 Docker Compose（推荐）

```bash
# 启动所有依赖服务（MySQL、Nacos）
docker compose up -d mysql nacos

# 启动后端服务
cd backend-services
mvn spring-boot:run -pl user-service &
mvn spring-boot:run -pl qa-service &
mvn spring-boot:run -pl api-gateway &

# 启动前端
cd ../ai-chatbot
cp .env.local.example .env.local
# 编辑 .env.local，配置环境变量
pnpm dev
```

#### 方式二：本地运行

```bash
# 1. 启动 MySQL（本地）
brew services start mysql  # macOS
sudo systemctl start mysql  # Linux

# 2. 启动 Nacos
cd nacos
sh startup.sh -m standalone

# 3. 运行后端服务
cd backend-services
mvn spring-boot:run

# 4. 运行前端
cd ai-chatbot
pnpm dev
```

### 1.5 验证环境

访问以下地址确保服务正常：

- 前端: http://localhost:3000
- API Gateway: http://localhost:8083/swagger-ui.html
- Nacos: http://localhost:8848/nacos

## 二、代码规范

### 2.1 后端代码规范

#### 命名规范

```java
// 类名：大驼峰
public class UserService {}

// 方法名：小驼峰
public User getUserById(Long id) {}

// 常量：全大写，下划线分隔
public static final String DEFAULT_ROLE = "USER";

// 包名：全小写
package com.example.aiqa.service;

// 变量名：小驼峰
private String userName;
```

#### 代码示例

```java
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDTO registerUser(RegisterRequest request) {
        // 参数校验
        validateRegisterRequest(request);

        // 检查用户是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        // 创建用户
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .nickname(request.getNickname())
            .role(UserRole.USER)
            .createdAt(LocalDateTime.now())
            .build();

        // 保存用户
        user = userRepository.save(user);

        // 返回 DTO
        return UserMapper.toDTO(user);
    }
}
```

#### 异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException e) {
        ErrorResponse error = ErrorResponse.builder()
            .code("USER_EXISTS")
            .message(e.getMessage())
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error", e);
        ErrorResponse error = ErrorResponse.builder()
            .code("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### 2.2 前端代码规范

#### TypeScript 类型定义

```typescript
// types/user.ts
export interface User {
  id: string;
  username: string;
  email: string | null;
  nickname?: string | null;
  role: string;
  type: UserType;
  accessToken?: string;
}

export type UserType = "guest" | "regular";

// types/api.ts
export interface ApiResponse<T> {
  data: T;
  code: number;
  message: string;
}
```

#### React 组件规范

```typescript
// components/chat/Message.tsx
import React from "react";
import type { Message as MessageType } from "@/lib/types";
import { cn } from "@/lib/utils";

interface MessageProps {
  message: MessageType;
  isLoading?: boolean;
}

export const Message: React.FC<MessageProps> = ({
  message,
  isLoading = false
}) => {
  const isUser = message.role === "user";

  return (
    <div
      className={cn(
        "flex gap-3 p-4",
        isUser ? "justify-end" : "justify-start"
      )}
    >
      <div
        className={cn(
          "max-w-[80%] rounded-lg px-4 py-2",
          isUser
            ? "bg-primary text-primary-foreground"
            : "bg-muted"
        )}
      >
        {isLoading ? (
          <div className="flex items-center gap-2">
            <div className="h-4 w-4 animate-bounce rounded-full bg-current" />
            <div className="h-4 w-4 animate-bounce rounded-full bg-current [animation-delay:0.1s]" />
            <div className="h-4 w-4 animate-bounce rounded-full bg-current [animation-delay:0.2s]" />
          </div>
        ) : (
          <p className="text-sm">{message.content}</p>
        )}
      </div>
    </div>
  );
};
```

#### API 调用规范

```typescript
// lib/api/gateway.ts
import axios from "axios";
import type { User, ChatMessage } from "@/lib/types";

const gatewayClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_GATEWAY_URL,
  timeout: 60_000,
});

export const loginUser = async (
  usernameOrEmail: string,
  password: string
): Promise<{ user: User; token: string }> => {
  try {
    const response = await gatewayClient.post("/api/gateway/auth/login", {
      usernameOrEmail,
      password,
    });
    return response.data.data;
  } catch (error) {
    console.error("Login failed:", error);
    throw new Error("Invalid credentials");
  }
};

export const sendMessage = async (
  message: string,
  sessionId: string,
  accessToken: string
): Promise<ReadableStream> => {
  const response = await gatewayClient.post(
    "/api/gateway/qa/chat",
    {
      message: { content: message },
      sessionId,
    },
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      responseType: "stream",
    }
  );
  return response.data;
};
```

### 2.3 Git 提交规范

```bash
# 提交格式
<type>(<scope>): <subject>

<body>

<footer>

# 类型
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式（不影响功能）
refactor: 重构
test: 测试
chore: 构建/工具相关

# 示例
feat(auth): add JWT refresh mechanism

- Implement automatic token refresh
- Add refresh token storage
- Update authentication middleware

Closes #123
```

## 三、开发流程

### 3.1 功能开发流程

1. **创建功能分支**
   ```bash
   git checkout -b feature/user-profile
   ```

2. **编写代码**
   - 遵循代码规范
   - 添加必要的注释
   - 编写单元测试

3. **本地测试**
   ```bash
   # 后端
   mvn test

   # 前端
   pnpm lint
   pnpm type-check
   ```

4. **提交代码**
   ```bash
   git add .
   git commit -m "feat(profile): add user profile page"
   git push origin feature/user-profile
   ```

5. **创建 Pull Request**
   - 填写 PR 模板
   - 等待 Code Review
   - 根据反馈修改

6. **合并代码**
   - 通过 CI/CD 检查
   - 合并到 main 分支

### 3.2 调试技巧

#### 后端调试

1. **日志调试**
   ```java
   @Slf4j
   @Service
   public class UserService {
       public User getUser(Long id) {
           log.debug("Getting user with id: {}", id);
           User user = userRepository.findById(id)
               .orElseThrow(() -> new UserNotFoundException(id));
           log.info("Found user: {}", user.getUsername());
           return user;
       }
   }
   ```

2. **断点调试**
   - 在 IDE 中设置断点
   - 使用 Debug 模式启动
   - 查看变量值和调用栈

3. **测试 API**
   ```bash
   # 使用 curl 测试
   curl -X POST http://localhost:8083/api/gateway/auth/login \
     -H "Content-Type: application/json" \
     -d '{"usernameOrEmail":"demo","password":"password"}'

   # 使用 httpie
   http POST localhost:8083/api/gateway/auth/login \
     usernameOrEmail=demo password=password
   ```

#### 前端调试

1. **React DevTools**
   - 安装浏览器扩展
   - 查看组件树和状态
   - 性能分析

2. **网络调试**
   ```typescript
   // 添加请求拦截器
   gatewayClient.interceptors.request.use(
     (config) => {
       console.log("Request:", config);
       return config;
     },
     (error) => {
       console.error("Request error:", error);
       return Promise.reject(error);
     }
   );
   ```

3. **错误边界**
   ```typescript
   // components/ErrorBoundary.tsx
   export class ErrorBoundary extends Component<
     PropsWithChildren,
     { hasError: boolean; error?: Error }
   > {
     constructor(props: PropsWithChildren) {
       super(props);
       this.state = { hasError: false };
     }

     static getDerivedStateFromError(error: Error) {
       return { hasError: true, error };
     }

     componentDidCatch(error: Error, errorInfo: ErrorInfo) {
       console.error("Error caught by boundary:", error, errorInfo);
     }

     render() {
       if (this.state.hasError) {
         return (
           <div className="p-4 border border-red-500 rounded">
             <h2>Something went wrong</h2>
             <details>
               {this.state.error?.message}
             </details>
           </div>
         );
       }

       return this.props.children;
     }
   }
   ```

## 四、API 文档

### 4.1 认证 API

#### 用户登录
```http
POST /api/gateway/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "demo",
  "password": "password"
}
```

响应：
```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "1",
      "username": "demo",
      "email": "demo@example.com",
      "nickname": "Demo User",
      "role": "USER",
      "type": "regular"
    },
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### 用户注册
```http
POST /api/gateway/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "nickname": "New User"
}
```

### 4.2 聊天 API

#### 发送消息
```http
POST /api/gateway/qa/chat
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": {
    "content": "Hello, AI!"
  },
  "sessionId": "session-123"
}
```

#### 获取聊天历史
```http
GET /api/gateway/qa/chat/session-123/history
Authorization: Bearer <token>
```

### 4.3 错误响应

```json
{
  "code": 401,
  "message": "Unauthorized",
  "error": "INVALID_TOKEN"
}
```

## 五、调试技巧

### 5.1 常用调试命令

```bash
# 查看容器日志
docker compose logs -f [service-name]

# 进入容器
docker exec -it [container-name] bash

# 查看端口占用
lsof -i :8083

# 查看进程
ps aux | grep java

# 网络测试
telnet localhost 8083
```

### 5.2 IDE 调试配置

#### IntelliJ IDEA 远程调试

添加 JVM 参数：
```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

配置 Remote JVM Debug：
- Host: localhost
- Port: 5005

#### VS Code 调试配置

```json
// .vscode/launch.json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Debug Java",
      "type": "java",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005
    },
    {
      "name": "Debug Next.js",
      "type": "node",
      "request": "launch",
      "program": "${workspaceFolder}/ai-chatbot/node_modules/.bin/next",
      "args": ["dev"],
      "cwd": "${workspaceFolder}/ai-chatbot",
      "env": {
        "NODE_OPTIONS": "--inspect"
      }
    }
  ]
}
```

## 六、常见问题

### 6.1 后端问题

**Q: 启动时提示 "Connection refused"**
A: 检查依赖服务是否已启动（MySQL、Nacos）

**Q: JWT token 无效**
A: 检查 JWT_SECRET 配置是否一致

**Q: 数据库连接失败**
A: 验证数据库配置和网络连通性

### 6.2 前端问题

**Q: Next.js 编译错误**
A: 检查 TypeScript 类型错误

**Q: API 调用失败**
A: 检查 NEXT_PUBLIC_GATEWAY_URL 配置

**Q: 认证失败**
A: 检查 AUTH_SECRET 配置

### 6.3 Docker 问题

**Q: 容器启动失败**
A: 查看容器日志：`docker logs [container-name]`

**Q: 端口冲突**
A: 修改 docker-compose.yml 中的端口映射

**Q: 网络连接问题**
A: 检查 Docker 网络配置

## 七、性能优化建议

### 7.1 后端优化

1. **数据库优化**
   - 添加适当索引
   - 使用连接池
   - 避免 N+1 查询

2. **缓存策略**
   ```java
   @Cacheable(value = "users", key = "#id")
   public User getUser(Long id) {
       return userRepository.findById(id);
   }
   ```

3. **异步处理**
   ```java
   @Async
   public CompletableFuture<Void> processAsync() {
       // 异步处理逻辑
   }
   ```

### 7.2 前端优化

1. **代码分割**
   ```typescript
   // 动态导入组件
   const ChatPage = dynamic(() => import('./ChatPage'), {
     loading: () => <div>Loading...</div>
   });
   ```

2. **图片优化**
   ```typescript
   import Image from "next/image";

   <Image
     src="/avatar.jpg"
     alt="Avatar"
     width={40}
     height={40}
     priority
   />
   ```

3. **状态管理优化**
   - 使用 React.memo 避免不必要的重渲染
   - 使用 useMemo 和 useCallback 缓存计算结果

## 八、测试指南

### 8.1 后端测试

```java
// 单元测试
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUser() {
        // Given
        RegisterRequest request = new RegisterRequest(
            "testuser",
            "test@example.com",
            "password"
        );

        when(userRepository.existsByUsername("testuser"))
            .thenReturn(false);

        // When
        UserDTO result = userService.registerUser(request);

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }
}

// 集成测试
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldLoginSuccessfully() {
        // Given
        LoginRequest request = new LoginRequest("demo", "password");

        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/gateway/auth/login",
            request,
            ApiResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotNull();
    }
}
```

### 8.2 前端测试

```typescript
// 组件测试
import { render, screen, fireEvent } from '@testing-library/react';
import { Message } from './Message';

describe('Message', () => {
  it('should render user message', () => {
    const message = {
      id: '1',
      role: 'user' as const,
      content: 'Hello',
      createdAt: new Date(),
    };

    render(<Message message={message} />);

    expect(screen.getByText('Hello')).toBeInTheDocument();
  });

  it('should show loading indicator', () => {
    const message = {
      id: '1',
      role: 'assistant' as const,
      content: '',
      createdAt: new Date(),
    };

    render(<Message message={message} isLoading />);

    expect(screen.getByRole('status')).toBeInTheDocument();
  });
});

// API 测试
import { loginUser } from '@/lib/api/gateway';
import { mockAxios } from '__mocks__/axios';

mockAxios.post.mockResolvedValue({
  data: {
    data: {
      user: { id: '1', username: 'demo' },
      token: 'token123'
    }
  }
});

describe('loginUser', () => {
  it('should login successfully', async () => {
    const result = await loginUser('demo', 'password');

    expect(result.user.username).toBe('demo');
    expect(result.token).toBe('token123');
  });
});
```

## 九、贡献指南

### 9.1 Pull Request 流程

1. Fork 项目
2. 创建功能分支
3. 编写代码和测试
4. 提交 PR
5. 代码审查
6. 合并代码

### 9.2 代码审查要点

- [ ] 代码符合规范
- [ ] 有适当的测试
- [ ] 文档已更新
- [ ] 无安全漏洞
- [ ] 性能影响可接受

## 十、资源链接

- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Next.js 文档](https://nextjs.org/docs)
- [Docker 文档](https://docs.docker.com)
- [API 文档](http://localhost:8083/swagger-ui.html)

---

更新时间：2025-01-16