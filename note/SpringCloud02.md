# SpringCloud_Rest微服务案例

- 实验工程架构
  - SpringServiceCloud父工程，带3个子模块。
    - microservicecloud-api（api公共模块）
    - microservicecloud-provider-dept-8001（微服务落地的提供者）
    - microservicecloud-consumer-dept-80（微服务落地的消费者）

## SpringCloudDept消费者模块

- `RestTemplate`提供了多种便捷访问远程Http服务的方法。
- `RestTemplate`是一种简单便捷的访问restful服务模板类，是Spring提供的用于访问Rest服务的客户端模板工具集。

