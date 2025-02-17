# Kubernetes 完整部署指南

## 1. 项目结构
```
k8s/
├── app/                    # 应用相关配置
│   ├── app-deployment.yaml # 应用部署配置
│   └── app-hpa.yaml       # 自动扩缩容配置
├── storage/               # 存储相关配置
│   ├── mysql-pv.yaml      # MySQL 持久卷
│   └── mysql-pvc.yaml     # MySQL 持久卷声明
├── metrics-server/        # 指标服务配置
│   ├── metrics-server.yaml     # 指标服务部署
│   └── metrics-server-api.yaml # 指标 API 服务
├── mysql/                 # MySQL 相关配置
│   └── mysql-deployment.yaml   # MySQL 部署配置
├── redis/                 # Redis 相关配置
│   └── redis-deployment.yaml   # Redis 部署配置
└── ingress-nginx/        # Ingress 相关配置
    ├── ingress.yaml           # 应用 Ingress 规则
    └── ingress-controller.yaml # Ingress Controller 配置

```

## 2. 部署步骤

### 2.1 准备工作
1. 安装 Docker Desktop
2. 启用 Kubernetes
3. 重新编译项目，确保项目的jar包是最新的：
```bash
 mvn clean package 


 2.2 创建命名空间和查看命名空间
```bash
kubectl create namespace account-balance
# 查看某个命名空间的详细信息
kubectl describe namespace account-balance
# 查看所有命名空间
kubectl get namespaces
# 查看特定命名空间中的所有资源
kubectl get all -n account-balance

# 查看特定命名空间中的 pods
kubectl get pods -n account-balance

# 查看特定命名空间中的服务
kubectl get services -n account-balance
# 实时监控命名空间变化
kubectl get ns --watch

# 实时监控特定命名空间的 pods
kubectl get pods -n account-balance --watch
```

### 2.3 部署存储
1. 创建 PV配置文件：
```yaml
# k8s/storage/mysql-pv.yaml（配置文件）
部署PV
kubectl apply -f k8s/storage/mysql-pv.yaml
```

2. 创建 PVC配置文件：
```yaml
# k8s/storage/mysql-pvc.yaml（配置文件）
部署PVC
kubectl apply -f k8s/storage/mysql-pvc.yaml
```


### 2.4 部署 MySQL
1. 创建mysql部署配置文件：
```yaml
# k8s/mysql/mysql-deployment.yaml （配置文件）
2. 部署mysql
```bash
kubectl apply -f k8s/mysql/mysql-deployment.yaml -n account-balance
```

### 2.5 部署 Redis
1. 创建redis配置文件：
```yaml
# k8s/redis/redis-deployment.yaml（配置文件）
```
2. 部署redis
```bash
kubectl apply -f k8s/redis/redis-deployment.yaml -n account-balance
```


### 2.6 配置 Ingress
1. 安装 Ingress Controller：
```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
2. 创建配置文件
```yaml
# k8s/ingress-nginx/deploy.yaml（配置文件）
```
3. 部署 Ingress
```bash
kubectl apply -f k8s/ingress-nginx/deploy.yaml
```

2. 配置 Ingress 规则：

1. 创建Ingress规则配置文件：  
```yaml
# k8s/ingress-nginx/ingress.yaml（配置文件）
```
2. 部署 Ingress规则  
```bash
kubectl apply -f k8s/ingress-nginx/ingress.yaml
```

### 2.7 配置 HPA
1. 部署 metrics-server：
1. 创建metrics-server配置文件：   
```yaml
# k8s/metrics-server/metrics-server.yaml（配置文件）
# ... metrics-server 配置 ...
部署metrics-server
```bash
kubectl apply -f k8s/metrics-server/metrics-server.yaml
```

2. 创建metrics API配置文件：
```yaml
# k8s/metrics-server/metrics-server-api.yaml（配置文件）
# ... metrics API 配置 ...
部署metrics API 
```bash
kubectl apply -f k8s/metrics-server/metrics-server-api.yaml

3. 配置 HPA：
创建HPA配置文件：
```yaml
# k8s/app/app-hpa.yaml（配置文件）  
实现HPA配置
```bash
kubectl apply -f k8s/app/app-hpa.yaml -n account-balance  
```
### 2.8部署应用
1. 创建Dockerfile：

```dockerfile
  # Dockerfile（配置文件）

```
2. 构建应用镜像：
```bash
docker build -t account-balance-app:latest .
```

3. 部署应用：
```bash

kubectl apply -f k8s/app/app-deployment.yaml -n account-balance
```
# 重启整个部署(如果应用部署完后有问题，需要重启整个部署)
kubectl rollout restart deployment account-balance-app -n account-balance

## 3. 验证部署

### 3.1 检查所有组件
```bash
# 检查所有 Pod
kubectl get pods -n account-balance
# 如何进入任意一个运行pod的命令
kubectl exec -it -n account-balance $(kubectl get pods -n account-balance -l app=account-balance-app -o jsonpath='{.items[0].metadata.name}') -- bash

# 检查所有服务
kubectl get svc -n account-balance

# 检查 Ingress
kubectl get ingress -n account-balance

# 检查 HPA
kubectl get hpa -n account-balance
```

### 3.2 验证数据库连接
```bash
# 测试 MySQL 连接
kubectl exec -it $(kubectl get pod -l app=mysql -n account-balance -o jsonpath='{.items[0].metadata.name}') -n account-balance -- mysql -u root -p123456 -e "SELECT 1"

# 测试 Redis 连接
kubectl exec -it $(kubectl get pod -l app=redis -n account-balance -o jsonpath='{.items[0].metadata.name}') -n account-balance -- redis-cli -a 123456
```

### 3.3 验证应用访问
```bash
# 通过 Ingress 访问
curl http://localhost/
```

## 4. 监控和维护

### 4.1 查看日志
```bash
# 应用日志
kubectl logs -f -l app=account-balance-app -n account-balance

# MySQL 日志
kubectl logs -f -l app=mysql -n account-balance

# Redis 日志
kubectl logs -f -l app=redis -n account-balance
```

### 4.2 资源监控
```bash
# 节点资源使用
kubectl top nodes

# Pod 资源使用
kubectl top pods -n account-balance
```

### 4.3 扩缩容监控
```bash
# 监控 HPA
kubectl get hpa -n account-balance -w
```

## 5. 故障排除

### 5.1 Pod 启动问题
```bash
# 查看 Pod 详情
kubectl describe pod <pod-name> -n account-balance

# 查看 Pod 日志
kubectl logs <pod-name> -n account-balance
```

### 5.2 存储问题
```bash
# 检查 PV 状态
kubectl get pv

# 检查 PVC 状态
kubectl get pvc -n account-balance
```

### 5.3 网络问题
```bash
# 检查 Service
kubectl get svc -n account-balance

# 检查 Endpoints
kubectl get endpoints -n account-balance
```

## 6. 最佳实践
1. 定期备份 MySQL 数据
2. 监控资源使用情况
3. 及时更新镜像和配置
4. 保持日志监控
5. 定期检查 HPA 状态