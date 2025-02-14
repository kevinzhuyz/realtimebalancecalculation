# Account Balance Calculation System

## 项目简介
Account Balance Calculation System 是一个基于 Spring Boot 的实时账户余额计算系统，提供账户管理、交易处理和余额计算等功能。系统采用分布式架构设计，使用 Redis 实现分布式缓存和锁，保证交易一致性和可靠性。

## 技术栈
- Java 23
- Spring Boot 3.2.2
- Spring Data JPA
- MySQL 8.0
- Redis
- Maven
- JUnit 5
- Mockito
- MapStruct
- Lombok
- Prometheus (监控)
- Micrometer (指标收集)

## 主要功能
1. 用户管理
   - 用户注册
   - 用户信息查询和更新

2. 账户管理
   - 创建账户
   - 查询账户信息
   - 账户余额更新

3. 交易处理
   - 存款
   - 取款
   - 转账
   - 交易记录查询

4. 系统监控
   - Redis 缓存监控
   - 性能指标收集
   - Prometheus 集成

## 系统架构

### 整体架构图
```
                                    [负载均衡 SLB]
                                          │
                                          ▼
                                [Ingress Controller]
                                          │
                    ┌─────────────────────┴─────────────────────┐
                    ▼                     ▼                     ▼
            [Service Pod 1]        [Service Pod 2]        [Service Pod 3]
                    │                     │                     │
                    └─────────────────────┼─────────────────────┘
                                         │
                    ┌─────────────────────┴─────────────────────┐
                    ▼                     ▼                     ▼
            [Redis Cluster]         [MySQL RDS]           [Prometheus]
```

### 分层架构图
```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ├─ REST Controllers                                        │
│  └─ API Documentation                                       │
├─────────────────────────────────────────────────────────────┤
│                     Business Layer                          │
│  ├─ Service Implementations                                 │
│  ├─ Transaction Management                                  │
│  └─ Business Logic                                          │
├─────────────────────────────────────────────────────────────┤
│                     Persistence Layer                       │
│  ├─ Repositories                                           │
│  ├─ Cache Management                                       │
│  └─ Data Access Objects                                    │
├─────────────────────────────────────────────────────────────┤
│                     Infrastructure Layer                    │
│  ├─ Database (MySQL)                                       │
│  ├─ Cache (Redis)                                          │
│  └─ Monitoring (Prometheus)                                │
└─────────────────────────────────────────────────────────────┘
```

### 设计理由

1. **微服务架构**
   - 采用微服务架构，便于独立开发、部署和扩展
   - 服务间通过 RESTful API 进行通信
   - 使用 Spring Boot 框架，简化开发和配置

2. **高可用设计**
   - 多实例部署，确保服务高可用
   - 使用 SLB 进行负载均衡
   - Redis 集群提供缓存服务
   - MySQL 主从复制保证数据库高可用

3. **数据一致性**
   - 使用分布式锁保证并发安全
   - 事务管理确保数据一致性
   - 缓存与数据库同步机制

4. **性能优化**
   - Redis 缓存减少数据库访问
   - 连接池优化数据库连接
   - 异步处理非核心业务

5. **安全性设计**
   - 密码加密存储
   - 接口访问控制
   - 敏感数据脱敏

6. **可扩展性**
   - 水平扩展支持
   - 模块化设计
   - 接口标准化

7. **监控和运维**
   - Prometheus 监控指标
   - 日志集中管理
   - 告警机制

8. **分层设计优势**
   - 展现层：处理 HTTP 请求和响应
   - 业务层：实现核心业务逻辑
   - 持久层：处理数据存储和缓存
   - 基础设施层：提供基础服务支持

9. **容器化部署**
   - 使用 Docker 容器化应用
   - Kubernetes 编排管理
   - 支持灰度发布和回滚

10. **技术选型理由**
    - Spring Boot：成熟稳定，开发效率高
    - Redis：高性能缓存，分布式锁支持
    - MySQL：可靠的数据存储，事务支持
    - Prometheus：强大的监控能力
    - ACK：阿里云容器服务，运维成本低

## 系统架构
```
├── controller        // REST API 控制器
├── service          // 业务逻辑层
├── repository       // 数据访问层
├── model           // 数据模型
├── mapper          // 对象映射
├── config          // 配置类
└── util            // 工具类
```

## 快速开始

### 环境要求
- JDK 23
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 配置说明
1. 数据库配置 (application.properties)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

2. Redis配置 (application.yml)
```yaml
spring:
  data:
    redis:
      host: your_redis_host
      port: 6379
      database: 0
```

### 构建和运行
```bash
# 克隆项目
git clone https://github.com/yourusername/accountbalancecalculation.git

# 进入项目目录
cd accountbalancecalculation

# 构建项目
mvn clean package

# 运行项目
java -jar target/accountbalancecalculation-0.0.1-SNAPSHOT.jar
```

