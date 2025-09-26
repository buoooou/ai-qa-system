##### 项目介绍

这是后端微服务程序，主要功能注册登录，以及提供 AI 问答系统的后台服务，并保留应答履历。

主要微服务如下：
- api-gateway：提供 gateway 网关服务，负责请求转发、权限校验等功能。
- user-service：提供用户注册、登录等功能。
- qa-service：提供 AI 问答系统的后台服务，并保留应答履历。

安全限制：
- 所有微服务经由api-gateway转发，不能直接访问其他微服务。

### 主要配置
java 17
spring boot 2.7.17


### 参考资料

# 课程内容:
https://kuo.postions.app/ai-chatbot-img

# nacos 注册中心
https://nacos.io/docs/latest/overview/

# 本作业使用的 nacos 服务器，可确认服务
nacos服务器：
http://3.101.113.38:8080/#/login

apikey 申请网站：
https://aistudio.google.com/apikey

