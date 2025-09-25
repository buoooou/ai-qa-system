# 使用多阶段构建减少镜像体积
FROM docker.io/library/maven:3.8-openjdk-17 AS build
WORKDIR /app

# 复制父级pom.xml
COPY pom.xml .
COPY user-service/pom.xml ./user-service/
COPY qa-service/pom.xml ./qa-service/
COPY api-gateway/pom.xml ./api-gateway/

# 下载依赖
RUN mvn dependency:go-offline -pl api-gateway -am

# 复制源代码
COPY user-service ./user-service
COPY qa-service ./qa-service
COPY api-gateway ./api-gateway

# 构建api-gateway
RUN mvn clean package -pl api-gateway -am -DskipTests

# 运行时镜像
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