## API 文档

### 用户相关接口
```
POST /api/users           # 创建用户
GET /api/users/{id}      # 获取用户信息
PUT /api/users/{id}      # 更新用户信息
```

### 账户相关接口
```
POST /api/accounts              # 创建账户
GET /api/accounts/{id}         # 获取账户信息
POST /api/accounts/{id}/deposit  # 存款
POST /api/accounts/{id}/withdraw # 取款
```

### 交易相关接口
```
POST /api/transactions          # 创建交易
GET /api/transactions/{id}     # 获取交易详情
GET /api/transactions/account/{accountId} # 获取账户交易记录
```

## 性能优化
1. Redis缓存优化
   - 账户信息缓存
   - 交易记录缓存
   - 分布式锁实现

2. 数据库优化
   - 索引优化
   - 事务管理
   - 连接池配置

## 监控和指标
1. 缓存监控
   - 缓存命中率
   - 缓存使用量

2. 性能指标
   - API响应时间
   - 事务处理时间
   - 系统资源使用情况

## 测试
```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=TransactionServiceTest
```

## 部署
### 1. 本地部署
```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/accountbalancecalculation-0.0.1-SNAPSHOT.jar
```

### 2. 阿里云 ACK 集群部署
#### 2.1 前置准备
1. 阿里云账号和访问密钥
2. 安装并配置 kubectl 和 阿里云 CLI
3. 创建 ACK 集群（按需选择托管版或专有版）

#### 2.2 制作容器镜像
1. 创建 Dockerfile
```dockerfile
FROM openjdk:23-slim
WORKDIR /app
COPY target/accountbalancecalculation-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

2. 构建并推送镜像到阿里云容器镜像服务
```bash
# 登录阿里云容器镜像服务
docker login --username=your_username registry.cn-hangzhou.aliyuncs.com

# 构建镜像
docker build -t registry.cn-hangzhou.aliyuncs.com/your-namespace/accountbalancecalculation:v1.0 .

# 推送镜像
docker push registry.cn-hangzhou.aliyuncs.com/your-namespace/accountbalancecalculation:v1.0
```

#### 2.3 准备 Kubernetes 配置文件
1. 创建 ConfigMap (config.yaml)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: accountbalancecalculation-config
data:
  application.yml: |
    spring:
      datasource:
        url: jdbc:mysql://your-rds-instance:3306/your_database
        username: ${DB_USERNAME}
        password: ${DB_PASSWORD}
      data:
        redis:
          host: your-redis-instance
          port: 6379
```

2. 创建 Deployment (deployment.yaml)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: accountbalancecalculation
spec:
  replicas: 3
  selector:
    matchLabels:
      app: accountbalancecalculation
  template:
    metadata:
      labels:
        app: accountbalancecalculation
    spec:
      containers:
      - name: accountbalancecalculation
        image: registry.cn-hangzhou.aliyuncs.com/your-namespace/accountbalancecalculation:v1.0
        ports:
        - containerPort: 8080
        env:
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
```

3. 创建 Service (service.yaml)
```yaml
apiVersion: v1
kind: Service
metadata:
  name: accountbalancecalculation
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
  selector:
    app: accountbalancecalculation
```

#### 2.4 部署应用
```bash
# 创建 namespace
kubectl create namespace accountbalancecalculation

# 创建密钥
kubectl create secret generic db-secret \
  --from-literal=username=your-db-username \
  --from-literal=password=your-db-password \
  -n accountbalancecalculation

# 应用配置
kubectl apply -f config.yaml -n accountbalancecalculation
kubectl apply -f deployment.yaml -n accountbalancecalculation
kubectl apply -f service.yaml -n accountbalancecalculation
```

#### 2.5 验证部署
```bash
# 查看部署状态
kubectl get pods -n accountbalancecalculation

# 查看服务状态
kubectl get svc -n accountbalancecalculation

# 查看日志
kubectl logs -f deployment/accountbalancecalculation -n accountbalancecalculation
```

#### 2.6 扩缩容
```bash
# 手动扩容
kubectl scale deployment accountbalancecalculation --replicas=5 -n accountbalancecalculation

# 配置自动扩缩容
kubectl autoscale deployment accountbalancecalculation \
  --min=3 \
  --max=10 \
  --cpu-percent=80 \
  -n accountbalancecalculation
```

#### 2.7 监控和日志
1. 在 ACK 控制台配置：
   - 应用实时监控服务 ARMS
   - 日志服务 SLS
   - 容器监控服务

2. 配置告警规则：
   - CPU 使用率告警
   - 内存使用率告警
   - 错误日志告警
   - 接口响应时间告警 