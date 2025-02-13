# Pod 副本关系说明

## 1. 副本的基本概念

### 1.1 配置定义
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-balance-app
spec:
  replicas: 2    # 定义两个副本
  template:
    spec:
      containers:
      - name: account-balance-app
        image: account-balance-app:latest
```

### 1.2 副本特点
- 完全相同的配置
- 运行相同的应用
- 独立的运行环境
- 负载均衡的请求处理

## 2. 副本的作用

### 2.1 高可用性
- 一个 Pod 故障时，另一个继续服务
- 自动故障转移
- 确保服务持续可用

### 2.2 负载均衡
```yaml
apiVersion: v1
kind: Service
metadata:
  name: account-balance-app
spec:
  selector:
    app: account-balance-app  # 选择所有副本
  ports:
  - port: 8080
```
- 请求自动分发到不同副本
- 平均分配负载
- 提高系统吞吐量

## 3. 副本管理

### 3.1 自动伸缩
- 保持指定数量的副本
- 故障时自动重启
- 滚动更新时保持服务可用

### 3.2 数据一致性
- 所有副本访问相同的数据库
- 无状态设计
- 通过外部存储保持数据一致 