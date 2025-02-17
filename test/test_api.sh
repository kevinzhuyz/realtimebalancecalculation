#!/bin/bash

# 测试创建用户API
echo "测试创建用户..."
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试用户",
    "gender": "MALE",
    "password": "test123456"
  }'

echo -e "\n\n验证用户数据..."
# 查询用户数据
curl -X GET http://localhost:8080/api/users/test-user

# 验证Redis缓存
echo -e "\n\n检查Redis缓存..."
kubectl exec -it db-test-pod -- bash -c "
apt-get update && apt-get install -y redis-tools && \
redis-cli -h host.docker.internal -p 6379 -a 123456 GET 'user:test-user'
"

# 验证MySQL数据
echo -e "\n\n检查MySQL数据..."
kubectl exec -it db-test-pod -- bash -c "
apt-get update && apt-get install -y mysql-client && \
mysql -h host.docker.internal -u root -p123456 VTMSystem -e 'SELECT * FROM users WHERE name=\"测试用户\";'
" 