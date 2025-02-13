# Kubernetes ResourceQuota 详解

## 1. ResourceQuota 概述

ResourceQuota 用于限制命名空间的资源使用，确保集群资源被合理分配和使用。

### 1.1 基本配置
```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: account-quota
  namespace: account-balance
spec:
  hard:
    cpu: "4"            # CPU 总限制
    memory: 4Gi         # 内存总限制
```

## 2. 可限制的资源类型

### 2.1 计算资源配额
```yaml
spec:
  hard:
    # CPU 资源
    cpu: "4"                    # 总 CPU 限制（4 核）
    requests.cpu: "2"           # CPU 请求总量
    limits.cpu: "4"             # CPU 限制总量
    
    # 内存资源
    memory: 4Gi                 # 总内存限制（4 GB）
    requests.memory: 2Gi        # 内存请求总量
    limits.memory: 4Gi          # 内存限制总量
```

### 2.2 对象数量配额
```yaml
spec:
  hard:
    pods: "20"                  # Pod 数量限制
    services: "10"              # Service 数量限制
    secrets: "10"               # Secret 数量限制
    configmaps: "10"            # ConfigMap 数量限制
    persistentvolumeclaims: "4" # PVC 数量限制
```

## 3. 实际应用

### 3.1 我们项目中的配置
```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: account-quota
  namespace: account-balance
spec:
  hard:
    # 计算资源限制
    cpu: "4"                    # 所有 Pod 的 CPU 总量不超过 4 核
    memory: 4Gi                 # 所有 Pod 的内存总量不超过 4GB
    
    # 对象数量限制
    pods: "10"                  # 最多运行 10 个 Pod
    services: "5"               # 最多创建 5 个 Service
    
    # 存储资源限制
    requests.storage: "500Gi"   # 存储请求总量
```

### 3.2 与 Pod 资源限制的关系
```yaml
# Pod 资源配置示例
spec:
  containers:
  - name: account-balance-app
    resources:
      requests:
        cpu: "200m"        # 每个 Pod 请求 0.2 核 CPU
        memory: "256Mi"    # 每个 Pod 请求 256MB 内存
      limits:
        cpu: "500m"        # 每个 Pod 最多使用 0.5 核 CPU
        memory: "512Mi"    # 每个 Pod 最多使用 512MB 内存
```

## 4. 监控和管理

### 4.1 查看配额使用情况
```bash
# 查看 ResourceQuota 状态
kubectl describe resourcequota account-quota -n account-balance

# 查看当前使用量
kubectl get resourcequota account-quota -n account-balance --output=yaml
```

### 4.2 常见问题处理
1. 超出配额：新建资源会失败
2. 资源竞争：可能影响应用扩展
3. 配额调整：需要评估实际使用情况

## 5. 最佳实践

### 5.1 配额设置建议
- 根据应用实际需求设置
- 预留足够的扩展空间
- 定期review和调整

### 5.2 监控和告警
- 设置资源使用率告警
- 监控配额使用趋势
- 及时调整配额设置 