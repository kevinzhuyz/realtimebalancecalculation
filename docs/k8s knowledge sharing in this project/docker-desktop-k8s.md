# Docker Desktop Kubernetes 设置指南

## 1. 启用 Kubernetes

### 1.1 基本设置
1. 打开 Docker Desktop
2. 点击 Settings（设置）
3. 选择 Kubernetes
4. 勾选 "Enable Kubernetes"（启用 Kubernetes）
5. 点击 "Apply & Restart"（应用并重启）

### 1.2 资源配置
```yaml
# 推荐的资源配置
Resources:
  CPU: 4 cores
  Memory: 8GB
  Disk: 60GB
```

## 2. 选择原因

### 2.1 Docker Desktop 内置 Kubernetes 的优势
- 开发环境一键部署
- 与 Docker 完美集成
- 无需额外配置
- 适合本地开发和测试

### 2.2 对比其他选项
1. **Kubeadm**
   - 适合生产环境
   - 配置复杂
   - 需要额外的系统资源

2. **Kind**
   - 需要 Docker 账号
   - 额外的配置步骤
   - 资源消耗可能更大

3. **Minikube**
   - 需要虚拟机支持
   - 启动较慢
   - 资源隔离性更好

## 3. 配置步骤

### 3.1 启用步骤
1. 确保 Docker Desktop 已安装最新版本
2. 在设置中启用 Kubernetes
3. 等待 Kubernetes 组件下载和启动
4. 验证集群状态

### 3.2 验证配置
```bash
# 检查集群状态
kubectl cluster-info

# 检查节点
kubectl get nodes

# 检查核心组件
kubectl get pods -n kube-system
```

## 4. 注意事项

### 4.1 资源配置建议
- CPU: 至少 2 核（推荐 4 核）
- 内存: 至少 4GB（推荐 8GB）
- 磁盘空间: 至少 40GB（推荐 60GB）

### 4.2 常见问题
1. 启动失败
   - 检查资源配置
   - 重置 Kubernetes
   - 更新 Docker Desktop

2. 性能问题
   - 调整资源限制
   - 关闭不必要的服务
   - 定期清理资源

## 5. 最佳实践

### 5.1 开发环境配置
- 使用 Docker Desktop 内置 Kubernetes
- 设置合适的资源限制
- 启用必要的插件

### 5.2 资源管理
- 及时清理不用的资源
- 监控资源使用情况
- 定期重启保持性能

### 5.3 开发工具集成
- VS Code Kubernetes 插件
- Kubernetes Dashboard
- Lens 或其他 GUI 工具 