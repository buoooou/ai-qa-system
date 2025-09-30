# 使用官方Maven镜像作为基础镜像
FROM maven:3-ibm-semeru-17-focal AS build
 
# 将Maven项目的pom.xml和src代码复制到容器中
COPY src /home/app/src
COPY pom.xml /home/app
 
# 设置工作目录
WORKDIR /home/app
 
# 使用Maven打包应用
RUN mvn -f /home/app/pom.xml clean package
 
# 使用Java的基础镜像
FROM openjdk:17-jdk-slim
 
# 将构建好的jar从build阶段复制到这个新镜像中
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
 
# 设置工作目录
WORKDIR /usr/local/lib
 
# 暴露端口（例如8080）如果你的Spring Boot应用需要监听外部请求
EXPOSE 8081
 
# 运行jar文件
ENTRYPOINT ["java","-jar","app.jar"]