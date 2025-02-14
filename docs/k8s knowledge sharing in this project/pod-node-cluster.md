# Pod、Node 和 Cluster 的区别与联系

## 1. 基本概念

### 1.1 Pod（豆荚）
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: account-balance-app
spec:
  containers:
  - name: account-balance-app
    image: account-balance-app:latest
```
- 最小的部署单位
- 包含一个或多个容器
- 共享网络和存储
- 生命周期相对短暂

### 1.2 Node（节点）
```yaml
apiVersion: v1
kind: Node
metadata:
  name: worker-1
status:
  capacity:
    cpu: "4"
    memory: "8Gi"
```
- 工作机器，可以是物理机或虚拟机
- 运行多个 Pod
- 提供计算资源
- 由 Master 节点管理

### 1.3 Cluster（集群）
```yaml
# 集群配置示例
apiVersion: kubeadm.k8s.io/v1beta3
kind: ClusterConfiguration
metadata:
  name: production-cluster
```
- 由多个 Node 组成
- 包含 Master 和 Worker 节点
- 统一的资源管理
- 提供高可用性

## 2. 层级关系

### 2.1 包含关系
```
Cluster
  ├── Node 1 (Master)
  │   ├── Pod A
  │   └── Pod B
  ├── Node 2 (Worker)
  │   ├── Pod C
  │   └── Pod D
  └── Node 3 (Worker)
      ├── Pod E
      └── Pod F
```

### 2.2 资源分配
- Cluster 管理整体资源
- Node 提供计算资源
- Pod 消耗资源

## 3. 功能特点

### 3.1 Pod 特点
- 共享网络命名空间
- 共享存储卷
- 原子调度单位
- 可以横向扩展

### 3.2 Node 特点
- 提供运行环境
- 管理本地容器
- 报告状态信息
- 执行 Pod 调度

### 3.3 Cluster 特点
- 统一管理
- 负载均衡
- 服务发现
- 自动扩缩容

## 4. 实际应用

### 4.1 Pod 应用
```yaml
# 我们项目的 Pod 示例
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-balance-app
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: account-balance-app
```

### 4.2 Node 管理
```bash
# 查看节点状态
kubectl get nodes
kubectl describe node worker-1
```

### 4.3 Cluster 运维
```bash
# 集群管理命令
kubectl cluster-info
kubectl get componentstatuses
```

## 5. 最佳实践

### 5.1 Pod 设计
- 单一职责原则
- 合理的资源请求
- 适当的健康检查
- 正确的重启策略

### 5.2 Node 规划
- 合理的资源配置
- 节点标签管理
- 维护策略
- 监控告警

### 5.3 Cluster 管理
- 高可用配置
- 资源规划
- 安全策略
- 备份恢复 