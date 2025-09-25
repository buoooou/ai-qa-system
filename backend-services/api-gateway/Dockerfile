# 使用Eclipse Temurin OpenJDK 17作为基础镜像（更稳定的官方镜像）
FROM eclipse-temurin:17-jdk-ubi10-minimal

# 设置工作目录
WORKDIR /app

# 更新包索引并安装curl用于健康检查
RUN microdnf update -y && microdnf install -y curl && microdnf clean all

# 设置时区为上海时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone

# 复制 Maven 配置文件和源代码
COPY pom.xml ./
COPY src ./src

# 构建应用
RUN microdnf install -y maven && \
    mvn clean package -DskipTests && \
    microdnf remove -y maven && \
    microdnf clean all

# 创建应用用户（安全最佳实践）
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 复制Maven构建的JAR文件到容器中
# 注意：这里假设Maven构建后的JAR文件在target目录下
COPY target/api-gateway-*.jar app.jar

# 更改文件所有者
RUN chown appuser:appuser app.jar

# 切换到应用用户
USER appuser

# 暴露应用端口（API Gateway默认端口8080）
EXPOSE 8080

# 设置JVM参数和启动命令
# -Xms256m -Xmx512m: 设置堆内存大小
# -Djava.security.egd=file:/dev/./urandom: 加速随机数生成
# -Dspring.profiles.active=prod: 激活生产环境配置
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx512m", \
    "-Djava.security.egd=file:/dev/./urandom", \

    "-jar", \
    "app.jar"]

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1