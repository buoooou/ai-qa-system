# 使用Eclipse Temurin OpenJDK 17作为基础镜像（更稳定的官方镜像）
FROM eclipse-temurin:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 安装curl用于健康检查
RUN apk add --no-cache curl

# 设置时区为上海时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone

# 创建应用用户（安全最佳实践）
RUN addgroup -S appuser && adduser -S -G appuser appuser

# 复制Maven构建的JAR文件到容器中
# 注意：这里假设Maven构建后的JAR文件在target目录下
COPY target/user-service-*.jar app.jar

# 更改文件所有者
RUN chown appuser:appuser app.jar

# 切换到应用用户
USER appuser

# 暴露应用端口（User Service默认端口8081）
EXPOSE 8081

# 设置JVM参数和启动命令
# -Xms256m -Xmx512m: 设置堆内存大小
# -Djava.security.egd=file:/dev/./urandom: 加速随机数生成
# Spring profile通过环境变量SPRING_PROFILES_ACTIVE设置
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx512m", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1