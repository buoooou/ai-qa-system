# 使用Eclipse Temurin OpenJDK 17作为基础镜像（更稳定的官方镜像）
FROM eclipse-temurin:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 安装curl和mysql客户端用于健康检查和数据库连接测试
RUN apk add --no-cache curl mysql-client

# 设置时区为上海时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone

# 创建应用用户（安全最佳实践）
RUN addgroup -S appuser && adduser -S -G appuser appuser

# 复制Maven构建的JAR文件到容器中
# 注意：这里假设Maven构建后的JAR文件在target目录下
COPY target/qa-service-*.jar app.jar

# 更改文件所有者
RUN chown appuser:appuser app.jar

# 切换到应用用户
USER appuser

# 暴露应用端口（QA服务默认端口8082）
EXPOSE 8082

# 设置JVM参数和启动命令
# -Xms256m -Xmx512m: 设置堆内存大小
# -Djava.security.egd=file:/dev/./urandom: 加速随机数生成
# -Dspring.profiles.active=docker: 激活docker环境配置
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["sh", "-c", "java -Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1