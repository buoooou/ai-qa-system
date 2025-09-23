# 使用官方MySQL 8.0镜像作为基础镜像
FROM mysql:8.0

# 设置环境变量
# MYSQL_ROOT_PASSWORD: MySQL root用户的密码
# MYSQL_DATABASE: 创建的默认数据库名称
# MYSQL_USER: 创建的普通用户名
# MYSQL_PASSWORD: 普通用户的密码
ENV MYSQL_ROOT_PASSWORD=ai_qa_system
ENV MYSQL_DATABASE=ai_qa_system
ENV MYSQL_USER=ai_qa_user
ENV MYSQL_PASSWORD=ai_qa_system

# 设置MySQL配置
# 允许远程连接，设置字符集为utf8mb4
ENV MYSQL_CHARSET=utf8mb4
ENV MYSQL_COLLATION=utf8mb4_unicode_ci

# 复制初始化脚本到容器中
# MySQL容器会自动执行/docker-entrypoint-initdb.d/目录下的.sql文件
COPY ./init-scripts/ /docker-entrypoint-initdb.d/

# 复制自定义MySQL配置文件
COPY ./my.cnf /etc/mysql/conf.d/

# 暴露MySQL默认端口
EXPOSE 3306

# 设置时区为上海时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 容器启动时的默认命令（继承自基础镜像）
CMD ["mysqld"]