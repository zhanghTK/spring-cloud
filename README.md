# Spring Cloud 代码示例

## Module 说明

### 各 Module 说明

01_springboot: Spring Boot 使用

02_eureka-server: Eureka 注册中心

03_hello-service: 服务提供者

04_eureka-server-ha: 高可用 Eureka 注册中心

05_hello-service: 注册到高可用 Eureka 注册中心的服务提供者

06_ribbon-consumer: 服务消费者，发现服务，消费服务

07_rest-template：RestTemplate 使用

08_ribbon-consumer-hystrix：服务消费者，发现服务，消费服务，错误熔断，Hystrix Dashboard

### Modules 使用：
01_springboot: Spring Boot

02_eureka-server，03_hello-service：向注册中心注册服务

04_eureka-server-ha，05_hello-service，06_ribbon-consumer：高可用的服务、注册中心，服务调用

07_rest-template：RestTemplate

04_eureka-server-ha，05_hello-service，08_ribbon-consumer-hystrix：高可用的服务、注册中心，服务调用，配合 hystrix

## Eureka

### 使用
#### 服务注册
1. 配置 @EnableDiscoveryClient
2. 配置 eureka.client.serviceUrl.defaultZone

#### 声明 Eureka Server
配置 @EnableEurekaServer

### Eureka 相关 UML

#### Eureka Client 实现

