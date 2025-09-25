# ----------- 构建阶段 -----------
FROM maven:3.9-eclipse-temurin-17 AS builder
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /build

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY qa-service/pom.xml         qa-service/
COPY qa-service/src             qa-service/src
RUN mvn -pl qa-service clean package -DskipTests

# ----------- 运行阶段 -----------
FROM eclipse-temurin:17-jre-ubi10-minimal
WORKDIR /app
RUN microdnf update -y && microdnf install -y curl && microdnf clean all
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN groupadd -r appuser && useradd -r -g appuser appuser

COPY --from=builder /build/qa-service/target/qa-service-*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

EXPOSE 8082
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1