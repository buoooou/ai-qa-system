# CI/CD

参照：
https://full-stack.postions.app/github-cicd
https://java.postions.app/java-github-cicd
   

### 一、前期准备工作
### 1.1 确认网络设置 （参照 主目录下的 README.md 中的网络设置）
连接公司网络，打开公司代理，确保可以访问 GitHub、Docker Hub 和 AWS。

### 1.2 配置 GitHub 仓库
1. WIN11 中克隆代码仓库（如果尚未克隆）
git clone https://github.com/fm-t7/ai-qa-system.git
cd ai-qa-system

2. 确保本地分支与远程同步
git checkout main
git pull origin main

### 1.3 注册DockerHub账号
使用github 账号登录 https://www.docker.com/, 获得Personal access tokens。

### 1.4 配置 WSL 环境

# 配置 ubuntu 环境
1. 更新 ubuntu 系统
<!-- sudo apt update && sudo apt upgrade -y -->

2. 确保当前用户加入 docker 组（避免每次使用 sudo）
sudo usermod -aG docker $USER -->

# 登录AWS控制台
URL: https://shida-awscloud3.signin.aws.amazon.com/console

# 建立 EC2 实例
选择 EC2 实例 -> 启动实例 -> 选择 ubuntu -> 选择实例类型 t3 medium（4G cpu）
-> 选择密钥对（ai-qa-system-sfm.pem） -> 选择系统盘大小 30 GB, gp2 磁盘类型 (免费)  
-> 建立安全组 -> 启动实例

# 在 Ubuntu 上配置 AWS EC2 信任密钥

1. 使用 vs code 链接 Ubuntu, 自然切换为 ubuntu 账户(/home/ubuntu) 
(不需要使用 su - ubuntu，whoami, pwd)

2. Copy EC2的密码文件到/home/ubuntu
copy /mnt/c/ai-qa-system/ai-qa-system-sfm.pem /home/ubuntu

