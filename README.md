# Spring Cloud 代码示例

## Module 说明

### 各 Module 说明

01_springboot: Spring Boot 使用

02_eureka-server: Eureka 注册中心

03_hello-service: 服务提供者

04_eureka-server-ha: 高可用 Eureka 注册中心

05_hello-service-ha: 注册到高可用 Eureka 注册中心的服务提供者

06_ribbon-consumer: 服务消费者，发现服务，消费服务

07_rest-template：RestTemplate 使用

08_ribbon-consumer-hystrix：服务消费者，发现服务，消费服务，错误熔断，Hystrix Dashboard

09_hystrix：hystrix 使用

10_turbine：turbine：集群监控

11_feign-consumer：Feign 使用

12_hello-service-api：服务消费者，服务提供者的统一接口

### Modules 使用：
01_springboot: Spring Boot

02_eureka-server，03_hello-service：向注册中心注册服务

04_eureka-server-ha，05_hello-service，06_ribbon-consumer：高可用的服务、注册中心，服务调用

07_rest-template：RestTemplate

04_eureka-server-ha，05_hello-service，08_ribbon-consumer-hystrix：高可用的服务、注册中心，服务调用，配合 hystrix

09_hystrix：hystrix 使用

04_eureka-server-ha，05_hello-service-ha，08_ribbon-consumer-hystrix，10_turbine：构建监控聚合服务

04_eureka-server-ha，05_hello-service-ha，11_feign-consumer，12_hello-service-api：feign 使用