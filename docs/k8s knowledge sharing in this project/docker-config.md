# Docker 配置指南

## 1. 基础配置

### 1.1 配置文件
```json
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
    }
  },
  "experimental": false
}
```

### 1.2 配置说明
- `builder.gc`: 构建缓存垃圾回收
  - `defaultKeepStorage`: 保留的存储空间
  - `enabled`: 启用垃圾回收
- `experimental`: 实验性功能开关

## 2. 存储管理

### 2.1 存储配置
- 默认保留 20GB 存储空间
- 启用自动垃圾回收
- 定期清理未使用的镜像和缓存

### 2.2 优化建议
- 根据实际需求调整存储空间
- 定期运行 `docker system prune`
- 监控磁盘使用情况

## 3. 性能优化

### 3.1 资源限制
```json
{
  "resources": {
    "cpu": 4,
    "memory": "8GB",
    "disk": "60GB"
  }
}
```

### 3.2 建议配置
- CPU: 4 cores
- Memory: 8GB
- Disk: 60GB
- 启用垃圾回收

## 4. 最佳实践

### 4.1 存储管理
- 定期清理未使用的镜像
- 设置合理的存储限制
- 监控存储使用情况

### 4.2 性能优化
- 合理配置资源限制
- 启用构建缓存
- 定期维护 