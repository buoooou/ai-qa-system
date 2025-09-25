# ----------- 构建阶段 -----------
FROM maven:3.9-eclipse-temurin-17 AS builder
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /build

# 复制总 pom + 本模块源码
COPY pom.xml .
# 2. 所有子模块“壳子”：只拷 pom.xml，让 Maven 校验通过
COPY user-service/pom.xml user-service/
COPY qa-service/pom.xml   qa-service/

COPY api-gateway/pom.xml        api-gateway/
COPY api-gateway/src            api-gateway/src
# 如有依赖其它内部模块，继续 COPY 即可
RUN mvn -pl api-gateway clean package -DskipTests

# ----------- 运行阶段 -----------
FROM eclipse-temurin:17-jre-ubi10-minimal
WORKDIR /app
RUN microdnf update -y && microdnf install -y curl && microdnf clean all
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 非 root 用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

COPY --from=builder /build/api-gateway/target/api-gateway-*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1