3. 设置属性 (# -rw-------)
chmod 600 ai-qa-system-sfm.pem

4. 确认属性
ls -ld ai-qa-system-sfm.pem         

5. 连接 EC2, 在authorized_keys下生成私钥
ssh -i ai-qa-system-sfm.pem ubuntu@3.26.56.14

6. 确认 ~/.ssh 目录权限 700(drwx------)
ls -ld ~/.ssh
drwx------ 2 ubuntu ubuntu 4096 Sep 24 01:34 /home/ubuntu/.ssh

7. 如果不是上面的结果，则需要在EC2上创建 .ssh 目录并设置权限（仅当前用户可读写执行）
mkdir -p ~/.ssh && chmod 700 ~/.ssh

8. 确认 authorized_keys 文件权限 600(-rw-------)
ubuntu@ip-172-31-9-235:~$ ls -ld ~/.ssh/authorized_keys
-rw------- 1 ubuntu ubuntu 398 Sep 24 01:34 /home/ubuntu/.ssh/authorized_keys

9. 如果不是上面结果，则需要创建 authorized_keys 文件并设置权限：
touch ~/.ssh/authorized_keys  # 创建文件
chmod 600 ~/.ssh/authorized_keys  # 权限必须为600，否则SSH会拒绝使用

10. 确认 authorized_keys 文件内容
cat ~/.ssh/authorized_keys

11. 生成无密码的SSH密钥对（github_actions_deploy_key 和 github_actions_deploy_key.pub）
ssh-keygen -t rsa -b 4096 -f github_actions_deploy_key -N ""

12. 确认生成的密钥对(增加了公钥)
ls -l github_actions_deploy_key*

13. 重新执行追加公钥的命令：
cat github_actions_deploy_key.pub >> ~/.ssh/authorized_keys

14. 再次确认 authorized_keys 文件内容
cat ~/.ssh/authorized_keys

15. 在客户端ubuntu上验证 SSH 连接
touch github_actions_deploy_key
chmod 600 ./github_actions_deploy_key
nano github_actions_deploy_key  # 输入私钥内容，Ctrl+X保存退出。
ssh -i github_actions_deploy_key ubuntu@3.26.56.14


### 二、CI/CD 流程设计

CI/CD 流程设计，包括：
1. 编辑CICD脚本
    docerfile, docker-compose.yml,.github/workflows/main.yml 文件
2. 配置 GitHub Secrets
3. Docker compose 部署
4. 提交代码触发 GitHub Actions
    自动执行测试（前端 + 后端）
    构建 Docker 镜像
    推送镜像到 AWS ECR
    部署到 AWS EC2
    #执行健康检查
5. 访问前端页面，确认部署成功（机能确认）

### 2.1 编辑CICD脚本
输入项目概要，交给AI生成脚本。内容如下：
为了构建chatbox的前后端项目ai-qa-system。
后端backend-services是java 17、spring boot 2.7.17、Maven的构建的3个微服务（api-gateway:8080, user-service:8081, qa-service:8082）。后端使用nacos来注册发现微服务，其IP和端口是3.101.113.38:8848。后端有代码健康检查和OpenAPI文档。
前端frontend-nextjs是nextjs构建的构建的前端网页，端口为3000(node --version: v22.18.0)。

现在要通过workflows实现CICD。
（代码推送到github，镜像推送到dockerhub，部署到AWS的EC2的ubuntu-24.04-amd64-server上）

关于镜像名字，固定，后端为：chatbox-各自的微服务名, 前端为chatbox-frontend。

目录结构：
ai-qa-system
├──backend-services 
│       ├──api-gateway
│       ├──user-service
│       ├──qa-service
├──frontend-nextjs

请按照最佳实践，帮我生成dockerfile(后端每个微服务一个dockerfile), docker-compose.yml，workflows的main.yml。


### 2.2 GitHub Actions 配置
在 GitHub 项目<ai-qa-system> 的Settings/Secrets and variables/Actions下，
按照AI提示，点击New repository secret分别添加以下 Secrets：
1. DOCKERHUB_USERNAME: Docker Hub 用户名
2. DOCKERHUB_TOKEN: Docker Hub 访问令牌
3. EC2_HOST: AWS EC2 实例IP
4. EC2_USERNAME: EC2 用户名 (通常是 ec2-user)
5. EC2_SSH_KEY: SSH 私钥

### 2.3 Docker compose 部署
# COPY docker-compose.yml 到ubuntu用户的目录下
cp docker-compose.yml /home/ubuntu/
# 设置600的权限，确保只有当前用户可读写
chmod 600 /home/ubuntu/docker-compose.yml
# 通过scp命令传输文件到EC2实例
scp -i github_actions_deploy_key docker-compose.yml ubuntu@3.26.56.14:/home/ubuntu/
# 登录EC2实例确认文件是否传输成功
ssh -i github_actions_deploy_key ubuntu@3.26.56.14

使用ubuntu的ubuntu用户登录到 EC2 实例。
在 EC2 实例的 /home/ubuntu 目录下，创建 docker-compose.yml 文件。
设置600的权限，确保只有当前用户可读写。
nano /home/ubuntu/docker-compose.yml
chmod 600 /home/ubuntu/docker-compose.yml

scp -i ./my-ec2-key.pem ./docker-compose.yml ubuntu@54.198.123.45:/home/ubuntu/
/home/ubuntu下，执行下面的命令，会把本地的docker-compose.yml文件传输到EC2的/home/ubuntu目录下么？

ls -l /home/ubuntu/docker-compose.yml
-rw------- 1 ubuntu ubuntu ... /home/ubuntu/docker-compose.yml

### 2.4 提交代码触发 GitHub Actions
在本地项目根目录下，执行以下命令提交代码：
建立新分支:
git checkout -b dev

开发，测试，提交代码:
git add .
git commit -m "描述你的更改"
git push origin dev

合并到主分支
git checkout main
git merge dev
git push origin main
git push origin main --force

在 GitHub 仓库的项目的 Actions 标签页，点击 workflow，可以观察执行状态，修改代码再运行等。

### 2.5 访问前端页面，确认部署成功
访问 http://<EC2_HOST>:3000 查看前端页面，确认部署成功。


### 三、本地开发与 CI/CD 衔接脚本
# 本地开发
完成前后端开发和测试

# 测试前后端的打包（Dockerfile中会重新制作）
1. 测试打包后端, 生成jar包
cd backend
./mvnw clean package -DskipTests

2. 编译前端，生成dist目录
cd frontend
npm run build


### 四、常见问题

### 4.1 Docker compose 部署失败
1. 确认docker-compose.yml部署位置
docker-compose.yml 要提前配置在EC2的/home/ubuntu目录下，并且要确保配置文件中，容器名称和镜像名称要保持一致。
2. 确认镜像名
docker-compose.yml 中的镜像名，和docerhub上的镜像名要保持一致。参数取不到，采取直接书写的方式。

### 4.2 部署成功后，前端无法访问
因为 Dockerfile 部署后前端目录结构变化，导致前端无法访问。
COPY --from=frontend-builder /app/dist/*/browser/ ./src/main/resources/static/

解决方法：
修改后端的放行规则
