# Docker 镜像和 Pod 的工作机制

## 1. 基本概念

### 1.1 Docker 镜像
- 应用程序的打包格式
- 包含运行应用所需的所有依赖
- 不可变的只读模板

### 1.2 Kubernetes Pod
- K8s 中最小的部署单元
- 可以包含一个或多个容器
- 共享网络和存储空间

## 2. 工作流程

### 2.1 Pod 创建过程
```yaml
# app-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-balance-app
spec:
  replicas: 2    # 创建 2 个 Pod 副本
  template:
    spec:
      containers:
      - name: account-balance-app
        image: account-balance-app:latest    # 指定使用的镜像
```

1. Deployment 控制器创建 Pod
2. Pod 调度到某个节点
3. Kubelet 使用指定镜像创建容器

### 2.2 镜像拉取策略
```yaml
spec:
  containers:
  - name: account-balance-app
    image: account-balance-app:latest
    imagePullPolicy: Never    # 本地镜像策略
```

## 3. 实际应用场景

### 3.1 应用服务 Pod
```yaml
# 应用服务的 Pod 配置
spec:
  containers:
  - name: account-balance-app
    image: account-balance-app:latest
    ports:
    - containerPort: 8080
    env:
    - name: SPRING_DATASOURCE_URL
      value: "jdbc:mysql://mysql:3306/VTMSystem"
```

### 3.2 数据库服务 Pod
```yaml
# MySQL Pod 配置
spec:
  containers:
  - name: mysql
    image: mysql:8.0
    ports:
    - containerPort: 3306
    volumeMounts:
    - name: mysql-persistent-storage
      mountPath: /var/lib/mysql
```

### 3.3 缓存服务 Pod
```yaml
# Redis Pod 配置
spec:
  containers:
  - name: redis
    image: redis:latest
    ports:
    - containerPort: 6379
```

## 4. Pod 和镜像的关系

### 4.1 生命周期关系
1. **Pod 创建**：
   - 调度到节点
   - 拉取指定镜像
   - 创建容器

2. **Pod 运行**：
   - 容器基于镜像运行
   - 处理业务请求
   - 维护应用状态

3. **Pod 销毁**：
   - 停止容器
   - 清理资源
   - 镜像保持不变

### 4.2 资源管理
```yaml
spec:
  containers:
  - name: account-balance-app
    resources:
      requests:
        memory: "256Mi"
        cpu: "200m"
      limits:
        memory: "512Mi"
        cpu: "500m"
```

## 5. 最佳实践

### 5.1 镜像配置
1. 使用具体的镜像版本标签
2. 配置合适的拉取策略
3. 确保镜像安全性

### 5.2 Pod 配置
1. 设置资源限制
2. 配置健康检查
3. 使用合适的重启策略

### 5.3 高可用策略
1. 使用多副本部署
2. 配置 Pod 反亲和性
3. 实施滚动更新 