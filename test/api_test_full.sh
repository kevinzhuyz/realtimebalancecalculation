#!/bin/bash

# 设置API基础URL
API_BASE="http://localhost:8080/api"

# 测试函数
test_api() {
    local description=$1
    local method=$2
    local endpoint=$3
    local data=$4

    echo "测试: $description"
    echo "请求: $method $endpoint"
    echo "数据: $data"
    
    response=$(curl -s -X $method \
        -H "Content-Type: application/json" \
        -w "\nHTTP_STATUS:%{http_code}" \
        -d "$data" \
        $API_BASE$endpoint)
    
    status=$(echo "$response" | grep "HTTP_STATUS" | cut -d':' -f2)
    body=$(echo "$response" | sed '$d')
    
    echo "状态码: $status"
    echo "响应: $body"
    echo "----------------------------------------"
}

# 1. 测试正常创建用户
test_api "正常创建用户" "POST" "/users" '{
    "name": "正常用户",
    "gender": "MALE",
    "password": "Test123456"
}'

sleep 1

# 2. 测试创建重复用户名
test_api "创建重复用户名" "POST" "/users" '{
    "name": "正常用户",
    "gender": "MALE",
    "password": "Test123456"
}'

sleep 1

# 3. 测试密码格式错误
test_api "密码格式错误" "POST" "/users" '{
    "name": "密码错误用户",
    "gender": "MALE",
    "password": "123"
}'

sleep 1

# 4. 测试性别格式错误
test_api "性别格式错误" "POST" "/users" '{
    "name": "性别错误用户",
    "gender": "UNKNOWN",
    "password": "Test123456"
}'

# 验证数据
echo -e "\n验证数据库数据..."
mysql -h localhost -u root -p123456 VTMSystem -e 'SELECT id, name, gender, created_at FROM users;'

echo -e "\n验证Redis缓存..."
redis-cli -h localhost -p 6379 -a 123456 KEYS "user:*" 