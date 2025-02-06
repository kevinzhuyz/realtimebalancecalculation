# Account Balance Calculation API Documentation
# 账户余额计算系统 API 文档

## Account APIs (账户相关接口)

### 1. Get Account Details (获取账户详情)
- **Endpoint:** GET /api/accounts/{cardId}
- **描述:** 根据卡号获取账户信息
- **响应:** 账户对象或 404 未找到

### 2. Get All Accounts (获取所有账户)
- **Endpoint:** GET /api/accounts
- **描述:** 获取系统中所有账户列表
- **响应:** 账户对象列表

### 3. Create Account (创建账户)
- **Endpoint:** POST /api/accounts
- **描述:** 创建新的账户
- **请求体:** 账户对象
- **响应:** 创建成功的账户对象

### 4. Get Account Transactions (获取账户交易记录)
- **Endpoint:** GET /api/accounts/{cardId}/transactions
- **描述:** 获取指定账户的交易历史记录
- **响应:** 交易记录列表

## Account Operations APIs (账户操作接口)

### 1. Deposit Money (存款)
- **Endpoint:** POST /api/accounts/operations/{cardId}/deposit
- **参数:**
  - cardId (路径参数): 账户卡号
  - amount (查询参数): 存款金额
- **响应:** 更新后的账户对象或错误信息

### 2. Withdraw Money (取款)
- **Endpoint:** POST /api/accounts/operations/{cardId}/withdraw
- **参数:**
  - cardId (路径参数): 账户卡号
  - amount (查询参数): 取款金额
- **响应:** 更新后的账户对象或错误信息

## Transaction APIs (交易相关接口)

### 1. Transfer Money (转账)
- **Endpoint:** POST /api/transactions/transfer
- **参数:**
  - sourceCardId (查询参数): 源账户卡号
  - targetCardId (查询参数): 目标账户卡号
  - amount (查询参数): 转账金额
- **响应:** 交易对象或错误信息

### 2. Get Transaction History (获取交易历史)
- **Endpoint:** GET /api/transactions/account/{cardId}
- **描述:** 获取指定卡号的所有交易记录
- **响应:** 交易记录列表

### 3. Get Transaction Details (获取交易详情)
- **Endpoint:** GET /api/transactions/{transactionId}
- **描述:** 获取指定交易ID的详细信息
- **响应:** 交易对象或 404 未找到

### 4. Get All Transactions (获取所有交易)
- **Endpoint:** GET /api/transactions
- **描述:** 获取系统中所有交易记录
- **响应:** 交易记录列表

## Error Responses (错误响应)

所有接口可能返回以下错误状态码：
- 200: 成功
- 400: 请求参数错误
- 404: 资源未找到
- 500: 服务器内部错误

## Sample Curl Commands (接口调用示例)

### Transfer Money (转账示例)

```bash
curl -X POST "http://localhost:8080/api/transactions/transfer?sourceCardId=6&targetCardId=7&amount=100.00"
```

### Deposit Money (存款示例)

```bash
curl -X POST "http://localhost:8080/api/accounts/operations/6/deposit?amount=100.00"
```

### Get Account Details (查询账户示例)

```bash
curl "http://localhost:8080/api/accounts/6"
```

### Get Transaction History (查询交易历史示例)
```bash
curl "http://localhost:8080/api/transactions/account/6"
```

### Get Transaction Details (查询交易详情示例)
```bash
curl "http://localhost:8080/api/transactions/1"
```

### Get All Transactions (查询所有交易示例)

```bash
curl "http://localhost:8080/api/transactions"
```     




