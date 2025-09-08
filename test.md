启动User Service
cd ai-qa-system/backend-services/user-service && mvn spring-boot:run

测试用户服务的健康检查接口
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X GET http://localhost:8081/api/user/health
{"code":200,"message":"User Service is running","data":"User Service is running","timestamp":1757306693619,"success":true,"error":false}%   

测试用户注册
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X POST http://localhost:8081/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "confirmPassword": "Test123456",
    "email": "test@example.com"
  }'
{"code":200,"message":"注册成功","data":{"id":1,"username":"te***er","email":"t***t@example.com","status":1,"createTime":"2025-09-08 12:54:24","updateTime":"2025-09-08 12:54:24"},"timestamp":1757307265429,"success":true,"error":false}%   

测试JWT登录功能
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "password": "Test123456"
  }'
{"code":200,"message":"登录成功","data":{"user":{"id":1,"userName":"testuser","password":null,"email":"test@example.com","status":1,"createTime":"2025-09-08T12:54:24.753304","updateTime":"2025-09-08T12:54:24.753312"},"accessToken":"eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJhY2Nlc3MiLCJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTc1NzMwNzUzOSwiZXhwIjoxNzU3MzE0NzM5fQ.WN1z6Mrj7GNnuaJwfb9e70LKN1ZjCzSHN5a1U0d2o79kcd8nXfrQR3lh0-kS8qtNtHzw1PK3uWwX1_KumxTUSA","refreshToken":"eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJyZWZyZXNoIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwic3ViIjoidGVzdHVzZXIiLCJpYXQiOjE3NTczMDc1MzksImV4cCI6MTc1NzkxMjMzOX0.EcUPLYMACAGkTXEC0qb0QYORs7BxieIuCX20fzoghD1fJTX2Gf335CROh0fdS1koqkCLIrl1iYeMIweeee62VA","tokenType":"Bearer","expiresIn":7200},"timestamp":1757307539726,"success":true,"error":false}%   

测试令牌验证
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X GET http://localhost:8081/api/auth/verify -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJhY2Nlc3MiLCJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTc1NzMwNzUzOSwiZXhwIjoxNzU3MzE0NzM5fQ.WN1z6Mrj7GNnuaJwfb9e70LKN1ZjCzSHN5a1U0d2o79kcd8nXfrQR3lh0-kS8qtNtHzw1PK3uWwX1_KumxTUSA"
{"code":200,"message":"令牌有效","data":"令牌有效","timestamp":1757308630096,"success":true,"error":false}%               

测试令牌刷新功能
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X POST http://localhost:8081/api/auth/refresh -H "Content-Type: application/json" -d '{"refreshToken":"eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJyZWZyZXNoIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwic3ViIjoidGVzdHVzZXIiLCJpYXQiOjE3NTczMDc1MzksImV4cCI6MTc1NzkxMjMzOX0.EcUPLYMACAGkTXEC0qb0QYORs7BxieIuCX20fzoghD1fJTX2Gf335CROh0fdS1koqkCLIrl1iYeMIweeee62VA"}'
{"code":200,"message":"令牌刷新成功","data":{"user":{"id":1,"userName":"testuser","password":null,"email":"test@example.com","status":1,"createTime":"2025-09-08T12:54:24.753304","updateTime":"2025-09-08T12:54:24.753312"},"accessToken":"eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJhY2Nlc3MiLCJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTc1NzMwODY1OSwiZXhwIjoxNzU3MzE1ODU5fQ.46Njj7ET6ZNmSByEhNxVTlu4hB36nasMQR-ywufyacgqkkiPe1KmLPVRcDXWPMJVGS0UH4p1mOG_yW9v4FpCzA","refreshToken":"eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJyZWZyZXNoIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwic3ViIjoidGVzdHVzZXIiLCJpYXQiOjE3NTczMDc1MzksImV4cCI6MTc1NzkxMjMzOX0.EcUPLYMACAGkTXEC0qb0QYORs7BxieIuCX20fzoghD1fJTX2Gf335CROh0fdS1koqkCLIrl1iYeMIweeee62VA","tokenType":"Bearer","expiresIn":7200},"timestamp":1757308659046,"success":true,"error":false}%           

测试获取用户信息功能
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X GET http://localhost:8081/api/auth/profile -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJhY2Nlc3MiLCJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTc1NzMwODY1OSwiZXhwIjoxNzU3MzE1ODU5fQ.46Njj7ET6ZNmSByEhNxVTlu4hB36nasMQR-ywufyacgqkkiPe1KmLPVRcDXWPMJVGS0UH4p1mOG_yW9v4FpCzA"
{"code":200,"message":"获取用户信息成功","data":{"id":1,"userName":"testuser","password":null,"email":"test@example.com","status":1,"createTime":"2025-09-08T12:54:24.753304","updateTime":"2025-09-08T12:54:24.753312"},"timestamp":1757308685143,"success":true,"error":false}%     

启动QA Service
cd ai-qa-system/backend-services/qa-service && mvn spring-boot:run

测试Q&A Service的功能
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl http://localhost:8082/api/qa/health
{"code":200,"message":"QA Service is running","data":null}%      

测试问答功能（使用模拟回答，因为Gemini API Key未配置）
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X POST http://localhost:8082/api/qa/ask -H "Content-Type: application/json" -d '{"userId":1,"question":"你好，请介绍一下你自己"}'
{"code":200,"message":"问答成功","data":{"id":3,"userId":1,"question":"你好，请介绍一下你自己","answer":"你好！我是AI智能助手，很高兴为您服务。请问有什么可以帮助您的吗？","createTime":"2025-09-08T13:28:20.562511","model":"gemini-pro","responseTime":251}}% 

