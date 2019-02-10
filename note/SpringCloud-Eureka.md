# SpringCloud-Eureka

## Eureka是什么

- Netflix在设计Eureka时遵循的就是AP原则。
- Eureka是Netflix的一个子模块，也是核心模块之一。Eureka是一个基于REST服务，用于定位服务，以实现云端中间层服务发现和故障转移。服务注册与发现对于微服务架构来说是非常重要的，有了服务注册与发现，只需要使用服务的标识符，就可以访问到服务，而不需要修改服务调用的配置文件了。功能类似于dubbo的注册中心，比如Zookeeper。

## 原理讲解

### Eureka的基本框架

- SpringCloud封装了Netflix公司开发的Eureka架构来实现服务注册和发现。
- Eureka采用了C-S的设计架构。Eureka Server作为服务注册功能的服务器，它是服务注册中心。
- 系统中的其他微服务，使用Eureka的客户端连接到Eureka Server并维持心跳连接。这样系统维护人员就可以通过Eureka Server来监控系统中各个微服务是否正常运行。SpringCloud的其他模块（比如Zuul）就可以通过Eureka Server来发现系统中的其他微服务，并执行相关的逻辑。
- Eureka包含两大组件
  - `Eureka Server`提供服务注册服务，各个节点启动后，会在Eureka Server中进行注册，这样Eureka Server中的服务注册表将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。
  - `Eureka Client`是一个Java客户端，用于简化Eureka Server的交互，客户端同时具备一个内置的，使用轮询负载算法的负载均衡器。在应用启动后，将会向Eureka Server发送心跳（默认周期是30秒）。如果Eureka Server在多个心跳周期内没有收到某个 节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除（默认90秒）。

### 三大角色

- Eureka Server提供服务注册和发现。
- Service Provider服务提供方将自身服务注册到Eureka，从而使服务方额能够找到。
- Service Consumer服务消费方从Eureka获取注册服务列表，从而能够消费服务。

## 引入Cloud的新技术组件

- 新增一个新技术相关的maven坐标。

  ```xml
  <!--eureka-server服务端 -->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-eureka-server</artifactId>
  </dependency>
  ```

  

- 在主启动类上面，标注的启动该新组件技术的相关的注解标签。

  ```java
  package com.devinkin.springcloud;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
  
  @SpringBootApplication
  @EnableEurekaServer
  public class EurekaServer7001_App {
      public static void main(String[] args) {
          SpringApplication.run(EurekaServer7001_App.class,args);
      }
  }
  ```

  

- Java业务逻辑编码

## Eureka相关配置

- `eureka.instance.hostname`为eureka服务端的实例名称。
- `eureka.client.register-with-eureka`表示是否向注册中心注册自己。
- `eureka.client.fetch-registry`表示是否需要检索服务。Eureka自身就是注册中心，不需要自己检索自己的服务。
- `eureka.client.service-url.defaultZone`设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址。

## Eureka工程搭建

### Eureka注册中心

- pom.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <parent>
          <artifactId>microservicecloud</artifactId>
          <groupId>com.devinkin.springcloud</groupId>
          <version>1.0-SNAPSHOT</version>
      </parent>
      <modelVersion>4.0.0</modelVersion>
  
      <artifactId>microservicecloud-eureka-7001</artifactId>
  
      <dependencies>
          <!--eureka-server服务端 -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-eureka-server</artifactId>
          </dependency>
          <!-- 修改后立即生效，热部署 -->
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>springloaded</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-devtools</artifactId>
          </dependency>
      </dependencies>
  </project>
  ```

- application.yml

  ```yaml
  server:
    port: 7001
  
  eureka:
    instance:
      hostname: localhost #eureka服务端的实例名称
    client:
      register-with-eureka: false     #false表示不向注册中心注册自己。
      fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
      service-url:
        defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/       #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址（单机）。
  #      defaultZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  ```

- 在启动类上使用`@EnableEurekaServer`注解接受其他微服务注册进来。

### 服务提供方注册到Eureka

- 新增以下依赖到pom.xml

  ```xml
  <!-- 将微服务provider侧注册进eureka -->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-eureka</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-config</artifactId>
  </dependency>
  ```

- 新增如下配置到application.yml

  ```yml
  eureka:
    client:
      serviceUrl:
        defaultZone: http://localhost:7001/eureka
  ```

- 在主启动类中添加注解`@EnableEurekaClient`将服务注册到Eureka Server

  ```java
  @SpringBootApplication
  @EnableEurekaClient             // 本服务启动后自动注册到eureka服务中
  public class DeptProvider8001_App {
      public static void main(String[] args) {
          SpringApplication.run(DeptProvider8001_App.class, args);
      }
  }
  ```

## Actuator与注册微服务信息完善

### 主机名称：服务名称修改

- 在服务提供方的`application.yml`文件中添加如下内容

  ```yaml
  eureka:  
    instance:
      instance-id: microservicecloud-dept8001
  ```

### 访问信息有IP信息提示

