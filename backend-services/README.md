
# 课程内容:
https://kuo.postions.app/ai-chatbot-img

# nacos 注册中心
https://nacos.io/docs/latest/overview/

# 本作业使用的 nacos 服务器，可确认服务
nacos服务器：
http://54.219.180.170:8080/#/login

apikey 申请网站：
https://aistudio.google.com/apikey




### 关于微服务的分层架构设计

在微服务架构中，将目录划分为 api、application、common、domain、infrastructure 是一种典型的 分层架构设计（参考 DDD 领域驱动设计思想），这种划分方式有助于实现代码的高内聚、低耦合，提升可维护性和扩展性。是否采用这种结构，取决于团队规模、业务复杂度和技术规范，以下是具体分析：

# 1. 各层的核心职责
这种划分方式本质是按 “职责边界” 分层，各层作用如下：

目录	核心职责	典型内容
api	对外暴露的接口层，负责请求 / 响应处理、参数校验、协议转换（如 HTTP/grpc）。	控制器（Controller）、DTO（数据传输对象）、API 文档配置、全局异常处理等。
application	应用服务层，协调领域层完成业务流程，不包含核心业务逻辑。	服务实现类（Service）、事务管理、跨领域对象协作等。
domain	领域层，核心业务逻辑和领域模型所在，是微服务的 “灵魂”。	领域模型（Entity）、领域服务（Domain Service）、仓储接口（Repository 接口）等。
infrastructure	基础设施层，提供技术能力支撑，实现领域层的接口。	数据库访问实现（Repository 实现）、缓存、消息队列、第三方服务集成等。
common	公共资源层，存放全服务共享的工具类、常量、通用配置等。	工具类（Utils）、枚举（Enum）、通用异常、全局配置等。

# 2. 为什么推荐这种划分？
职责清晰：每层专注于自己的职责，避免业务逻辑与技术细节混杂（如 domain 层不依赖数据库框架，infrastructure 层不包含业务规则）。
可测试性：通过接口抽象（如 domain 层定义 Repository 接口，infrastructure 层实现），便于单元测试（如用 Mock 替代真实数据库）。
适应业务变化：核心业务逻辑集中在 domain 层，当技术实现（如数据库从 MySQL 换成 PostgreSQL）变化时，只需修改 infrastructure 层，不影响业务逻辑。
团队协作友好：不同角色可分工开发（如开发人员专注 domain 层业务，架构师设计 infrastructure 层技术方案）。

# 3. 注意事项
并非强制标准：如果是简单微服务（如仅提供几个 CRUD 接口），不必过度设计，可简化为 controller、service、repository、entity 等基础结构。
避免过度分层：各层之间应通过接口交互，避免跨层依赖（如 api 层直接调用 infrastructure 层），否则会破坏分层意义。
结合框架特性：Spring Boot 等框架对分层没有严格限制，可灵活调整（如将 application 层和 domain 层合并为 service 层，适合小型服务）。

# 4. 目录结构示例
plaintext
user-service/
├── src/main/java/com/ai/qa/user/
│   ├── api/                  // 接口层
│   │   ├── controller/       // 控制器
│   │   ├── dto/              // 数据传输对象
│   │   └── exception/        // API 层异常处理
│   ├── application/          // 应用服务层
│   │   ├── UserService.java  // 应用服务实现
│   │   └── command/          // 命令对象（如注册用户的参数封装）
│   ├── domain/               // 领域层
│   │   ├── model/            // 领域模型（User 实体）
│   │   ├── service/          // 领域服务（如用户权限校验逻辑）
│   │   └── repo/             // 仓储接口（UserRepository 接口）
│   ├── infrastructure/       // 基础设施层
│   │   ├── persistence/      // 数据库实现（UserRepositoryImpl）
│   │   ├── cache/            // 缓存配置
│   │   └── security/         // 安全相关实现（如 UserDetailsService）
│   └── common/               // 公共资源
│       ├── utils/            // 工具类
│       └── constant/         // 常量定义
└── src/main/resources/       // 配置文件

# 总结
如果你的微服务 业务逻辑复杂、团队规模较大、需要长期维护，推荐采用这种分层结构（尤其是引入 DDD 思想时）；如果服务简单，可适当简化，避免为了 “分层” 而增加不必要的复杂度。核心原则是：让代码的组织方式符合业务逻辑，便于理解和维护。
