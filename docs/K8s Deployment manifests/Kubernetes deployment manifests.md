# Kubernetes 部署清单

## 1. 前置条件

### 1.1 环境要求
- JDK 23
- Maven 3.8+
- Docker Desktop (启用 Kubernetes)
- kubectl 命令行工具

### 1.2 相关依赖版本
- Spring Boot 3.4.2
- MySQL 8.0
- Redis Latest

## 2. 项目结构
.
├── Dockerfile
└── k8s/
├── app/
│ ├── app-deployment.yaml
│ └── hpa.yaml
├── mysql/
│ └── mysql-deployment.yaml
├── redis/
│ └── redis-deployment.yaml
├── ingress-nginx/
│ └── deploy.yaml
└── ingress.yaml

## 3. 部署步骤

### 3.1 准备工作

```bash:k8s/Kubernetes deployment manifests.md
# 确保Kubernetes集群正在运行
kubectl cluster-info

# 创建命名空间
kubectl create namespace account-balance
```

### 3.2 构建和部署

#### 3.2.1 构建应用
```bash
# 构建应用
mvn clean package -DskipTests

# 构建Docker镜像
docker build -t account-balance-app:latest .
```

#### 3.2.2 部署 MySQL
```bash
# 部署 MySQL
kubectl apply -f k8s/mysql/mysql-deployment.yaml -n account-balance

# 验证 MySQL 部署
kubectl get pods -n account-balance -l app=mysql
```

#### 3.2.3 部署 Redis
```bash
# 部署 Redis
kubectl apply -f k8s/redis/redis-deployment.yaml -n account-balance

# 验证 Redis 部署
kubectl get pods -n account-balance -l app=redis
```

#### 3.2.4 部署应用程序
```bash
# 部署应用
kubectl apply -f k8s/app/app-deployment.yaml -n account-balance

# 验证应用部署
kubectl get pods -n account-balance -l app=account-balance-app
```

#### 3.2.5 配置 HPA
```bash
# 部署 HPA
kubectl apply -f k8s/app/hpa.yaml -n account-balance

# 验证 HPA 配置
kubectl get hpa -n account-balance
```

#### 3.2.6 部署 Ingress-Nginx
```bash
# 部署 Ingress Controller
kubectl apply -f k8s/ingress-nginx/deploy.yaml

# 验证 Ingress Controller 部署
kubectl get pods -n ingress-nginx
```

#### 3.2.7 配置 Ingress 规则
```bash
# 部署 Ingress 规则
kubectl apply -f k8s/ingress.yaml -n account-balance

# 验证 Ingress 配置
kubectl get ingress -n account-balance
```

## 4. 配置说明

### 4.1 应用配置
- 应用副本数：2
- 容器端口：8080
- 资源限制：
  - 请求：CPU 200m，内存 256Mi
  - 限制：CPU 500m，内存 512Mi

### 4.2 HPA 配置
- 最小副本数：2
- 最大副本数：5
- CPU 使用率阈值：70%
- 内存使用率阈值：80%

### 4.3 数据库配置
- MySQL 版本：8.0
- 数据库名：VTMSystem
- 持久化存储：使用 PersistentVolumeClaim
- 默认密码：123456（生产环境建议修改）

### 4.4 Redis 配置
- 最新版本
- 单副本部署
- 端口：6379

### 4.5 Ingress 配置
- 域名：account.local
- 路径：/
- 后端服务：account-balance-app:8080

## 5. 监控和维护

### 5.1 查看状态
```bash
# 查看所有资源
kubectl get all -n account-balance

# 查看 Pod 日志
kubectl logs -f deployment/account-balance-app -n account-balance

# 查看 HPA 状态
kubectl get hpa -n account-balance
```

### 5.2 扩缩容操作
```bash
# 手动扩容
kubectl scale deployment account-balance-app --replicas=3 -n account-balance

# 查看 HPA 自动扩缩容状态
kubectl describe hpa account-balance-app-hpa -n account-balance
```

### 5.3 更新配置
```bash
# 更新部署配置
kubectl apply -f k8s/app/app-deployment.yaml -n account-balance

# 重启部署
kubectl rollout restart deployment account-balance-app -n account-balance
```

## 6. 故障排除

### 6.1 常见问题检查
```bash
# 检查 Pod 状态
kubectl describe pod <pod-name> -n account-balance

# 检查服务状态
kubectl describe service account-balance-app -n account-balance

# 检查 Ingress 状态
kubectl describe ingress account-balance-ingress -n account-balance
```

### 6.2 日志查看
```bash
# 应用日志
kubectl logs -f deployment/account-balance-app -n account-balance

# MySQL 日志
kubectl logs -f deployment/mysql -n account-balance

# Redis 日志
kubectl logs -f deployment/redis -n account-balance

# Ingress Controller 日志
kubectl logs -f deployment/ingress-nginx-controller -n ingress-nginx
```

## 7. 安全考虑
- 所有服务运行在专用命名空间内
- MySQL 使用持久化存储
- 可配置 TLS 证书（在 Ingress 配置中）
- 资源限制防止资源耗尽
- HPA 确保服务可用性和性能

## 8. 备份和恢复
- MySQL 数据使用 PersistentVolume 持久化
- 建议定期备份 MySQL 数据
- 可以使用 kubectl cp 命令备份配置文件

## 9. 注意事项
1. 生产环境部署前：
   - 修改默认密码
   - 配置适当的资源限制
   - 启用 TLS
   - 配置备份策略
   
2. 性能优化：
   - 根据实际负载调整 HPA 配置
   - 监控资源使用情况
   - 适时调整资源限制

3. 安全加固：
   - 使用 Secret 管理敏感信息
   - 定期更新密码
   - 限制网络访问
   - 启用审计日志



