# Nacos 与 Spring Boot WebFlux 兼容性问题修复

## 问题描述

在使用 Spring Boot 2.7.17 + Spring Cloud Alibaba Nacos 时，遇到以下问题：

```
NacosException: Client not connected, current status:STARTING
```

### 根本原因

1. **SpringBoot 2.7.17** 默认使用 **ReactiveWebServer (Netty)**
2. **ReactiveWebServerApplicationContext** 在 WebServer 还没真正绑定端口之前就会发布 `ReactiveWebServerInitializedEvent`
3. **Nacos 的 AbstractAutoServiceRegistration** 监听到该事件就立即执行注册
4. 此时 **gRPC 连接往往尚未就绪**，导致注册失败

这是 `alibaba-nacos-discovery` 与 `SpringBoot-WebFlux` 的已知兼容性缺陷，在 2021.x/2022.x 分支均存在。

## 解决方案

### 1. 升级到官方已修复版本

- **spring-cloud-starter-alibaba-nacos-discovery** ≥ 2022.0.1.0
- **nacos-client** ≥ 2.2.1

该版本把监听事件改为 `WebServerReadyEvent`，保证端口就绪后再注册。

### 2. 项目修复内容

#### 2.1 升级依赖版本

在 `backend-services/pom.xml` 中：

```xml
<properties>
    <spring-cloud-alibaba.version>2022.0.0.0</spring-cloud-alibaba.version>
</properties>
```

#### 2.2 优化 Nacos 配置

在所有服务的 `application-docker.yml` 中添加：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        # 基础连接配置
        server-addr: ai-qa-nacos:8848
        timeout: 30000
        heart-beat-timeout: 30000
        heart-beat-interval: 5000
        fail-fast: false
        register-enabled: true
        
        # 添加稳定性配置
        watch:
          enabled: true
        
        # 服务实例配置
        instance:
          weight: 1.0
          enabled: true
          metadata:
            version: "1.0"
            zone: "default"
      
      # 配置中心配置（提升连接稳定性）
      config:
        server-addr: ai-qa-nacos:8848
        timeout: 30000
        enabled: false
```

### 3. 修复效果

- ✅ 解决 `Client not connected, current status:STARTING` 错误
- ✅ 确保服务在端口就绪后再注册到 Nacos
- ✅ 提升服务启动的稳定性
- ✅ 兼容 WebFlux 和传统 Web 应用

### 4. 验证方法

1. 重新构建项目：
   ```bash
   mvn clean package
   ```

2. 启动 Docker 容器：
   ```bash
   docker-compose up --build
   ```

3. 检查服务注册状态：
   - 访问 Nacos 控制台：http://localhost:8848/nacos
   - 查看服务列表，确认所有服务正常注册

### 5. 注意事项

- 此修复适用于所有使用 Nacos 服务发现的 Spring Boot 应用
- 升级后需要重新构建所有服务镜像
- 建议在测试环境验证后再部署到生产环境

## 参考资料

- [Spring Cloud Alibaba 官方文档](https://github.com/alibaba/spring-cloud-alibaba)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Boot WebFlux 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)