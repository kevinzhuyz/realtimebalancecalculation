# 账户余额计算系统 - Kubernetes 部署指南

本指南主要说明如何在本地开发环境中使用 Kubernetes 部署账户余额计算系统，同时使用本地的 MySQL 和 Redis 服务。

## 目录结构
```
k8s/
├── docs/                   # 文档目录
│   └── 生产环境部署推荐方案.md
├── app/                    # 应用配置
│   ├── app-deployment.yaml # 应用部署配置
│   └── app-service.yaml    # 应用服务配置
├── configs/               # 配置文件
│   └── app-config.yaml    # 应用配置
└── README.md              # 项目说明
```

## 环境要求

### 1. 基础环境
- Docker Desktop (启用 Kubernetes)
- JDK 21
- Maven 3.x
- MySQL 8.0
- Redis 7.x

### 2. 数据库配置
1. MySQL 配置：
```sql
-- 允许远程访问
CREATE USER 'root'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

2. Redis 配置：
```conf
# redis.conf
bind 0.0.0.0
protected-mode no
requirepass 123456
```

## 部署步骤

### 1. 准备工作
```bash
# 创建命名空间
kubectl create namespace account-balance

# 验证命名空间
kubectl get namespace account-balance
```

### 2. 构建应用
```bash
# 编译项目
mvn package

# 构建Docker镜像
docker build -t account-balance-app:latest .
```

### 3. 部署应用
```bash
# 部署配置
kubectl apply -f k8s/configs/app-config.yaml

# 部署应用
kubectl apply -f k8s/app/app-deployment.yaml
kubectl apply -f k8s/app/app-service.yaml
```
配置 HPA：
创建HPA配置文件：
```yaml
# k8s/app/app-hpa.yaml（配置文件）  
实现HPA配置
```bash
kubectl apply -f k8s/app/app-hpa.yaml -n account-balance 

### 4. 验证部署
```bash
# 查看Pod状态
kubectl get pods -n account-balance

# 查看服务状态
kubectl get svc -n account-balance

# 查看Pod日志
kubectl logs -f deployment/account-balance-app -n account-balance
```

## 访问应用

- 本地Spring Boot应用：http://localhost:8080
- Kubernetes部署应用：http://localhost:30080

## 常用操作命令

### 1. 查看状态
```bash
# 查看所有资源
kubectl get all -n account-balance

# 查看Pod详细信息
kubectl describe pod <pod-name> -n account-balance

# 查看应用日志
kubectl logs -f <pod-name> -n account-balance
```

### 2. 扩缩容
```bash
# 扩展副本数
kubectl scale deployment account-balance-app --replicas=3 -n account-balance

# 查看扩容结果
kubectl get pods -n account-balance
```

### 3. 重启应用
```bash
# 重启Deployment
kubectl rollout restart deployment account-balance-app -n account-balance
```

### 4. 删除部署
```bash
# 删除所有资源
kubectl delete -f k8s/app/app-service.yaml -n account-balance
kubectl delete -f k8s/app/app-deployment.yaml -n account-balance
kubectl delete -f k8s/configs/app-config.yaml -n account-balance

# 删除Docker镜像
docker rmi account-balance-app:latest

# 强制删除Docker镜像（如果普通删除失败）
docker rmi -f account-balance-app:latest

# 删除所有相关镜像（包括中间层镜像）
docker images | grep account-balance-app | awk '{print $3}' | xargs docker rmi -f
```

## 故障排查

### 1. Pod启动失败
- 检查本地MySQL和Redis是否正常运行
- 确认数据库用户名密码配置正确
- 查看Pod详细信息和日志

### 2. 无法访问应用
- 确认Service配置正确
- 检查防火墙设置
- 验证端口映射是否正确

### 3. 数据库连接问题
- 确保MySQL允许远程连接
- 检查host.docker.internal解析
- 验证数据库权限配置

### 4. Maven构建警告
- 检查 pom.xml 中是否存在重复依赖：
  ```bash
  # 常见的重复依赖
  - spring-boot-starter-data-redis
  - spring-boot-starter-cache
  ```
- 解决方法：
  1. 打开 pom.xml 文件
  2. 搜索并删除重复的依赖声明
  3. 只保留一个版本的依赖声明
  4. 重新执行 `mvn clean package`

## 注意事项

1. 本配置仅适用于本地开发环境
2. 生产环境部署请参考 `docs/生产环境部署推荐方案.md`
3. 确保本地MySQL和Redis允许远程连接
4. Windows/macOS环境下host.docker.internal自动解析
5. Linux环境可能需要额外配置host.docker.internal

## 常见问题

1. Q: 为什么选择NodePort而不是LoadBalancer？
   A: 本地开发环境使用NodePort更简单直接，无需额外配置。

2. Q: 如何处理数据持久化？
   A: 本地开发环境直接使用主机的MySQL和Redis，数据自动持久化。

3. Q: 如何确保应用配置正确？
   A: 可以通过查看Pod日志和应用健康检查接口来验证。