![Eureka 继承关系.jpg](https://ooo.0o0.ooo/2017/10/10/59dc2bc93f565.jpg)

左边是 Spring 的接口及调用，右边是 Netflix Eureka 的接口及具体实现，关于 Eureka 的实现集中在 DiscoveryClient。

#### Eureka Server 调用时序
![eureka-server-register.png](https://i.loli.net/2017/10/10/59dc3c2de72ef.png)

### 核心实现

#### Client
DiscoveryClient 实现 EurekaClient 接口。EurekaClient 协助与 Eureka Server 协作，具体包括：
0. 加载 Eureka Server URL 列表，支持 Region, Zone 实现区域性故障容错集群
1. 向 Eureka Server 注册服务实例，更具体的实现：InstanceInfoReplicator
2. 向 Eureka Server 服务续约，更具体的显示: HeartbeatThread, TimedSupervisorTask
3. 获取 Eureka Server 中的服务实例列表，更具体的实现：CacheRefreshThread, TimedSupervisorTask
4. 当服务关闭向 Eureka Server 取消续约

#### Server
1. ApplicationResource 接收请求，验证信息
2. 调用 PeerAwareInstanceRegistryImpl.register 方法注册
3. PeerAwareInstanceRegistryImpl 依赖父类的 register 完成注册逻辑
4. PeerAwareInstanceRegistryImpl 调用 replicateToPeers 向其它 Eureka Server 节点做状态同步

#### 总结
- Client：Eureka Server URL 列表支持 Region, Zone 实现区域性故障容错集群，注册的管理依赖定时任务
- Server：验证信息，完成注册，同步状态

#### 参考：
[Spring Cloud Eureka服务注册源码分析](http://xujin.org/sc/sc-eureka-register/)

## Ribbon

### 使用
创建一个 @LoadBalanced 注解的 RestTemplate

### Ribbon 相关 UML

![Ribbon.jpg](https://i.loli.net/2017/10/13/59e028770e175.jpg)

左侧是 Spring 的接口以及调用，右边是 Netflix 的主要接口和实现。二者通过 RibbonLoadBalancerClient 与 ILoadBalancer 进行关联。

### 核心实现

Spring Cloud 对 Ribbon 的使用核心逻辑在 RibbonLoadBalancerClient，其完成了 Spring 与 Netflix 的集成

#### 请求拦截, 转换

拦截：LoadBalancerInterceptor 拦截 RestTemplate 请求

服务名 -> HOST 转换：
- 调用时转换（Spring）：
  1. RibbonLoadBalancerClient.execute：调用 LoadBalancerRequest.apply（request 是由 LoadBalancerRequestFactory 创建）
  2. LoadBalancerRequest.apply
  3. LoadBalancerRequestFactory.createRequest：创建 ServiceRequestWrapper（重写 getURI），调用 ClientHttpRequestExecution.execute
  4. ClientHttpRequestExecution.execute：完成转换

- 直接转换（Netflix）:
  RibbonLoadBalancerClient.reconstructURI 调用 LoadBalancerContext.reconstructURIWithServer：获取 Server 实例, 原始 URI(服务名) 拼接成 HOST

#### 负载均衡调用

负载均衡的接口：ILoadBalancer ，默认实现：ZoneAwareLoadBalancer（由 RibbonClientConfiguration 配置）

- AbstractLoadBalancer：增加了新方法
- BaseLoadBalancer：负载均衡器的基础实现
  - 服务实例检测委托给 IPing。定义默认实现 SerialPingStrategy，采用线性遍历。以定时任务形式（PingTask）调用
  - 负载均衡处理规则委托给 IRule。默认实现采用 RoundRobinRule，采用线性遍历
  - 统计信息委托给 LoadBalancerStats
  - 实现了 ILoadBalancer, AbstractLoadBalancer 声明的方法
- DynamicServerListLoadBalancer：服务实例列表动态更新，服务实例列表过滤
  - 服务实例列表获取委托给 ServerList。默认实现采用 DomainExtractingServerList（EurekaRibbonClientConfiguration 中配置），进一步委托为构造时传入的 DiscoveryEnabledNIWSServerList
  - 服务列表更新委托给 ServerListUpdater。默认实现为 PollingServerListUpdater，以定时任务形式进行服务列表更新，更新操作逻辑委托给 ServerList
  - 服务实例列表过滤委托为 ServerListFilter。
- ZoneAwareLoadBalancer: 按区域负载均衡
  - 重新 setServerListForZones：实例列表更新后，为每个区域创建负载均衡器。
  - 重写 chooseServer 方法，筛选服务实例

#### 负载均衡策略实现
IRule
AbstractLoadBalancerRule：定义了 ILoadBalancer，作为负责依据
RandomRule：随机策略，无锁退让
RoundRobinRule：线性轮询，无锁退让，原子加
RetryRule：重试机制策略，在其他策略基础上增加重试
WeightedResponseTimeRule：扩展 RoundRobinRule，根据实例运行状况计算权重：定时任务触发计算，权重以区间表示
ClientConfigEnabledRobinRule：其他高级策略的基础，实现委托给 RoundRobinRule
BestAvailableRule：选取最空间策略，注入 LoadBalancerStats 获得统计信息
PredicateBasedRule：抽象策略，基于模板模式实现先过滤实例列表，在轮询
AvailabilityFilteringRule：过滤条件：非故障，实例并发请求数小于阀值；对线性轮询的改进：线性抽样直接尝试寻找可用且空闲的实例
ZoneAvoidanceRule：使用组合过滤：先过滤区域，再过滤非故障、实例并发请求数小于阀值的实例；


### 配置

#### 配置方式
1. Java Config
2. @RibbonClient(name = "SERVER-NAME", configuration = SERVER_CONFIGURATION.class) 实现对具体客户端的配置
3. 配置文件：
  - 全局配置：ribbon.<key>=<vaalue>
  - 对具体客户端配置：<clientName>.ribbon.<key>=<value>
  - ILoadBalancer：NFLoadBalancerClassName
  - IPing：NFLoadBalancerPingClassName
  - IRule：NFLoadBalancerRuleClassName
  - ServerList：NIWSServerListClassName
  - ServerListFilter：NIWSServerListFilterClassName

#### 总结
1. 拦截 RestTemplate 请求，交给 ILoadBalancer 负载均衡
2. ILoadBalancer 的实现以定时任务形式维护可用服务列表，提供服务获取、过滤，统计调用调用信息、服务状态

## Hystrix

### 使用
1. 启用 @EnableCircuitBreaker
2. 使用 @HystrixCommand

### 相关 UNL

### 核心实现
1. @EnableCircuitBreaker 注解指定 EnableCircuitBreakerImportSelector 加载 Spring.factories 中的 EnableCircuitBreaker=HystrixCircuitBreakerConfiguration
2. HystrixCommandAspect 创建 @HystrixCommand 注解切面
3. 根据方法信息创建 MetaHolder 实例，
  1. 同步方式调用 HystrixCommand.execute
  2. 异步方式调用 HystrixCommand.queue
  3. OBSERVABLE 方式调用 HystrixCommand.observe 或 HystrixCommand.toObservable

### 总结
1. 创建 @HystrixCommand 方法切面
2. 将普通方法转换为 HystrixCommand
