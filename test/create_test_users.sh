#!/bin/bash

# 创建多个测试用户
create_user() {
    local name=$1
    local gender=$2
    
    echo "创建用户: $name"
    curl -X POST http://localhost:8080/api/users \
      -H "Content-Type: application/json" \
      -d "{
        \"name\": \"$name\",
        \"gender\": \"$gender\",
        \"password\": \"test123456\"
      }"
    echo -e "\n"u的
}

# 创建一组测试用户
create_user "张三" "MALE"
sleep 1
create_user "李四" "FEMALE"
sleep 1
create_user "王五" "MALE"

# 验证数据
echo "验证MySQL中的用户数据..."
kubectl exec -it db-test-pod -- bash -c "
apt-get update && apt-get install -y mysql-client && \
mysql -h host.docker.internal -u root -p123456 VTMSystem -e 'SELECT id, name, gender, created_at FROM users;'
"

echo "验证Redis缓存..."
kubectl exec -it db-test-pod -- bash -c "
apt-get update && apt-get install -y redis-tools && \
redis-cli -h host.docker.internal -p 6379 -a 123456 KEYS 'user:*'
" 