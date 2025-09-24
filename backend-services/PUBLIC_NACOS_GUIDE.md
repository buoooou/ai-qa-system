# 阿里云公网Nacos配置指南

## 1. 阿里云微服务引擎MSE（推荐）

阿里云提供免费的Nacos服务，每个月有一定的免费额度：

### 免费额度：
- 2个节点免费使用
- 每月免费调用次数：100万次
- 适合开发和测试环境

### 使用步骤：
1. 注册阿里云账号：https://www.aliyun.com/
2. 开通微服务引擎MSE：https://mse.console.aliyun.com/
3. 创建Nacos实例（选择免费规格）
4. 获取访问地址和认证信息

### 配置示例：
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: your-mse-nacos-address:8848
        namespace: your-namespace-id
        username: nacos
        password: your-password
```

## 2. 华为云ServiceStage

华为云也提供免费的微服务治理平台：

### 免费额度：
- 每月免费调用次数：50万次
- 免费存储空间：1GB

### 访问地址：https://console.huaweicloud.com/servicestage/

## 3. 腾讯云TSF

腾讯云微服务平台也有免费额度：

### 免费额度：
- 轻量版免费使用
- 适合小规模应用

### 访问地址：https://console.cloud.tencent.com/tsf

## 4. 公共测试Nacos服务

⚠️ 注意：仅用于学习和测试，不要用于生产环境！

### 测试地址：
- 服务地址：nacos.io:8848
- 用户名：nacos
- 密码：nacos
