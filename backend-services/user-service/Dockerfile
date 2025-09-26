
# 多阶段构建，仅构建 user-service 服务
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY ../pom.xml ./pom.xml
COPY user-service/pom.xml ./user-service/pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY qa-service/pom.xml ./qa-service/pom.xml
COPY user-service/src ./user-service/src
RUN mvn clean package -pl user-service -am -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/user-service/target/*SNAPSHOT.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "-Dmanagement.metrics.enabled=false", "-Dmanagement.endpoints.web.exposure.include=health", "app.jar"]
