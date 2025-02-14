# Pod 多容器模式说明

## 1. 常见的多容器模式

### 1.1 Sidecar 模式
```yaml
spec:
  containers:
  - name: account-balance-app    # 主容器
    image: account-balance-app:latest
    
  - name: log-collector         # 日志收集 sidecar
    image: fluentd:latest
    volumeMounts:
    - name: log-volume
      mountPath: /var/log
```

### 1.2 Ambassador 模式
```yaml
spec:
  containers:
  - name: account-balance-app    # 主应用容器
    image: account-balance-app:latest
    
  - name: redis-proxy           # Redis 代理容器
    image: redis-proxy:latest
    ports:
    - containerPort: 6379
```

### 1.3 Adapter 模式
```yaml
spec:
  containers:
  - name: account-balance-app    # 主应用容器
    image: account-balance-app:latest
    
  - name: metric-adapter        # 指标适配器
    image: prometheus-adapter:latest
```

## 2. 我们项目中的选择

### 2.1 当前配置
```yaml
# k8s/app/app-deployment.yaml
spec:
  containers:
  - name: account-balance-app    # 单容器模式
    image: account-balance-app:latest
    ports:
    - containerPort: 8080
    env:
    - name: SPRING_DATASOURCE_URL
      value: "jdbc:mysql://mysql:3306/VTMSystem"
```

### 2.2 选择单容器的原因
1. 应用架构简单清晰
2. 便于维护和排障
3. 符合单一职责原则
4. 资源隔离更彻底

## 3. 何时使用多容器

### 3.1 适用场景
1. 需要日志收集
2. 需要服务代理
3. 需要监控适配
4. 需要数据同步

### 3.2 注意事项
1. 容器间通信通过 localhost
2. 共享同一网络命名空间
3. 可以共享存储卷
4. 生命周期绑定

## 4. 最佳实践

### 4.1 设计原则
1. 保持容器职责单一
2. 避免不必要的容器
3. 合理使用资源限制
4. 正确处理容器依赖

### 4.2 监控建议
1. 监控所有容器状态
2. 收集各容器日志
3. 跟踪容器资源使用
4. 设置合适的健康检查 