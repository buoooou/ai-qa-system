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

# 成功：登录成功
C:\>curl -X POST http://localhost:8080/api/user/login -H "Content-Type: application/json" -d "{ \"username\": \"test1\", \"password\": \"test001\" }"
{"code":200,"message":"Success","data":{"id":3,"username":"test1","token":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q"}}

### --------------------------------------------------------------------------------------
# 功能3：提问和插入、查询、删除历史记录
### --------------------------------------------------------------------------------------
# 提问
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q" -d "{ \"question\": \" 美国现任总统是谁？ \" }"
{"code":200,"message":"Success","data":{"id":17,"userId":3,"question":" 美国现任总统是谁？ ","answer":"美国的现任总统是乔·拜登 (Joe Biden)。\n","createTime":"2025-09-19T11:15:19.0009338"}}

# 追加提问
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q" -d "{ \"question\": \" 他今年多大年龄？（今年是哪一年？） \" }"
{"code":200,"message":"Success","data":{"id":18,"userId":3,"question":" 他今年多大年龄？（今年是哪一年？） ","answer":"乔·拜登总统于1942年11月20日出生。 截至2024年，他81岁。\n","createTime":"2025-09-19T11:16:25.3094095"}}

# 历史
C:\>curl -X GET http://localhost:8080/api/qa/history -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q"
{"code":200,"message":"Success","data":[{"id":18,"userId":3,"question":" 他今年多大年龄？（今年是哪一年？） ","answer":"乔·拜登总统于1942年11月20日出生。 截至2024年，他81岁。\n","createTime":"2025-09-19T11:16:25"},{"id":17,"userId":3,"question":" 美国现任总统是谁？ ","answer":"美国的现任总统是乔·拜登 (Joe Biden)。\n","createTime":"2025-09-19T11:15:19"}]}


# 删除历史，查询历史
# 删除
C:\>curl -X POST http://localhost:8080/api/qa/deleteHistory -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q"
{"code":200,"message":"Success","data":true}

# 查询历史
C:\>curl -X GET http://localhost:8080/api/qa/history -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q"
{"code":200,"message":"Success","data":[]}

### --------------------------------------------------------------------------------------
# 功能4：查询用户名
### --------------------------------------------------------------------------------------
# 使用qa服务的getUserName接口(服务间调用)
C:\>curl -X GET http://localhost:8080/api/qa/getUserName -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q"
{"code":200,"message":"Success","data":"test1"}

# 使用userservice的getUserName接口
C:\>curl -X GET http://localhost:8080/api/user/getUserName?userId=3 -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODI1MDI1OCwiZXhwIjoxNzU4MzM2NjU4fQ.AbKIesOib7jkhJDOI4dsc-lbw3mvk4zwHyZ--iuQFMP2L2P9QsE06qMMTKasBJUitbf4xMEGxySmmjqtg_2-9Q"
{"code":200,"message":"Success","data":"test1"}

