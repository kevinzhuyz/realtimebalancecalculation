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
3. 确保 kubectl 可用：
```bash
kubectl version
```

### 2.2 创建命名空间
```bash
kubectl create namespace account-balance
```

### 2.3 部署存储
1. 创建 PV：
```yaml
# k8s/storage/mysql-pv.yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: mysql-pv
spec:
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: "/mnt/data"
```

2. 创建 PVC：
```yaml
# k8s/storage/mysql-pvc.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pv-claim
  namespace: account-balance
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

3. 应用存储配置：
```bash
kubectl apply -f k8s/storage/mysql-pv.yaml
kubectl apply -f k8s/storage/mysql-pvc.yaml
```

### 2.4 部署 MySQL
```yaml
# k8s/mysql/mysql-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
  namespace: account-balance
spec:
  selector:
    matchLabels:
      app: mysql
  template:
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        env:
        - name: MYSQL_ROOT_PASSWORD
          value: "123456"
        - name: MYSQL_DATABASE
          value: "VTMSystem"
        ports:
        - containerPort: 3306
        volumeMounts:
        - name: mysql-persistent-storage
          mountPath: /var/lib/mysql
      volumes:
      - name: mysql-persistent-storage
        persistentVolumeClaim:
          claimName: mysql-pv-claim
---
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: account-balance
spec:
  ports:
  - port: 3306
  selector:
    app: mysql
```

### 2.5 部署 Redis
```yaml
# k8s/redis/redis-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  namespace: account-balance
spec:
  selector:
    matchLabels:
      app: redis
  template:
    spec:
      containers:
      - name: redis
        image: redis:latest
        ports:
        - containerPort: 6379
---
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: account-balance
spec:
  ports:
  - port: 6379
  selector:
    app: redis
```

### 2.6 部署应用
1. 构建应用镜像：
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

```bash
docker build -t account-balance-app:latest .
```

2. 部署应用：
```yaml
# k8s/app/app-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-balance-app
  namespace: account-balance
spec:
  replicas: 2
  selector:
    matchLabels:
      app: account-balance-app
  template:
    spec:
      containers:
      - name: account-balance-app
        image: account-balance-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: jdbc:mysql://mysql:3306/VTMSystem?useSSL=false
        - name: SPRING_DATASOURCE_USERNAME
          value: root
        - name: SPRING_DATASOURCE_PASSWORD
          value: "123456"
        - name: SPRING_DATA_REDIS_HOST
          value: redis
```

### 2.7 配置 Ingress
1. 安装 Ingress Controller：
```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
```

2. 配置 Ingress 规则：
```yaml
# k8s/ingress-nginx/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: account-balance-ingress
  namespace: account-balance
spec:
  ingressClassName: nginx
  rules:
  - http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: account-balance-app
            port:
              number: 8080
```

### 2.8 配置 HPA
1. 部署 metrics-server：
```yaml
# k8s/metrics-server/metrics-server.yaml
# ... metrics-server 配置 ...

# k8s/metrics-server/metrics-server-api.yaml
# ... metrics API 配置 ...
```

2. 配置 HPA：
```yaml
# k8s/app/app-hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: account-balance-app-hpa
  namespace: account-balance
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: account-balance-app
  minReplicas: 2
  maxReplicas: 5
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

## 3. 验证部署

### 3.1 检查所有组件
```bash
# 检查所有 Pod
kubectl get pods -n account-balance

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
kubectl exec -it $(kubectl get pod -l app=redis -n account-balance -o jsonpath='{.items[0].metadata.name}') -n account-balance -- redis-cli ping
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