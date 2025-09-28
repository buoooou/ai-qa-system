# 使用多阶段构建减少镜像体积
# FROM maven:3.8.7-openjdk-17 AS build
FROM docker.io/library/maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
# 显式复制所有子模块
COPY user-service /app/user-service
COPY qa-service /app/qa-service
COPY api-gateway /app/api-gateway
RUN mvn dependency:go-offline  # 提前下载依赖（含 Lombok）
COPY . .
RUN mvn clean package -DskipTests

# 运行时镜像
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# 根据实际服务端口调整
EXPOSE 8080 8081 8082  
CMD ["java", "-jar", "app.jar"]
