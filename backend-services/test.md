##### 通过 gateway 访问后端服务

### --------------------------------------------------------------------------------------
### 功能1：注册
### --------------------------------------------------------------------------------------

# 成功：注册test1成功！
C:\>curl -X POST http://localhost:8080/api/user/register -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test001\" }"
{"code":200,"message":"Success","data":{"id":3,"username":"test1","token":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3MzQyMSwiZXhwIjoxNzU4MjU5ODIxfQ.JxntF8gLmxGIS-0zJQN-DndHgW2_Hv11fJhIOINV3wFP7ucysJcoV8PTVSglQoV5WdXiY9I0fncqRxuYsqX96g"}}

# 失败：重复注册
C:\>curl -X POST http://localhost:8080/api/user/register -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test002\" }"
{"code":400,"message":"用户名已存在","data":null}

# 失败：直接访问
C:\>curl -X POST http://localhost:8081/api/user/register -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test001\" }"
{"code":401,"message":"请通过网关访问"}

# 成功：注册test2成功！
C:\>curl -X POST http://localhost:8080/api/user/register -H "Content-Type: application/json" -d "{ \"username\": \"test2\", \"password\": \"test002\" }"
{"code":200,"message":"Success","data":{"id":4,"username":"test2","token":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiI0IiwidXNlcm5hbWUiOiJ0ZXN0MiIsImlhdCI6MTc1ODE3MzUxNSwiZXhwIjoxNzU4MjU5OTE1fQ.0nu79xN3cvQM82FhRnI5-kYmJOkykID9yvewuIGs9xsifsP4H3Oshn2e_S9LzQX6Bgtj6T3jbpNRuX2brrgk1A"}}

### --------------------------------------------------------------------------------------
### 功能2：登录
### --------------------------------------------------------------------------------------

# 失败：直接访问
C:\>curl -X POST http://localhost:8081/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test001\" }"
{"code":401,"message":"请通过网关访问"}

# 失败：用户名错误
C:\>curl -X POST http://localhost:8080/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test3\", \"password\": \"test001\" }"
{"code":401,"message":"用户名错误","data":null}

# 失败：密码错误
C:\>curl -X POST http://localhost:8080/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test001x\" }"
{"code":401,"message":"用户名或密码错误","data":null}

C:\>curl -X POST http://localhost:8080/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test001\" }"
{"code":200,"message":"Success","data":{"id":3,"username":"test1","token":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw"}}

### --------------------------------------------------------------------------------------
# 功能3：提问和插入、查询、删除历史记录
### --------------------------------------------------------------------------------------
# 提问
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw" -d "{ \"userId\": 1, \"question\": \"1 + 1 = ? \" }"
{"code":200,"message":"Success","data":{"id":13,"userId":1,"question":"1 + 1 = ? ","answer":"1 + 1 = 2\n","createTime":"2025-09-18T14:26:15.3601666"}}

# 历史
C:\>curl -X GET http://localhost:8080/api/qa/history -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw" -d "{ \"userId\": 1 }"
{"code":200,"message":"Success","data":[{"id":13,"userId":1,"question":"1 + 1 = ? ","answer":"1 + 1 = 2\n","createTime":"2025-09-18T14:26:15"}]}

### 查询 成功 (追加提问: 上一个问题)
# 提问
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw" -d "{ \"userId\": 1, \"question\": \"上一个问题是什么? \" }"
{"code":200,"message":"Success","data":{"id":14,"userId":1,"question":"上一个问题是什么? ","answer":"上一个问题是：1 + 1 = ?\n","createTime":"2025-09-18T14:27:41.8499454"}}

# 取得历史
C:\>curl -X GET http://localhost:8080/api/qa/history -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw" -d "{ \"userId\": 1 }"
{"code":200,"message":"Success","data":[{"id":14,"userId":1,"question":"上一个问题是什么? ","answer":"上一个问题是：1 + 1 = ?\n","createTime":"2025-09-18T14:27:42"},{"id":13,"userId":1,"question":"1 + 1 = ? ","answer":"1 + 1 = 2\n","createTime":"2025-09-18T14:26:15"}]}

# 删除历史，查询历史
# 删除
C:\>curl -X POST http://localhost:8080/api/qa/deleteHistory?userId=1 -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw"
{"code":200,"message":"Success","data":true}

# 查询历史
C:\>curl -X GET http://localhost:8080/api/qa/history -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw" -d "{ \"userId\": 1 }"
{"code":200,"message":"Success","data":[]}

### --------------------------------------------------------------------------------------
# 功能4：提问和查询历史
### --------------------------------------------------------------------------------------
C:\>curl -X GET http://localhost:8080/api/qa/getUserName?userId=1 -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw" -d "{ \"userId\": 1 }"

### --------------------------------------------------------------------------------------
# 功能5：查询用户名
### --------------------------------------------------------------------------------------
# 使用userservice的getUserName接口
C:\>curl -X GET http://localhost:8080/api/user/getUserName?userId=3 -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw"
{"code":200,"message":"Success","data":"test1"}

# 使用qa服务的getUserName接口(服务间调用)
C:\>curl -X GET http://localhost:8080/api/qa/getUserName?userId=3 -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODE3NDkxMywiZXhwIjoxNzU4MjYxMzEzfQ.5Gz3yfiQ5WjmjGlcbCN7FK9-oXBi-sW_NC_cwxfYM_O5ppVHsHugDmddll8pRLU5KU9yKbfSwZ1J52SQPuWHfw"
{"code":200,"message":"Success","data":"test1"}
