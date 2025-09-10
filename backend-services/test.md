### 注册
# 成功
C:\>curl -X POST http://localhost:8081/api/user/register -H "Content-Type: application/json" -d "{ \"username\": \"test\", \"password\": \"test123\" }"
{"code":200,"message":"Success","data":{"id":2,"username":"test","token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzU3NDc5NjYxLCJleHAiOjE3NTc0ODMyNjF9.aqUYbWtNoobTEVQL2GY3FMtKpBkB2SjlYfrD7p8n661UbbONvzmAwAlDokdc7P2PQ1uLNcsLBk2fJqoMGAeceA"}}

# 失败: 用户名已存在
C:\>curl -X POST http://localhost:8081/api/user/register -H "Content-Type: application/json" -d "{ \"username\": \"test\", \"password\": \"test123\" }"
{"code":400,"message":"用户名已存在","data":null}

### 登录
# 成功
C:\>curl -X POST http://localhost:8081/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test\", \"password\": \"test123\" }"
{"code":200,"message":"Success","data":{"id":2,"username":"test","token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzU3NDc5NzM2LCJleHAiOjE3NTc0ODMzMzZ9.a4NoW9NVXGYY87kdQfj2RTzMy3aMm57Cs9FW00TiAr8uHMzgKbP1RybTWCBzfL_d5k2nmgrQ_G-ZYKA3tx6v3w"}}

# 失败: 用户名错误
C:\>curl -X POST http://localhost:8081/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test123\" }"
{"code":401,"message":"用户名错误","data":null}

# 失败：密码错误
C:\>curl -X POST http://localhost:8081/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test\", \"password\": \"test1234\" }"
{"code":401,"message":"用户名或密码错误","data":null}