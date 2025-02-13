# PV 和 PVC 的解释说明

## 1. 基本概念

### 1.1 PV（PersistentVolume）
```yaml
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
- 集群级别的存储资源
- 不属于任何命名空间
- 由管理员创建和管理
- 定义存储的具体实现

### 1.2 PVC（PersistentVolumeClaim）
```yaml
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
- 命名空间级别的资源
- 由用户创建
- 申请存储资源
- 与 Pod 关联使用

## 2. 工作原理

### 2.1 绑定关系
- PVC 向 PV 申请存储空间
- Kubernetes 自动将 PVC 与合适的 PV 绑定
- Pod 通过 PVC 使用存储

### 2.2 生命周期
1. 配置：管理员创建 PV
2. 绑定：用户创建 PVC，自动绑定到 PV
3. 使用：Pod 使用 PVC
4. 释放：删除 PVC 后释放 PV
5. 回收：PV 可被重新使用

## 3. 实际应用

### 3.1 在 Pod 中使用
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
spec:
  template:
    spec:
      containers:
      - name: mysql
        volumeMounts:
        - name: mysql-persistent-storage
          mountPath: /var/lib/mysql
      volumes:
      - name: mysql-persistent-storage
        persistentVolumeClaim:
          claimName: mysql-pv-claim
```

### 3.2 存储类型
- hostPath：本地主机存储
- NFS：网络文件系统
- 云存储：如 AWS EBS、Azure Disk
- 其他分布式存储

## 4. 最佳实践

### 4.1 容量规划
- 根据应用需求设置存储大小
- 预留足够的扩展空间
- 监控存储使用情况

### 4.2 访问模式
- ReadWriteOnce：单节点读写
- ReadOnlyMany：多节点只读
- ReadWriteMany：多节点读写

### 4.3 数据保护
- 定期备份
- 设置存储类型
- 配置回收策略