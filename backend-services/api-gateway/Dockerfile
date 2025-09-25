# 第一阶段：构建JAR包（使用Maven镜像）
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# 复制整个后端服务目录（包含父POM和所有子模块）
COPY ../pom.xml /app/pom.xml
COPY ../api-gateway /app/api-gateway

# 进入当前服务目录，基于父POM构建
WORKDIR /app/api-gateway
RUN mvn -B clean package -DskipTests


# 第二阶段：运行JAR包（使用轻量JRE镜像）
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 从构建阶段复制生成的JAR包
COPY --from=builder /app/api-gateway/target/*.jar app.jar

# 暴露服务端口（api-gateway使用8080）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
    