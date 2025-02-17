# Kubernetes Pod 删除指南

## 1. 删除方式

### 1.1 直接删除特定 Pod
```bash
# 语法
kubectl delete pod <pod-name> -n <namespace>


# 示例：删除特定的应用 Pod
kubectl delete pod account-balance-app-6fb6789c-72xzd -n account-balance
```

### 1.2 使用标签选择器批量删除
```bash
# 删除带有特定标签的所有 Pod
kubectl delete pod -l app=account-balance-app -n account-balance
```

### 1.3 重启 Deployment（推荐）
```bash
# 重启整个部署
kubectl rollout restart deployment account-balance-app -n account-balance
```

### 1.4 缩容扩容方式
```bash
# 缩容到 0
kubectl scale deployment account-balance-app --replicas=0 -n account-balance

# 扩容到指定副本数
kubectl scale deployment account-balance-app --replicas=2 -n account-balance
```

## 2. 各种方式的影响

### 2.1 直接删除 Pod
- Pod 会被立即终止
- Deployment 控制器会自动创建新的 Pod
- 可能导致短暂的服务中断

### 2.2 标签选择器删除
- 批量删除符合条件的 Pod
- 适合需要重新创建所有 Pod 的场景
- 会同时影响多个 Pod

### 2.3 重启 Deployment
- 触发滚动更新
- 逐个替换旧的 Pod
- 最小化服务中断
- 保持期望的副本数

### 2.4 缩容扩容
- 先终止所有 Pod
- 然后重新创建
- 会导致服务完全中断
- 适合完全重置的场景

## 3. 最佳实践

### 3.1 选择合适的删除方式
1. 日常重启：使用 rollout restart
2. 单个问题 Pod：直接删除特定 Pod
3. 完全重置：使用缩容扩容
4. 批量操作：使用标签选择器

### 3.2 删除前检查
```bash
# 检查 Pod 状态
kubectl get pods -n account-balance

# 检查 Pod 详情
kubectl describe pod <pod-name> -n account-balance

# 查看 Pod 日志
kubectl logs <pod-name> -n account-balance
```

### 3.3 删除后验证
```bash
# 验证新 Pod 创建
kubectl get pods -n account-balance

# 检查新 Pod 状态
kubectl describe pod <new-pod-name> -n account-balance

# 验证服务可用性
kubectl get svc -n account-balance
```

## 4. 注意事项

### 4.1 数据持久性
- 确保重要数据已持久化
- 检查 PVC 状态
- 注意临时数据可能丢失

### 4.2 服务影响
- 评估对其他服务的影响
- 考虑业务高峰期
- 准备回滚方案

### 4.3 监控和告警
- 监控新 Pod 的健康状态
- 关注服务可用性指标
- 设置适当的告警阈值