测试获取问答历史
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl http://localhost:8082/api/qa/history/1
{"code":200,"message":"查询成功","data":[{"id":3,"userId":1,"question":"你好，请介绍一下你自己","answer":"你好！我是AI智能助手，很高兴为您服务。请问有什么可以帮助您的吗？","createTime":"2025-09-08T13:28:20.562511","model":null,"responseTime":null}]}% 


启动API Gateway
cd ai-qa-system/backend-services/api-gateway && mvn spring-boot:run

测试通过API Gateway访问各个服务
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl http://localhost:8080/actuator/health
{"status":"UP","components":{"discoveryComposite":{"status":"UP","components":{"discoveryClient":{"status":"UP","details":{"services":["api-gateway","user-service-1","user-service-jiayan","qa-service"]}}}},"diskSpace":{"status":"UP","details":{"total":245107195904,"free":27404529664,"threshold":10485760,"exists":true}},"nacosDiscovery":{"status":"UP"},"ping":{"status":"UP"},"reactiveDiscoveryClients":{"status":"UP","components":{"Simple Reactive Discovery Client":{"status":"UP","details":{"services":[]}}}},"refreshScope":{"status":"UP"}}}% 

测试网关路由
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl http://localhost:8080/actuator/gateway/routes
[{"predicate":"Paths: [/user-service-jiayan/**], match trailing slash: true","metadata":{"nacos.instanceId":"192.168.1.253#8081#DEFAULT#DEFAULT_GROUP@@user-service-jiayan","nacos.weight":"1.0","nacos.cluster":"DEFAULT","IPv6":"[2409:8a63:398:e350:6bfc:d5b3:e926:7025]","nacos.ephemeral":"true","nacos.healthy":"true","preserved.register.source":"SPRING_CLOUD"},"route_id":"ReactiveCompositeDiscoveryClient_user-service-jiayan","filters":["[[RewritePath /user-service-jiayan/?(?<remaining>.*) = '/${remaining}'], order = 1]"],"uri":"lb://user-service-jiayan","order":0},{"predicate":"Paths: [/api-gateway/**], match trailing slash: true","metadata":{"nacos.instanceId":"9.78.239.6#8080#DEFAULT#DEFAULT_GROUP@@api-gateway","nacos.weight":"1.0","nacos.cluster":"DEFAULT","preserved.register.source":"SPRING_CLOUD","nacos.ephemeral":"true","nacos.healthy":"true"},"route_id":"ReactiveCompositeDiscoveryClient_api-gateway","filters":["[[RewritePath /api-gateway/?(?<remaining>.*) = '/${remaining}'], order = 1]"],"uri":"lb://api-gateway","order":0},{"predicate":"Paths: [/qa-service/**], match trailing slash: true","metadata":{"nacos.instanceId":"9.78.239.6#8082#DEFAULT#DEFAULT_GROUP@@qa-service","nacos.weight":"1.0","nacos.cluster":"DEFAULT","preserved.register.source":"SPRING_CLOUD","nacos.ephemeral":"true","nacos.healthy":"true"},"route_id":"ReactiveCompositeDiscoveryClient_qa-service","filters":["[[RewritePath /qa-service/?(?<remaining>.*) = '/${remaining}'], order = 1]"],"uri":"lb://qa-service","order":0},{"predicate":"Paths: [/user-service-1/**], match trailing slash: true","metadata":{"nacos.instanceId":"9.78.239.6#8081#DEFAULT#DEFAULT_GROUP@@user-service-1","nacos.weight":"1.0","nacos.cluster":"DEFAULT","preserved.register.source":"SPRING_CLOUD","nacos.ephemeral":"true","nacos.healthy":"true"},"route_id":"ReactiveCompositeDiscoveryClient_user-service-1","filters":["[[RewritePath /user-service-1/?(?<remaining>.*) = '/${remaining}'], order = 1]"],"uri":"lb://user-service-1","order":0},{"predicate":"Paths: [/api/user/**], match trailing slash: true","route_id":"user_service_route","filters":["[[StripPrefix parts = 0], order = 1]"],"uri":"lb://user-service-1","order":0},{"predicate":"Paths: [/api/auth/**], match trailing slash: true","route_id":"auth_service_route","filters":["[[StripPrefix parts = 0], order = 1]"],"uri":"lb://user-service-1","order":0},{"predicate":"Paths: [/api/qa/**], match trailing slash: true","route_id":"qa_service_route","filters":["[[StripPrefix parts = 0], order = 1]"],"uri":"lb://qa-service","order":0}]%   

测试Q&A服务
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl http://localhost:8080/api/qa/health
{"code":200,"message":"QA Service is running","data":null}%     

测试其他服务
qiaozhe@qiaozhedebijibendiannao FullStack Development Training Camp_三期 % curl -X POST http://localhost:8080/api/qa/ask -H "Content-Type: application/json" -d '{"userId":1,"question":"通过API Gateway测试问答功能"}'
{"code":200,"message":"问答成功","data":{"id":4,"userId":1,"question":"通过API Gateway测试问答功能","answer":"你好！我是AI智能助手，很高兴为您服务。请问有什么可以帮助您的吗？","createTime":"2025-09-08T13:45:04.464154","model":"gemini-pro","responseTime":219}}%  