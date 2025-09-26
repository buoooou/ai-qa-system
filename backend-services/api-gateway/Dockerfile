
# 多阶段构建，仅构建 api-gateway 服务
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY ../pom.xml ./pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY user-service/pom.xml ./user-service/pom.xml
COPY qa-service/pom.xml ./qa-service/pom.xml
COPY api-gateway/src ./api-gateway/src
RUN mvn clean package -pl api-gateway -am -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/api-gateway/target/*SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
