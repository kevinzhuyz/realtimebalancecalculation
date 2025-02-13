# Redis 和 MySQL 数据库逻辑处理关系说明

## 1. 数据模型关系

### 1.1 MySQL 存储的数据
- 账户信息（Account）
- 用户信息（User）
- 交易记录（Transaction）
- 所有业务的持久化数据

### 1.2 Redis 缓存的数据
```java
// 缓存键前缀定义
private static final String ACCOUNT_CACHE_KEY_PREFIX = "account:";
private static final String USER_CACHE_KEY_PREFIX = "user:";
private static final String TRANSACTION_CACHE_KEY_PREFIX = "transaction:";
```

## 2. 业务场景分析

### 2.1 账户余额查询
```java
// AccountServiceImpl.java
public Account getAccountById(Long id) {
    String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + id;
    
    // 1. 优先查询 Redis 缓存
    Account account = cacheService.get(cacheKey, Account.class);
    if (account != null) {
        log.info("Account found in cache: {}", cacheKey);
        return account;
    }
    
    // 2. 缓存未命中，查询 MySQL
    log.info("Account not found in cache, fetching from database: {}", id);
    account = accountRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Account not found"));
        
    // 3. 将 MySQL 查询结果放入 Redis 缓存
    cacheService.set(cacheKey, account, CACHE_TIMEOUT, TimeUnit.MINUTES);
    return account;
}
```

### 2.2 交易处理流程
```java
// TransactionServiceImpl.java
@Transactional
public Transaction createTransaction(CreateTransactionRequest request) {
    // 1. 获取分布式锁
    String lockKey = LOCK_KEY_PREFIX + request.getSourceAccountId();
    boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
    
    try {
        // 2. 先写入 MySQL
        Transaction transaction = transactionRepository.save(newTransaction);
        
        // 3. 更新账户余额（MySQL）
        balanceService.updateBalance(request.getSourceAccountId(), amount.negate());
        
        // 4. 更新 Redis 缓存
        String cacheKey = TRANSACTION_CACHE_KEY_PREFIX + transaction.getId();
        cacheService.set(cacheKey, transaction, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        // 5. 失效相关账户缓存
        cacheService.delete(ACCOUNT_CACHE_KEY_PREFIX + request.getSourceAccountId());
        
        return transaction;
    } finally {
        // 6. 释放分布式锁
        if (locked) {
            redisTemplate.delete(lockKey);
        }
    }
}
```

## 3. 缓存策略

### 3.1 缓存写入策略
1. **写入 MySQL 后更新缓存**：
```java
// 更新账户信息
public Account updateAccount(Account account) {
    // 1. 先更新 MySQL
    Account updatedAccount = accountRepository.save(account);
    
    // 2. 再更新 Redis 缓存
    String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + updatedAccount.getId();
    cacheService.set(cacheKey, updatedAccount, CACHE_TIMEOUT, TimeUnit.MINUTES);
    
    return updatedAccount;
}
```

### 3.2 缓存失效策略
1. **定时失效**：
```java
// 缓存过期时间设置
private static final long CACHE_TIMEOUT = 30; // 30分钟
```

2. **主动失效**：
```java
// 数据变更时主动失效缓存
public void invalidateCache(Long accountId) {
    String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + accountId;
    cacheService.delete(cacheKey);
}
```

## 4. 数据一致性保证

### 4.1 分布式锁
```java
// 使用 Redis 实现分布式锁
String lockKey = LOCK_KEY_PREFIX + accountId;
boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
```

### 4.2 事务处理
```java
@Transactional
public void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
    // 1. MySQL 事务处理
    Account sourceAccount = accountRepository.findByIdWithLock(sourceAccountId);
    Account targetAccount = accountRepository.findByIdWithLock(targetAccountId);
    
    // 2. 更新 MySQL 数据
    sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
    targetAccount.setBalance(targetAccount.getBalance().add(amount));
    
    accountRepository.save(sourceAccount);
    accountRepository.save(targetAccount);
    
    // 3. 失效 Redis 缓存
    cacheService.delete(ACCOUNT_CACHE_KEY_PREFIX + sourceAccountId);
    cacheService.delete(ACCOUNT_CACHE_KEY_PREFIX + targetAccountId);
}
```

## 5. 性能优化

### 5.1 缓存预热
```java
@PostConstruct
public void init() {
    // 系统启动时加载热点数据到缓存
    List<Account> accounts = accountRepository.findAll();
    for (Account account : accounts) {
        String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + account.getId();
        cacheService.set(cacheKey, account, CACHE_TIMEOUT, TimeUnit.MINUTES);
    }
}
```

### 5.2 批量操作优化
```java
// 批量获取账户信息
public List<Account> getAccountsByIds(List<Long> ids) {
    List<Account> accounts = new ArrayList<>();
    List<String> missedIds = new ArrayList<>();
    
    // 1. 批量查询缓存
    for (Long id : ids) {
        String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + id;
        Account account = cacheService.get(cacheKey, Account.class);
        if (account != null) {
            accounts.add(account);
        } else {
            missedIds.add(id);
        }
    }
    
    // 2. 查询未命中的数据
    if (!missedIds.isEmpty()) {
        List<Account> dbAccounts = accountRepository.findAllById(missedIds);
        accounts.addAll(dbAccounts);
        
        // 3. 放入缓存
        for (Account account : dbAccounts) {
            String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + account.getId();
            cacheService.set(cacheKey, account, CACHE_TIMEOUT, TimeUnit.MINUTES);
        }
    }
    
    return accounts;
}
``` 