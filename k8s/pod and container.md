# Pod 和 Container 的区别与联系

## 1. Container（容器）

### 1.1 容器定义
容器是一个轻量级的、独立的软件包，包含运行应用程序所需的所有内容。

### 1.2 容器特点
- 有自己的文件系统
- 独立的 CPU 和内存分配
- 独立的进程空间
- 可以独立运行和管理

### 1.3 示例（我们的项目）
```dockerfile
# Dockerfile
FROM azul/zulu-openjdk:21-latest    # 基础镜像
WORKDIR /app                         # 工作目录
COPY target/*.jar app.jar           # 应用文件
EXPOSE 8080                         # 端口
ENTRYPOINT ["java","-jar","app.jar"] # 启动命令
```

## 2. Pod（豆荚）

### 2.1 Pod 定义
Pod 是 Kubernetes 中最小的可部署单位，可以包含一个或多个容器。

### 2.2 Pod 特点
- 共享网络命名空间
- 共享存储卷
- 共享 IP 地址和端口空间
- 作为容器的管理单位

### 2.3 示例（我们的项目）
```yaml
# app-deployment.yaml
spec:
  containers:
  - name: account-balance-app        # 容器名称
    image: account-balance-app:latest # 容器镜像
    ports:
    - containerPort: 8080           # 容器端口
    env:                            # 环境变量
    - name: SPRING_DATASOURCE_URL
      value: jdbc:mysql://mysql:3306/VTMSystem?useSSL=false
```

## 3. 区别与联系

### 3.1 关系
- Pod 是容器的包装器（wrapper）
- Pod 管理容器的生命周期
- 一个 Pod 可以包含多个容器
- 容器是实际运行应用的地方

### 3.2 资源管理
```yaml
resources:                         # 资源限制
  requests:
    cpu: "200m"
    memory: "256Mi"
  limits:
    cpu: "500m"
    memory: "512Mi"
```

### 3.3 生命周期管理
- Pod 提供：
  - 容器的生命周期管理
  - 健康检查机制
  - 重启策略
  - 资源限制策略

### 3.4 网络管理
- Pod 内的容器：
  - 共享同一个网络命名空间
  - 可以通过 localhost 互相访问
  - 共享 Pod 的 IP 地址

## 4. 实际应用场景

### 4.1 单容器 Pod
- 最常见的使用场景
- 我们的项目就是这种情况
- Pod 管理单个 Spring Boot 应用容器

### 4.2 多容器 Pod
- 主应用容器
- 辅助容器（边车模式）
- 初始化容器

### 4.3 部署策略
```yaml
spec:
  replicas: 2                      # Pod 副本数
  template:
    spec:
      containers:                  # 容器定义
      - name: account-balance-app
```

## 5. 最佳实践

### 5.1 容器设计原则
- 单一职责
- 可重用性
- 资源独立

### 5.2 Pod 设计原则
- 合理的资源限制
- 适当的健康检查
- 合理的重启策略

### 5.3 注意事项
1. 容器应该是无状态的
2. Pod 是临时实体，随时可能被重新调度
3. 合理使用资源限制
4. 正确配置健康检查
5. 做好日志收集 