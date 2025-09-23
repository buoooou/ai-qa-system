# 第一阶段：构建JAR包（使用Maven镜像）
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# 复制pom.xml和源代码
COPY pom.xml .
COPY src ./src

# 编译打包（跳过测试加速构建）
RUN mvn clean package -DskipTests

# 第二阶段：运行JAR包（使用轻量JRE镜像）
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 从构建阶段复制生成的JAR包
COPY --from=builder /app/target/*.jar app.jar

# 暴露服务端口（根据实际配置修改，如user-service是8081）
EXPOSE 8081

# 启动命令（执行JAR包）
ENTRYPOINT ["java", "-jar", "app.jar"]