- 在服务提供方的`application.yml`文件中添加如下内容

  ```yaml
  eureka:
    instance:
      prefer-ip-address: true
  ```

### 微服务info内容详细信息

- 在服务提供方的`pom.xml`文件中添加如下内容

  ```xml
  <!-- actuator监控信息完善 -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
  ```

- 总的父工程的`pom.xml`添加build信息

  ```xml
  <build>
      <finalName>microservicecloud</finalName>
      <resources>
          <resource>
              <directory>src/main/resources</directory>
              <filtering>true</filtering>
          </resource>
      </resources>
      <plugins>
          <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-resources-plugin</artifactId>
              <configuration>
                  <delimiters>
                      <delimit>$</delimit>
                  </delimiters>
              </configuration>
          </plugin>
      </plugins>
  </build>
  ```

- 在服务提供方`application.yml`添加如下配置

  ```yaml
  info:
    app.name: devinkin-microservicecloud
    company.name: www.devinkin.com
    build.artifactId: ${project.artifactId}
    build.version: ${project.version}
  ```

## Eureka自我保护机制

- 某时刻某个微服务不可用了，eureka不会立刻清理，依旧会对该微服务的信息进行保存。
- 默认情况下，如果Eureka Server在一定时间内没有接收到某个微服务实例的心跳，Eureka Server将会注销该实例（默认90秒）。但网络分区故障时，微服务与Eureka Server之间无法正常通信，以上行为可能变得非常危险了。因为微服务本身是健康的，此时本不应该注销这个微服务。
- Eureka通过“自我保护模式”来解决这个问题，当Eureka Server节点在短时间内丢失过多客户端时（可能发生了网络分区故障），那么该节点就可能进入自我保护模式。一旦进入该模式，Eureka Server就会保护服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何服务）。当网络故障恢复后，该Eureka Server节点自动退出自我保护模式。
- 总结：自我保护模式是一种应对网络异常的安全保护措施。它的架构哲学是宁可保留所有的微服务（健康的微服务和不健康的微服务都会保留），也不盲目注销任何健康的微服务。使用自我保护模式，可以让Eureka集群更加健壮，稳定。
- 在Spring Cloud中，可以使用`eureka.server.enable-self-preservation=false`禁用自我保护模式。

## Eureka服务发现

- 通过注册进Eureka里面的微服务，我们可以通过服务发现来获得该服务的信息。

- 修改服务提供者的Controller，添加如下内容

  ```java
  // 服务发现
  @Autowired
  private DiscoveryClient client;
  
  @GetMapping(value = "/dept/discovery")
  public Object discovery() {
      List<String> list = client.getServices();
      System.out.println("***********" + list);
  
      List<ServiceInstance> srvlist = client.getInstances("MICROSERVICECLOUD-DEPT");
      for (ServiceInstance e : srvlist) {
          System.out.println(e.getServiceId() + "\t" + e.getHost() + "\t" +
                  e.getPort() + "\t" + e.getUri());
      }
      return this.client;
  }
  ```

- 在主启动类中添加标注`@EnableDiscoveryClient`注解作为服务发现。

- 在消费者的Controller中添加如下内容

  ```java
  // 测试@EnableDiscoveryClient，消费端可以调用服务发现
  @GetMapping("/consumer/dept/discovery")
  public Object discoveriy() {
      return restTemplate.getForObject(REST_URL_PREFIX + "/dept/discovery", Object.class);
  }
  ```

## Eureka集群配置

- 创建新的工程。
- 复制pom.xml和主启动类到新工程下。

### 修改映射配置

- 修改映射配置到hosts文件中。

- 修改eureka注册中心的`application.yml`

  ```yaml
  eureka:
    instance:
      hostname: eureka7001 #eureka服务端的实例名称
    client:
      register-with-eureka: false     #false表示不向注册中心注册自己。
      fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
      service-url:
        defaultZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
  ```

### 服务提供者修改配置

- 服务提供者发布到上面三台eureka集群配置

  ```yaml
  eureka:
    client:
      service-url:
        defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka,http://eureka7003.com:7003/eureka
    instance:
      instance-id: microservicecloud-dept8001
      prefer-ip-address: true
  ```


## Eureka比Zookeeper好在哪里

### ACID和CAP

- ACID代表的是
  - `A(Atomicity)`原子性
  - `C(Consistency)`一致性
  - `I(Isolation)`独立性
  - `D(Durability)`持久性
- CAP代表的是
  - `C(Consistency)`强一致性
  - `A(Availability)`可用性
  - `P(Partition tolerance)`分区容错性
- CAP的3进2说的是一个分布式系统不可能同时很好的满足一致性，可用性和分区容错性这三个需求，只能满足其中的两个需求。
  - `CA`单点集群，满足一致性，可用性的系统，通常在可扩展性上不太强大。
  - `CP`满足一致性，分区容忍性的系统，通常性能不是特别高。
  - `AP`满足可用性，分区容忍性的系统，通常可能对一致性要求低一些。
- Zookeeper保证的是CP，Eureka则是AP。
- Eureka看可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像zookeeper那样使整个注册服务瘫痪。