# === Build stage ===
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /workspace

COPY . .

RUN mvn -pl user-service -am -B clean package -DskipTests

# === Runtime stage ===
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /workspace/user-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

