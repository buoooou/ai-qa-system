# === Build stage ===
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY qa-service/pom.xml ./qa-service/pom.xml
COPY qa-service/src ./qa-service/src

RUN mvn -pl qa-service -am -B clean package -DskipTests

# === Runtime stage ===
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /workspace/qa-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

