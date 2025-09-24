FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/*.jar app.jar

RUN groupadd -r spring && useradd -r -g spring spring
RUN chown -R spring:spring /app
USER spring

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]