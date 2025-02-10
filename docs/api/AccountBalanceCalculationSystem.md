# 账户余额计算系统 API 文档

## 1. 概述

### 1.1 简介
账户余额计算系统提供账户管理、交易处理和余额计算等功能的REST API接口。

### 1.2 基础信息
- 基础URL: `http://localhost:8080`
- 响应格式: JSON
- 认证方式: 待实现

## 2. 用户管理接口

### 2.1 创建用户
```http
POST /api/users
```

**请求参数:**
```json
{
    "name": "string",      // 用户名
    "password": "string",  // 密码
    "gender": "string"     // 性别
}
```

**响应示例:**
```json
{
    "id": 1,
    "name": "testUser",
    "gender": "M",
    "createdAt": "2024-03-15T10:00:00"
}
```

### 2.2 获取用户信息
```http
GET /api/users/{userId}
```

**响应示例:**
```json
{
    "id": 1,
    "name": "testUser",
    "gender": "M",
    "createdAt": "2024-03-15T10:00:00"
}
```

## 3. 账户管理接口

### 3.1 创建账户
```http
POST /api/accounts
```

**请求参数:**
```json
{
    "userId": 1,
    "accountNumber": "ACC001",
    "creditLimit": 5000.00
}
```

**响应示例:**
```json
{
    "id": 1,
    "userId": 1,
    "accountNumber": "ACC001",
    "balance": 0.00,
    "creditLimit": 5000.00,
    "createdAt": "2024-03-15T10:00:00"
}
```

### 3.2 获取账户信息
```http
GET /api/accounts/{accountId}
```

**响应示例:**
```json
{
    "id": 1,
    "userId": 1,
    "accountNumber": "ACC001",
    "balance": 1000.00,
    "creditLimit": 5000.00,
    "createdAt": "2024-03-15T10:00:00"
}
```

### 3.3 存款
```http
POST /api/accounts/{accountId}/deposit
```

**请求参数:**
```
amount: 100.00  // 存款金额
```

**响应示例:**
```json
{
    "id": 1,
    "balance": 1100.00,
    "updatedAt": "2024-03-15T10:30:00"
}
```

### 3.4 取款
```http
POST /api/accounts/{accountId}/withdraw
```

**请求参数:**
```
amount: 100.00  // 取款金额
```

**响应示例:**
```json
{
    "id": 1,
    "balance": 900.00,
    "updatedAt": "2024-03-15T10:30:00"
}
```

## 4. 交易管理接口

### 4.1 创建交易
```http
POST /api/transactions
```

**请求参数:**
```json
{
    "sourceAccountId": 1,
    "targetAccountId": 2,
    "amount": 100.00,
    "type": "TRANSFER",
    "description": "转账交易"
}
```

**响应示例:**
```json
{
    "id": 1,
    "sourceAccountId": 1,
    "targetAccountId": 2,
    "amount": 100.00,
    "type": "TRANSFER",
    "description": "转账交易",
    "transactionTime": "2024-03-15T10:30:00"
}
```

### 4.2 获取交易记录
```http
GET /api/transactions/{transactionId}
```

**响应示例:**
```json
{
    "id": 1,
    "sourceAccountId": 1,
    "targetAccountId": 2,
    "amount": 100.00,
    "type": "TRANSFER",
    "description": "转账交易",
    "transactionTime": "2024-03-15T10:30:00"
}
```

### 4.3 获取账户交易记录
```http
GET /api/transactions/account/{accountId}
```

**响应示例:**
```json
[
    {
        "id": 1,
        "sourceAccountId": 1,
        "targetAccountId": 2,
        "amount": 100.00,
        "type": "TRANSFER",
        "description": "转账交易",
        "transactionTime": "2024-03-15T10:30:00"
    }
]
```

## 5. 错误码说明

| 错误码 | 描述 | 处理建议 |
|-------|------|---------|
| 400 | 请求参数错误 | 检查请求参数格式和值 |
| 401 | 未授权 | 检查认证信息 |
| 403 | 权限不足 | 确认操作权限 |
| 404 | 资源不存在 | 检查资源ID是否正确 |
| 500 | 服务器内部错误 | 联系系统管理员 |



