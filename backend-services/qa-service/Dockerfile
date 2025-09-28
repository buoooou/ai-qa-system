
# 多阶段构建，仅构建 qa-service 服务
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY backend-services/pom.xml ./pom.xml
COPY backend-services/qa-service/pom.xml ./qa-service/pom.xml
COPY backend-services/user-service/pom.xml ./user-service/pom.xml
COPY backend-services/api-gateway/pom.xml ./api-gateway/pom.xml
COPY backend-services/qa-service/src ./qa-service/src
RUN mvn clean package -pl qa-service -am -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/qa-service/target/*SNAPSHOT.jar app.jar
EXPOSE 8082
CMD ["java", "-jar", "-Dmanagement.metrics.enabled=false", "-Dmanagement.endpoints.web.exposure.include=health", "app.jar"]
