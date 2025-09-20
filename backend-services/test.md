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
{"code":200,"message":"Success","data":{"id":3,"username":"test1","token":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"}}

### --------------------------------------------------------------------------------------
# 功能3：提问和插入、查询、删除历史记录
### --------------------------------------------------------------------------------------
# 提问
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw" -d "{ \"conversationId\": null, \"question\": \"美国现任总统是谁？\" }"
{"code":200,"message":"Success","data":{"id":24,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"美国现任总统是谁？","answer":"美国现任总统是乔·拜登 (Joe Biden)。\n","createTime":"2025-09-20T15:10:40.2612726"}}

# 追加提问2
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw" -d "{ \"conversationId\": \"41a025cc29ec407096dfcf4ab5e98c58859\", \"question\": \"他今年多大年龄？（今年是哪一年？）\" }"
{"code":200,"message":"Success","data":{"id":25,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"他今年多大年龄？（今年是哪一年？）","answer":"乔·拜登总统的出生日期是1942年11月20日。 截至2024年10月26日，他81岁。\n","createTime":"2025-09-20T15:13:12.2092585"}}

# 追加提问3
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw" -d "{ \"conversationId\": \"41a025cc29ec407096dfcf4ab5e98c58859\", \"question\": \"下一届总统会是特朗普？\" }"

# 新会话
C:\>curl -X POST http://localhost:8080/api/qa/question -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw" -d "{ \"conversationId\": null, \"question\": \"我猜下一届美国总统会是特朗普，对吧。\" }"
{"code":200,"message":"Success","data":{"id":27,"userId":3,"conversationId":"b3712fc2dcda4fb59bc43564a3a580d6923","question":"我猜下一届美国总统会是特朗普，对吧。","answer":"预测下一届美国总统选举的结果非常困难，即使是现在距离选举还有一段时间。  虽然唐纳德·特朗普已经宣布竞选，但他面临着来自共和党内和其他党派候选人的激烈竞争。  民调结果会随着时间的推移而变化，而且它们也并非总是准确的预测指标。  因此，说特朗普“对吧”是过于武断的。  最终结果将取决于许多因素，包括经济状况、国内外事件以及候选人的竞选活动。\n","createTime":"2025-09-20T15:18:18.2873536"}}


# 查询所有历史
C:\>curl -X GET http://localhost:8080/api/qa/conversations/allHistory -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"
{"code":200,"message":"Success","data":[{"id":24,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"美国现任总统是谁？","answer":"美国现任总统是乔·拜登 (Joe Biden)。\n","createTime":"2025-09-20T15:10:40"},{"id":25,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"他今年多大年龄？（今年是哪一年？）","answer":"乔·拜登总统的出生日期是1942年11月20日。 截至2024年10月26日，他81岁。\n","createTime":"2025-09-20T15:13:12"},{"id":26,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"下一届总统会是特朗普？","answer":"目前还无法确定下一届美国总统会是谁。  虽然唐纳德·特朗普已宣布参加2024年总统大选，但他是否会赢得选举仍有待观察。  选举结果取决于许多因素，包括其他候选人的表现、选民的偏好以及竞选活动本身。\n","createTime":"2025-09-20T15:15:45"},{"id":27,"userId":3,"conversationId":"b3712fc2dcda4fb59bc43564a3a580d6923","question":"我猜下一届美国总统会是特朗普，对吧。","answer":"预测下一届美国总统选举的结果非常困难，即使是现在距离选举还有一段时间。  虽然唐纳德·特朗普已经宣布竞选，但他面临着来自共和党内和其他党派候选人的激烈竞争。  民调结果会随着时间的推移而变化，而且它们也并非总是准确的预测指标。  因此，说特朗普“对吧”是过于武断的。  最终结果将取决于许多因素，包括经济状况、国内外事件以及候选人的竞选活动。\n","createTime":"2025-09-20T15:18:18"}]}

# 查询某个会话的历史
C:\>curl -X GET http://localhost:8080/api/qa/conversation/history?conversationId="41a025cc29ec407096dfcf4ab5e98c58859" -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"
{"code":200,"message":"Success","data":[{"id":24,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"美国现任总统是谁？","answer":"美国现任总统是乔·拜登 (Joe Biden)。\n","createTime":"2025-09-20T15:10:40"},{"id":25,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"他今年多大年龄？（今年是哪一年？）","answer":"乔·拜登总统的出生日期是1942年11月20日。 截至2024年10月26日，他81岁。\n","createTime":"2025-09-20T15:13:12"},{"id":26,"userId":3,"conversationId":"41a025cc29ec407096dfcf4ab5e98c58859","question":"下一届总统会是特朗普？","answer":"目前还无法确定下一届美国总统会是谁。  虽然唐纳德·特朗普已宣布参加2024年总统大选，但他是否会赢得选举仍有待观察。  选举结果取决于许多因素，包括其他候选人的表现、选民的偏好以及竞选活动本身。\n","createTime":"2025-09-20T15:15:45"}]}

# 删除历史记录
# 删除当前用户的指定会话历史
C:\>curl -X DELETE http://localhost:8080/api/qa/conversation/delete?conversationId="41a025cc29ec407096dfcf4ab5e98c58859" -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"
{"code":200,"message":"Success","data":true}

# 删除当前用户的所有会话历史
C:\>curl -X DELETE http://localhost:8080/api/qa/conversations/deleteAll -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"
{"code":200,"message":"Success","data":true}

### --------------------------------------------------------------------------------------
# 功能4：查询用户名
### --------------------------------------------------------------------------------------
# 使用qa服务的getUserName接口(服务间调用)
C:\>curl -X GET http://localhost:8080/api/qa/getUserName -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"
{"code":200,"message":"Success","data":"test1"}

# 使用userservice的getUserName接口
C:\>curl -X GET http://localhost:8080/api/user/getUserName?userId=3 -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"
{"code":200,"message":"Success","data":"test1"}

### --------------------------------------------------------------------------------------
# 功能5：取得会话ID列表
### --------------------------------------------------------------------------------------
C:\>curl -X GET http://localhost:8080/api/qa/conversations/IDList?userId=3 -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyaWQiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0MSIsImlhdCI6MTc1ODM0OTU3MiwiZXhwIjoxNzU4NDM1OTcyfQ.e06uR6SzwW7iQCO-fAaFTV0jsjG8rg2i6RaIbB5FdIu7szXN3JMzf-lBRDlzcIPsngl4I7z4e1E4xZSQDBMetw"
{"code":200,"message":"Success","data":["32428a3b40e7482fbd5c52395565b99f784","7e52601d6f1b4f89aa8eb6883111776c857"]}
