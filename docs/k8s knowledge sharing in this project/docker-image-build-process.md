# Docker 镜像构建过程文档

## 1. 前置条件

### 1.1 环境要求
- JDK 21
- Maven 3.8+
- Docker Desktop
- Spring Boot 3.2.2

### 1.2 相关文件
- pom.xml：Maven 项目配置文件
- Dockerfile：Docker 镜像构建配置文件
- k8s/app/app-deployment.yaml：Kubernetes 部署配置文件

## 2. Maven 构建阶段

### 2.1 Maven 配置
```xml
<properties>
    <java.version>21</java.version>
    <maven.test.skip>true</maven.test.skip>
    <skipTests>true</skipTests>
</properties>
```

### 2.2 构建步骤
```bash
mvn clean package -DskipTests
```

构建过程：
1. clean：清理之前的构建文件
2. compile：编译 Java 源代码
3. process-resources：处理资源文件
4. package：打包成 JAR 文件
   - 输出位置：target/*.jar
   - 包含：编译后的类文件、资源文件、依赖项

## 3. Docker 镜像构建阶段

### 3.1 Dockerfile 解析
```dockerfile
FROM azul/zulu-openjdk:21-latest
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

### 3.2 构建命令
```bash
docker build -t account-balance-app:latest .
```

### 3.3 构建过程详解

1. **基础镜像层**
   - 使用 azul/zulu-openjdk:21-latest 作为基础镜像
   - 提供完整的 JDK 21 运行环境
   - 包含基础系统工具和 Java 运行时

2. **工作目录层**
   - 创建并设置 /app 目录
   - 后续命令都在此目录下执行
   - 提供统一的文件操作环境

3. **应用层**
   - 复制本地构建的 JAR 文件
   - 重命名为 app.jar
   - 包含完整的 Spring Boot 应用

4. **配置层**
   - 声明应用将使用 8080 端口
   - 文档性质，实际端口映射在运行时指定
   - 便于容器编排和服务发现

5. **启动命令层**
   - 设置容器启动时执行的命令
   - 使用 java -jar 运行 Spring Boot 应用
   - 容器启动即应用启动

## 4. 镜像验证

### 4.1 检查镜像
```bash
# 查看构建的镜像
docker images account-balance-app:latest

# 查看镜像详细信息
docker inspect account-balance-app:latest
```

### 4.2 测试运行
```bash
# 本地测试运行
docker run -p 8080:8080 account-balance-app:latest

# 检查容器日志
docker logs <container-id>
```

## 5. Kubernetes 部署准备

### 5.1 镜像准备
```bash
# 标记镜像
docker tag account-balance-app:latest registry.cn-hangzhou.aliyuncs.com/your-namespace/account-balance-app:latest

# 推送镜像
docker push registry.cn-hangzhou.aliyuncs.com/your-namespace/account-balance-app:latest
```

### 5.2 部署配置
- 部署文件：k8s/app/app-deployment.yaml
- 资源配置：
  - CPU 请求：200m，限制：500m
  - 内存请求：256Mi，限制：512Mi
- 副本数：2
- 服务端口：8080

## 6. 优化建议

### 6.1 构建优化
- 使用多阶段构建减小镜像大小
- 优化 Maven 依赖
- 配置适当的 JVM 参数

### 6.2 安全优化
- 使用非 root 用户运行应用
- 移除不必要的系统工具
- 定期更新基础镜像

### 6.3 性能优化
- 配置合适的 JVM 内存参数
- 使用 Spring Boot 的 layered jars
- 优化应用配置

## 7. 注意事项
1. 确保 Maven 构建成功再进行 Docker 构建
2. 检查 Dockerfile 中的基础镜像版本
3. 验证应用健康检查接口
4. 注意资源限制配置
5. 确保镜像仓库配置正确
