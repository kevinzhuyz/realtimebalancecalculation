# 使用 Postman 连接 K8s 集群应用测试指南

## 1. 导入 Collection

### 1.1 步骤
1. 打开 Postman
2. 点击 "Import" 按钮
3. 选择 "Raw text"
4. 粘贴下方 Collection JSON

### 1.2 Collection JSON
```json
{
  "info": {
    "name": "Account Balance API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Users",
      "item": [
        {
          "name": "Create User",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/api/users",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\"\n}"
            }
          }
        },
        {
          "name": "Get User",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/api/users/{{userId}}"
          }
        }
      ]
    },
    {
      "name": "Accounts",
      "item": [
        {
          "name": "Create Account",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/api/accounts",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"userId\": {{userId}},\n  \"balance\": 1000.00,\n  \"creditLimit\": 5000.00\n}"
            }
          }
        },
        {
          "name": "Get All Accounts",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/api/accounts"
          }
        },
        {
          "name": "Get Account by ID",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/api/accounts/{{accountId}}"
          }
        }
      ]
    },
    {
      "name": "Transactions",
      "item": [
        {
          "name": "Create Transaction",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/api/transactions",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"accountId\": {{accountId}},\n  \"amount\": 100.00,\n  \"type\": \"DEPOSIT\",\n  \"description\": \"Initial deposit\"\n}"
            }
          }
        },
        {
          "name": "Get All Transactions",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/api/transactions"
          }
        }
      ]
    },
    {
      "name": "Health Checks",
      "item": [
        {
          "name": "Health Status",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/actuator/health"
          }
        },
        {
          "name": "Metrics",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/actuator/metrics"
          }
        },
        {
          "name": "Prometheus Metrics",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/actuator/prometheus"
          }
        }
      ]
    }
  ]
}
```

## 2. 创建环境变量

### 2.1 步骤
1. 点击右上角的齿轮图标
2. 点击 "Add" 创建新环境
3. 名称设为 "K8s Local"
4. 添加以下变量

### 2.2 环境变量
```json
{
  "baseUrl": "http://localhost",
  "userId": "1",
  "accountId": "1"
}
```

## 3. API 测试流程

### 3.1 健康检查
```http
GET {{baseUrl}}/actuator/health
```
预期响应：
```json
{
    "status": "UP"
}
```

### 3.2 创建用户
```http
POST {{baseUrl}}/api/users
Content-Type: application/json

{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
}
```

### 3.3 创建账户
```http
POST {{baseUrl}}/api/accounts
Content-Type: application/json

{
    "userId": 1,
    "balance": 1000.00,
    "creditLimit": 5000.00
}
```

### 3.4 创建交易
```http
POST {{baseUrl}}/api/transactions
Content-Type: application/json

{
    "accountId": 1,
    "amount": 100.00,
    "type": "DEPOSIT",
    "description": "Initial deposit"
}
```

## 4. 注意事项

1. 确保 K8s 集群正常运行
2. 确保 Ingress 配置正确
3. 所有请求都通过 Ingress 路由（http://localhost）
4. 测试前先检查健康状态端点
5. 按照用户->账户->交易的顺序进行测试

## 5. 常见问题排查

1. 如果遇到连接问题，检查：
   - Ingress 配置
   - K8s 服务状态
   - 应用健康状态

2. 如果遇到 404 错误：
   - 检查 API 路径是否正确
   - 检查 Ingress 路由配置

3. 如果遇到 503 错误：
   - 检查 Pod 运行状态
   - 检查服务端点配置 