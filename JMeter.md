
# JMeter 并发测试计划

## 1. 测试目标
- 测试账户转账接口的并发性能
- 验证数据一致性
- 检查死锁情况
- 评估系统在高并发下的表现

## 2. 测试环境准备
1. 下载并安装 JMeter (https://jmeter.apache.org/download_jmeter.cgi)
2. 启动应用程序
3. 准备测试数据（确保账户 6 有足够余额）

## 3. 测试场景设置

### 3.1 基本配置
1. 创建线程组 (Thread Group)
   - Number of Threads (用户): 10
   - Ramp-up Period (秒): 1
   - Loop Count: 1

2. 添加 HTTP Request Defaults
   - Protocol: http
   - Server Name: localhost
   - Port: 8080

### 3.2 测试场景一：单账户并发转出

http
POST http://localhost:8080/api/transactions/transfer
Parameters:
```
sourceCardId: 6
targetCardId: 7
amount: 100.00
```


### 3.3 测试场景二：多账户并发转账

http
POST http://localhost:8080/api/transactions/transfer
Parameters (CSV Data Set):
```csv
sourceCardId,targetCardId,amount
6,7,100.00
6,8,100.00
6,9,100.00
```


## 6. 结果验证

### 6.1 数据一致性检查
- 源账户余额是否正确（初始余额 - 总转出金额）
- 目标账户余额是否正确（初始余额 + 总转入金额）
- 交易记录数量是否与预期一致

### 6.2 性能指标
- 平均响应时间 < 1000ms
- 错误率 < 1%
- TPS > 10

### 6.3 并发问题检查
- 检查是否有死锁日志
- 检查是否有重复交易
- 验证所有交易是否都被正确记录

## 7、创建测试计划：

<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Transfer Test">
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.comments"></stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Thread Group">
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">10</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">100</stringProp>
        <stringProp name="ThreadGroup.ramp_time">1</stringProp>
        <longProp name="ThreadGroup.start_time">1373789594000</longProp>
        <longProp name="ThreadGroup.end_time">1373789594000</longProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
      </ThreadGroup>
    </hashTree>
  </hashTree>
</jmeterTestPlan>


## 账户6向不同账户并发转账
curl -X POST "http://localhost:8080/api/transactions/transfer?sourceCardId=6&targetCardId=7&amount=100.00"
curl -X POST "http://localhost:8080/api/transactions/transfer?sourceCardId=6&targetCardId=8&amount=100.00"


## 不同账户向账户7转账
curl -X POST "http://localhost:8080/api/transactions/transfer?sourceCardId=6&targetCardId=7&amount=100.00"
curl -X POST "http://localhost:8080/api/transactions/transfer?sourceCardId=8&targetCardId=7&amount=100.00"