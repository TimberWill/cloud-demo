

# springcloud
# 1. 介绍

1. 单体架构
   ![img.png](picture/单体架构.png)
- 公网ip：全世界都可以访问
- 域名：xx.com等，更好记
2. 集群
   解决大并发问题
   本质是一个个服务副本，如果要更新某个业务（比如订单），就需要先将所有业务都下线，再重新打包。【牵一发而动全身】
   ![img.png](picture/集群.png)
   通过网关来分发服务器请求。
- 网关/Nginx：所有请求流量的入口。
3. 分布式架构
   一个大应用拆分为多个小应用，分别部署在不同的服务器上。
   ![img.png](picture/分布式架构.png)
- 消除单点故障
- RPC：远程调用（HTTP+JSON）
- 服务注册：将服务上/下线消息告诉注册中心，这样就方便远程调用（在调用之前，知道要调用的服务在哪）
- 配置中心：统一管理所有配置，一旦发生配置变更，进行推送。
- 服务熔断：为了解决服务雪崩的问题。本质是一种快速失败机制，及时释放掉资源。比如如果前面的服务有50%都卡顿了5秒以上，后续就直接失败。
- 服务雪崩：一个服务的卡顿，影响到后续其他的服务，导致服务资源耗尽。
- 分布式事务：牵扯到不同的数据库，并且部署在不同的服务器。

  ![img_1.png](picture/分布式事务.png)

# 2. 项目创建
- 框架：

  ![img.png](picture/项目框架.png)
- 版本选择：
  ![img.png](picture/版本选择.png)
1. 修改父pom包，后续直接继承即可
  ```xml
<?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <!--继承springboot的版本-->
  <parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.3.4</version>
  <relativePath/> <!-- lookup parent from repository -->
  </parent>
  <packaging>pom</packaging>
  <groupId>com.whl.cloud</groupId>
  <artifactId>cloud-demo</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>cloud-demo</name>
  <description>cloud-demo</description>
  
      <properties>
          <maven.compiler.source>17</maven.compiler.source>
          <maven.compiler.target>17</maven.compiler.target>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <spring-cloud.version>2023.0.3</spring-cloud.version>
          <spring-cloud-alibaba.version>2023.0.3.2</spring-cloud-alibaba.version>
      </properties>
  
      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.springframework.cloud</groupId>
                  <artifactId>spring-cloud-dependencies</artifactId>
                  <version>${spring-cloud.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
              <dependency>
                  <groupId>com.alibaba.cloud</groupId>
                  <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                  <version>${spring-cloud-alibaba.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
  
  </project>
  ```

2. 创建子module（maven项目即可）
  services只是用来管理项目的，打包方式也是pom
![img.png](picture/img.png)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.whl.cloud</groupId>
        <artifactId>cloud-demo</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <packaging>pom</packaging>

    <artifactId>services</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

</project>
```

3. services下创建子模块：service-order、service-product，并在services下导入公共依赖
```xml
<dependencies>
    <!--注册中心、配置中心  服务发现-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <!--远程调用-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>
```


# 3. Nacos
   nacos是注册中心、配置中心

## 3.1 注册中心
   保存微服务清单列表

![img_1.png](picture/img_1.png)

   核心功能：
- 服务注册
- 服务发现

###  3.1.1 服务注册
1. 安装nacos
   ![img_2.png](picture/img_2.png)
   去官网下载2.4.3版本
    ![img_3.png](picture/img_3.png)
2. 解压，在bin目录下进入cmd
- 这里以单机模式启动为例：输入命令
  startup.cmd -m standalone
  ![img_4.png](picture/img_4.png)
- 启动成功后，可以访问链接：localhost:8848/nacos
  ![img_5.png](picture/img_5.png)
  配置管理对应配置中心
  服务管理对应注册中心
3. 服务注册步骤：

   ![img_6.png](picture/img_6.png)
   - 以service-order为例：
       - pom.xml添加依赖：
         ```xml
         <dependencies>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
         </dependencies>
         ```
       - application.yaml添加配置
         ```yaml
         spring:
           application:
             name: service-order
           cloud:
             nacos:
               server-addr: 127.0.0.1:8848
         server:
           port: 8001
         ```
      


- 启动order-service服务

    可以查看到新注册的服务。
    ![img_7.png](picture/img_7.png)
- 本地起多个应用示例
    - Copy configuration：
   
      ![img_8.png](picture/img_8.png)
    - Program arguments
  
      ![img_9.png](picture/img_9.png)
    - 修改端口号和name

      ![img_10.png](picture/img_10.png)
      
    - 在本地起多个服务：
  
      ![img_11.png](picture/img_11.png)
      
    - nacos网页可以看到：
      
      ![img_12.png](picture/img_12.png)
**注意：注册期间，cmd窗口不可关闭，否则nacos服务就没有开启。**

### 3.1.2 服务发现
步骤：
![img_13.png](picture/img_13.png)
1. 在服务启动类上加注解`@EnableDiscoveryClient`，可以实现服务之间的互相调用
2. 服务发现是自动的过程，可以通过`DiscoveryClient`和`NacosServiceDiscovery`来验证
```java
@SpringBootTest
   public class DiscoveryTest {

     @Autowired
     DiscoveryClient discoveryClient;
     
     @Autowired
     NacosServiceDiscovery nacosServiceDiscovery;
     
     @Test
     void testDiscoveryClient(){
       for (String service : discoveryClient.getServices()) {
         System.out.println(service);
         List<ServiceInstance> instances = discoveryClient.getInstances(service);
         for (ServiceInstance instance : instances) {
           System.out.println(instance.getHost() + ":" + instance.getPort());
         }
       }
     }
  
     @Test
     void testNacosServiceDiscovery(){
       try {
         for (String service : nacosServiceDiscovery.getServices()) {
           System.out.println("service = " + service);
           List<ServiceInstance> instances = nacosServiceDiscovery.getInstances(service);
           for (ServiceInstance instance : instances) {
             System.out.println(instance.getHost() + ":" + instance.getPort());
           }
         }
       } catch (NacosException e) {
         throw new RuntimeException(e);
       }
     }
   }
```

### 3.1.3 远程调用
基本流程：
[图片]
下单场景模拟：
[图片]

### 3.1.4 远程调用进阶-负载均衡
[图片]
比如有有多个order-service服务，希望请求均衡的分发到多个服务实例上，而不是怼着一个实例发送请求。
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
//负载均衡的调用
@Autowired
LoadBalancerClient loadBalancerClient;

@Test
void testLoadBalance() {
ServiceInstance choose1 = loadBalancerClient.choose("service-product");
System.out.println("choose1 = " + choose1.getHost() + ":" + choose1.getPort());
ServiceInstance choose2 = loadBalancerClient.choose("service-product");
System.out.println("choose2 = " + choose2.getHost() + ":" + choose2.getPort());
ServiceInstance choose3 = loadBalancerClient.choose("service-product");
System.out.println("choose3 = " + choose3.getHost() + ":" + choose3.getPort());
ServiceInstance choose4 = loadBalancerClient.choose("service-product");
System.out.println("choose4 = " + choose4.getHost() + ":" + choose4.getPort());
ServiceInstance choose5 = loadBalancerClient.choose("service-product");
System.out.println("choose5 = " + choose5.getHost() + ":" + choose5.getPort());
ServiceInstance choose6 = loadBalancerClient.choose("service-product");
System.out.println("choose6 = " + choose6.getHost() + ":" + choose6.getPort());
}
//后台打印，能够均衡的获取实例【按照轮询规则】
choose1 = 10.12.165.201:7002
choose2 = 10.12.165.201:7001
choose3 = 10.12.165.201:7003
choose4 = 10.12.165.201:7002
choose5 = 10.12.165.201:7001
choose6 = 10.12.165.201:7003

### 3.1.5 @LoadBalance注解实现负载均衡
比如用RestTemplate类来实现调用三方接口时，直接在上面加注解即可。
[图片]
思考：如果注册中心宕机了，远程调用还能成功吗？
如果之前远程调用过，即使注册中心宕机了也可以成功【这里用的是缓存的信息】。如果之前没有调用过，就会失败。

## 3.2 配置中心
统一管理所有微服务的配置。
- 配置集中管理
- 推送配置变更
- 不停机配置更新
  [图片]
### 3.2.1 基本用法
1. 在services层引入依赖
   <dependency>
   <groupId>com.alibaba.cloud</groupId>
   <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
   </dependency>
2. 在service-order的配置文件中添加：
   spring:
   config:
   import: nacos:service-order.properties
3. 在nacos网页中添加
- 配置管理-配置列表-创建配置
- 新建配置信息：
  [图片]
4. 测试是否生效
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

@Value("${order.timeout}")
String orderTimeout;

@Value("${order.auto-confirm}")
String orderAutoConfirm;

@GetMapping("/config")
public String config() {
return "orderTimeout=" + orderTimeout + ", orderAutoConfirm=" + orderAutoConfirm;
}

}
启动日志：
[图片]
访问请求：
[图片]
在controller上添加注解`@RefreshScope`，这样在nacos中修改完配置，不需要重启，就可以直接获取新的配置值了。
[图片]

注意：一旦引入了config依赖，如果在application.yaml中没有指定config.import，项目启动就会报错。
[图片]
只需要在application.yaml中添加禁用导入检查即可
spring:
cloud:
nacos:
config:
import-check:
enabled: false

### 3.2.2 动态刷新
前面的方式不利于管理，现整合成一个properties类对应。
- OrderProperties类：
    - order代表前缀
    - 类的属性代表order后面的内容，遵从驼峰命名规则
      [图片]

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "order")
@Data
public class OrderProperties {
String timeout;

String autoConfirm;
}
- OrderController类：不需要`@RefreshScope`注解了
  import com.whl.cloud.properties.OrderProperties;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

@Autowired
OrderProperties orderProperties;

@GetMapping("/config")
public String config() {
return "orderTimeout=" + orderProperties.getTimeout() + ", orderAutoConfirm=" +
orderProperties.getAutoConfirm();
}

}

### 3.2.3 配置监听
实现：监听配置变化，并发送邮件。
在启动类中添加applicationRunner。
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableDiscoveryClient
@SpringBootApplication
public class OrderMainApplication {
public static void main(String[] args) {
System.out.println("Hello, order main application!");
SpringApplication.run(OrderMainApplication.class, args);
}

@Bean
ApplicationRunner applicationRunner(NacosConfigManager nacosConfigManager) {
return args -> {
ConfigService configService = nacosConfigManager.getConfigService();
configService.addListener("service-order.properties", "DEFAULT_GROUP" , new Listener() {

        @Override
        public Executor getExecutor() {
          return Executors.newFixedThreadPool(4);
        }

        //接收配置信息
        @Override
        public void receiveConfigInfo(String configInfo) {
          System.out.println("变化的配置信息" + configInfo);
          //模拟邮件通知
          System.out.println("邮件通知...");
        }
      });
      System.out.println("===================");
    };
}
}
在nacos页面中修改配置后，后台会实时打印信息。
[图片]

### 3.2.4 经典面试题
如果微服务中有配置，配置中心也有相关配置，那最终以哪个为准？
应该以配置中心为准，

### 3.2.5 数据隔离
namespace
通过构建多个名称空间，来区分多套环境，定义其配置
nacos中的配置
在项目启动前激活
[图片]
在命名空间，不创建任何命名空间的情况下，使用的是默认的命名空间。
[图片]
创建dev、test、prod命名空间。
[图片]
在dev下创建通用配置。
[图片]
比如这里在dev下创建：
- common.properties：通用配置
  [图片]
- database.properties：数据库配置
  [图片]
  还可以选中配置，克隆到别的环境中
  [图片]

动态切换环境
实现按需加载
基础版：
- application.yaml
  server:
  port: 8001
  spring:
  application:
  name: service-order
  cloud:
  nacos:
  server-addr: 127.0.0.1:8848
  config:
  namespace: test
  config:
  import:
  - nacos:common.properties?group=order
  - nacos:database.properties?group=order
- 日志打印：
  [图片]
  [图片]

plus版：针对不同的环境，导入配置数量不同的问题：
- application.yaml
  server:
  port: 8001
  spring:
  profiles:
  active: test
  application:
  name: service-order
  cloud:
  nacos:
  server-addr: 127.0.0.1:8848
  config:
  namespace: ${spring.profiles.active:public}

---
spring:
config:
import:
- nacos:common.properties?group=order
- nacos:database.properties?group=order
activate:
on-profile: dev
---
spring:
config:
import:
- nacos:common.properties?group=order
- nacos:database.properties?group=order
- nacos:haha.properties?group=order
activate:
on-profile: test
---
spring:
config:
import:
- nacos:common.properties?group=order
activate:
on-profile: prod

## 3.3 总结
[图片]


# 4. OpenFeign
   ## 4.1 远程调用
   ### 4.1.1 声明式实现
   远程调用工具/声明式
   [图片]

1. 使用
- 引入openfeign包：
<!--openfeign-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
  - 注解`@EnableFeignClients`：开启远程调用功能
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class OrderMainApplication {
  public static void main(String[] args) {
    System.out.println("Hello, order main application!");
    SpringApplication.run(OrderMainApplication.class, args);
  }

@Bean
ApplicationRunner applicationRunner(NacosConfigManager nacosConfigManager) {
return args -> {
ConfigService configService = nacosConfigManager.getConfigService();
configService.addListener("service-order.properties", "DEFAULT_GROUP" , new Listener() {

        @Override
        public Executor getExecutor() {
          return Executors.newFixedThreadPool(4);
        }

        //接收配置信息
        @Override
        public void receiveConfigInfo(String configInfo) {
          System.out.println("变化的配置信息" + configInfo);
          //模拟邮件通知
          System.out.println("邮件通知...");
        }
      });
      System.out.println("===================");
    };
}
}
- 编写远程调用客户端（发送远程调用请求的客户端）
    - mvc的两套逻辑：
        - 标注在Controller上，接收这样的请求
        - 标注在FeignClient上，发送这样的请求
    - 注解`@RequestHeader`：将参数放到请求头上
- ProductFeignClient ：
  /**
* 用来调用商品服务的客户端.
  */
  @FeignClient(value = "service-product")
  public interface ProductFeignClient {

@GetMapping("/product/{id}")
Product getProduct(@PathVariable Long id);

}
- OrderController：
  @RestController
  public class OrderController {

@Autowired
OrderProperties orderProperties;

@Autowired
private OrderService orderService;

@GetMapping("/config")
public String config() {
return "order.timeout=" + orderProperties.getTimeout() + ", order.autoConfirm=" +
orderProperties.getAutoConfirm() + ", order.dbUrl=" + orderProperties.getDbUrl();
}

//通过Feign实现远程调用
@GetMapping("/create")
public Order createOrder(Long userId, Long productId) {
Order order = orderService.createOrder(productId, userId);

    return order;
}

}

### 4.1.2 第三方API
以墨迹天气服务为例：
需要用url精确指定发送请求的地址（协议+域名）
1. FeignClient
   @FeignClient(value = "weather-client", url = "http://aliv18.data.moji.com")
   public interface WeatherFeignClient {

@PostMapping("whapi/json/alicityweather/condition")
String getWeather(@RequestHeader("Authorization") String auth,
@RequestParam("token") String token,
@RequestParam("cityId") String cityId);
}
2. Test类
   @SpringBootTest
   public class WeatherTest {

@Autowired
WeatherFeignClient weatherFeignClient;

@Test
void getWeather() {
String weather = weatherFeignClient.getWeather("APPCODE 93b7e19861a24c519a7548b17dc16d75",
"50b53ff8dd7d9fa320d3d3ca32cf8ed1",
"2182");

    System.out.println(weather);

}
}
注意：
1. 如果调用自己的api，直接将controller中的接口放到FeignClient中
2. 如果调用第三方api，就参照第三方的接口文档发请求

思考：客户端负载均衡与服务端负载均衡的区别？
[图片]

## 4.2 进阶配置
### 4.2.1 日志
1. 配置文件开启日志：application.yaml
   logging:
   level:
   com.whl.cloud.feign: debug
2. 配置类：OrderConfig
   import feign.Logger;

@Configuration
public class OrderConfig {
@Bean
Logger.Level feignLoggerLevel() {
return Logger.Level.FULL;
}

}
3. 日志打印示例：
   [图片]

### 4.2.2 超时控制
比如，远程调用请求卡顿，高并发下请求积压造成请求雪崩。
[图片]
这里就需要引入限时等待。
openFeign默认连接超时10秒，读取超时60秒（可以通过对feign配置来修改时间）
[图片]
超时返回500错误
[图片]
### 4.2.3 超时配置
官方文档中有介绍：https://docs.springframework.org.cn/spring-cloud-openfeign/reference/spring-cloud-openfeign.html
[图片]
没有设置feignClient的使用默认项目。
- application.yaml：
    - 其中includ： feign代表包含这个配置
      server:
      port: 8001
      spring:
      profiles:
      active: test
      include: feign
      application:
      name: service-order
      cloud:
      nacos:
      server-addr: 127.0.0.1:8848
      config:
      namespace: ${spring.profiles.active:public}

logging:
level:
com.whl.cloud.feign: debug


---
spring:
config:
import:
- nacos:common.properties?group=order
- nacos:database.properties?group=order
activate:
on-profile: dev
---
spring:
config:
import:
- nacos:common.properties?group=order
- nacos:database.properties?group=order
- nacos:haha.properties?group=order
activate:
on-profile: test
---
spring:
config:
import:
- nacos:common.properties?group=order
activate:
on-profile: prod
- application-feign.yaml：
    - 更多参数配置可以在FeignClientConfiguration类中找到
      [图片]
      其中service-product来自于：
      [图片]
      spring:
      cloud:
      openfeign:
      client:
      config:
      default:
      logger-level: full
      connect-timeout: 1000
      read-timeout: 2000
      service-product:
      logger-level: full
      connect-timeout: 3000
      read-timeout: 5000

### 4.2.4 重试机制
openFeign默认从不重试。可以自己定义一个重试机制。
每次重试都基于上次的1.5倍时间，越往后会间隔越大。
[图片]
- 在Config中添加Retryer配置
  @Configuration
  public class OrderConfig {

  @Bean
  Retryer retryer() {
  return new Retryer.Default();
  }

  @Bean
  Logger.Level feignLoggerLevel() {
  return Logger.Level.FULL;
  }

  @LoadBalanced//注解式负载均衡
  @Bean
  RestTemplate restTemplate() {
  return new RestTemplate();
  }
  }

### 4.2.5 拦截器
响应拦截器用的不多，主要是请求拦截器。
[图片]
从请求拦截器的请求头中拿到token
步骤：
1. order端：
   注意是feign包下的请求拦截器
   方式一：
- 拦截器：
  public class XTokenRequestInterceptor implements RequestInterceptor {
  /**
* 请求拦截器.
* @param requestTemplate 请求模板
  */
  @Override
  public void apply(RequestTemplate requestTemplate) {
  System.out.println("XTokenRequestInterceptor  >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
  requestTemplate.header("X-Token", UUID.randomUUID().toString());
  }
  }
- 配置文件：
  spring:
  cloud:
  openfeign:
  client:
  config:
  default:
  logger-level: full
  connect-timeout: 1000
  read-timeout: 2000
  service-product:
  logger-level: full
  connect-timeout: 3000
  read-timeout: 5000
  # 配置拦截器
  request-interceptors:
  - com.whl.cloud.interceptor.XTokenRequestInterceptor
  【这样做会使得拦截器对service-product服务生效，或者直接在拦截器上加上@Component注解，就可以每一次调用都会经过拦截器】
  方式二：
  @Component
  public class XTokenRequestInterceptor implements RequestInterceptor {
  /**
* 请求拦截器.
* @param requestTemplate 请求模板
  */
  @Override
  public void apply(RequestTemplate requestTemplate) {
  System.out.println("XTokenRequestInterceptor  >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
  requestTemplate.header("X-Token", UUID.randomUUID().toString());
  }
  }
2. product端：
- 接口获取token
  @RestController
  public class ProductController {

@Autowired
private ProductService productService;

@GetMapping("/product/{id}")
public Product getProduct(@PathVariable Long id,
HttpServletRequest request) {
String token = request.getHeader("X-Token");
System.out.println("hello.......token=" + token);
return productService.getProduct(id);
}
}
- 后台打印：每次请求的token都不一样
  [图片]

### 4.2.6 Fallback机制（兜底返回）
有的场景不需要错误信息，改善用户体验。
[图片]
Fallback实现了TestClient ，当请求失败后，会走Fallback中的请求，返回兜底数据。
[图片]
示例：
1. 在属性中指定fallback
   @FeignClient(value = "service-product", fallback = ProductFeignClientFallback.class)
   public interface ProductFeignClient {

@GetMapping("/product/{id}")
Product getProduct(@PathVariable Long id);

}
2. fallback处理
   @Component
   public class ProductFeignClientFallback implements ProductFeignClient {
   @Override
   public Product getProduct(Long id) {
   System.out.println("兜底回调。。。");
   //可以造一个假数据
   Product product = new Product();
   product.setId(id);
   product.setPrice(new BigDecimal("0"));
   product.setProductName("未知商品");
   product.setNum(0);

   return product;
   }
   }
3. 测试该功能需要将重试机制去掉，请求product的接口注释掉
   @Configuration
   public class OrderConfig {

//  @Bean
//  Retryer retryer() {
//    return new Retryer.Default();
//  }

@Bean
Logger.Level feignLoggerLevel() {
return Logger.Level.FULL;
}

@LoadBalanced//注解式负载均衡
@Bean
RestTemplate restTemplate() {
return new RestTemplate();
}
}
@RestController
public class ProductController {

@Autowired
private ProductService productService;

//  @GetMapping("/product/{id}")
//  public Product getProduct(@PathVariable Long id,
//                            HttpServletRequest request) {
//    String token = request.getHeader("X-Token");
//    System.out.println("hello.......token=" + token);
//    return productService.getProduct(id);
//  }
}
整合sentinel
1. pom.xml
   <dependency>
   <groupId>com.alibaba.cloud</groupId>
   <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
   </dependency>
2. application.yaml
   feign:
   sentinel:
   enabled: true
3. 测试请求
- 兜底回调
  [图片]
  [图片]
- 正常调用
  [图片]
  [图片]

# 5. Sentinel
## 5.1 工作原理
   服务保护（限流、熔断降级）
   [图片]
   架构原理：
   [图片]
   资源&规则
   认为所有的web接口都是资源
   [图片]
   工作原理
   [图片]

## 5.2 基础场景
步骤：
1. 下载jar包：https://sourceforge.net/projects/sentinel.mirror/files/1.8.8/sentinel-dashboard-1.8.8.jar/download
   [图片]
2. 在该jar包路径下进入cmd，执行下方启动命令：
   [图片]
3. 进入页面localhost:8080（默认的账号密码就是sentinel）
   [图片]
4. 登录成功，页面跳转
   [图片]
5. 每一个微服务都配置sentinel控制台
- pom.xml
<!--sentinel-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
  - application-feign.yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            logger-level: full
            connect-timeout: 1000
            read-timeout: 2000
          service-product:
            logger-level: full
            connect-timeout: 3000
            read-timeout: 5000
    # sentinel配置控制台
    sentinel:
      transport:
        dashboard: localhost:8080
      # 提前加载，项目一启动就连上sentinel控制台
      eager: true
6. 重新启动服务后，查看dashboard控制台
[图片]
7. 在方法上设置sentinel规则
@Service
public class OrderServiceImpl implements OrderService {

@Autowired
ProductFeignClient productFeignClient;

//声明式限流规则
@SentinelResource(value = "createOrder")
@Override
public Order createOrder(Long productId, Long userId) {
//使用feign完成远程调用
Product product = productFeignClient.getProduct(productId);

    Order order = new Order();
    order.setUserId(userId);
    order.setAddress("米花大楼");
    order.setProductList(Arrays.asList(product));

    return order;
}

}
8. 重新启动服务，dashboard控制台上可以查看到簇点链路
   [图片]
9. 在createOrder上设置流控规则
- 资源名：代表请求路径
- QPS：每秒的请求数量
- 阈值：设置1代表每秒请求次数最多为1
  [图片]
  [图片]
10. 请求/create接口频繁后，会看到
    [图片]
    该请求会被阻塞，目标方法不会被执行

## 5.3 异常处理
[图片]
### 5.3.1 Web接口
默认流控规则返回：
[图片]
如果想要自定义一个返回规则，需要自己写一个handler。
@Component
public class MyBlockExceptionHandler implements BlockExceptionHandler {

private ObjectMapper objectMapper = new ObjectMapper();

@Override
public void handle(HttpServletRequest request, HttpServletResponse response,
String resourceName, BlockException e) throws Exception {
response.setContentType("application/json;charset=utf-8");

    PrintWriter writer = response.getWriter();
    R<Object> r = R.fail(500, resourceName + "被sentinel限制了，原因：" + e.getClass());
    String json = objectMapper.writeValueAsString(r);

    writer.write(json);
    writer.flush();
    writer.close();
}

}
注意：sentinel默认将规则存在内存，重启项目后规则失效，如需要，就得重新定义。
[图片]

### 5.3.2 @SentinelResource
createOrder()方法用注解@SentinelResource标识。如果对该方法增加流控规则，请求频繁后会得到：springboot默认的异常处理机制。
[图片]
- 这里自定义一个全局异常处理器
    - 注意：@ResponseBody + @ControllerAdvice注解可以被@RestControllerAdvice取代。
      //@ResponseBody
      //@ControllerAdvice
      @RestControllerAdvice
      public class GlobalExceptionHandler {
      //  @ExceptionHandler(Throwable.class)
      //  public String handleException(Throwable e) {
      //    return "";
      //  }
      }
      不同项目，异常处理器写法不同。
- 给createOrder()方法设定兜底回调方法
    - blockHandler的值要与兜底回调的方法名一致。
      @Service
      public class OrderServiceImpl implements OrderService {

  @Autowired
  ProductFeignClient productFeignClient;

  @SentinelResource(value = "createOrder", blockHandler = "createOrderFallback")
  @Override
  public Order createOrder(Long productId, Long userId) {
  //使用feign完成远程调用
  Product product = productFeignClient.getProduct(productId);

  Order order = new Order();
  order.setUserId(userId);
  order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));
  order.setAddress("米花大楼");
  order.setProductList(Arrays.asList(product));

  return order;
  }

  //兜底回调
  public Order createOrderFallback(Long productId, Long userId, BlockException e) {
  Order order = new Order();
  order.setId(0L);
  order.setUserId(userId);
  order.setTotalAmount(new BigDecimal("0"));
  order.setAddress("异常信息：" + e.getMessage());
  order.setNickName("未知用户");

  return order;
  }

}
- 频繁请求后得到：
  [图片]
  因此，@SentinelResource一般标注在非controller层。一旦违反规则，规定有兜底回调，就使用blockHandler；如果没规定，就默认使用springboot异常处理机制。

### 5.3.3 OpenFeign
前面写过对ProductFeignClientFallback的兜底回调逻辑，如果对第三方调用增加流控规则，请求频繁后，会走到该兜底回调的逻辑中；如果没有设定兜底回调，就会默认返回springboot的异常处理机制。
@Component
public class ProductFeignClientFallback implements ProductFeignClient {
@Override
public Product getProduct(Long id) {
System.out.println("兜底回调。。。");
//可以造一个假数据
Product product = new Product();
product.setId(id);
product.setPrice(new BigDecimal("0"));
product.setProductName("未知商品");
product.setNum(0);

    return product;
}
}
[图片]
[图片]

### 5.3.4 Sphu硬编码
Sphu.entry()中传入资源名，当违背规则后会进入catch中进行处理。
仅作了解。
[图片]
了解更多可参考文档：https://github.com/alibaba/Sentinel/wiki/%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8

## 5.4 流控规则
[图片]

### 5.4.1 阈值类型
[图片]
一般推荐使用QPS
- QPS
- 并发线程数量（需要配合线程池，性能比较底下）
  [图片]
  另外，如果选择了集群模式，就分为单机均摊和总体阈值；顾名思义，如果有3个机器，单机均摊为5，那么总共可以有15个请求，如果设置总体阈值为5，那么总共只能有5个请求。

### 5.4.2 流控模式
[图片]
- 直接（默认）：对请求直接进行限制
- 关联：
- 链路：根据不同的调用链来限制，比如对普通的创建订单不做限制，对秒杀项目做限制。【仅对某一路径下的访问生效】
  [图片]
  [图片]
#### 5.4.2.1 链路
  @RestController
  public class OrderController {

  @Autowired
  OrderProperties orderProperties;
  @Autowired
  private OrderService orderService;

  @GetMapping("/config")
  public String config() {
  return "order.timeout=" + orderProperties.getTimeout() + ", order.autoConfirm=" +
  orderProperties.getAutoConfirm() + ", order.dbUrl=" + orderProperties.getDbUrl();
  }

  @GetMapping("/create")
  public Order createOrder(Long userId, Long productId) {
  Order order = orderService.createOrder(productId, userId);

  return order;
  }

  @GetMapping("/seckill")
  public Order secKill(Long userId, Long productId) {
  Order order = orderService.createOrder(productId, userId);
  order.setId(Long.MAX_VALUE);

  return order;
  }

}
- 取消上下文
  spring:
  cloud:
  openfeign:
  client:
  config:
  default:
  logger-level: full
  connect-timeout: 1000
  read-timeout: 2000
  service-product:
  logger-level: full
  connect-timeout: 3000
  read-timeout: 5000
  sentinel:
  transport:
  dashboard: localhost:8080
  eager: true
  # 关闭上下文统一
  web-context-unify: false
  feign:
  sentinel:
  enabled: true
- 添加流控
  [图片]
- 正常请求：
  [图片]
  [图片]
- 请求频繁后：
  [图片]

#### 5.4.2.2 关联
当写的请求量大的时候读的限流才会触发读的限流，否则随便读。
[图片]
- 方法
  @GetMapping("/read")
  public String read(){
  return "read  ....";
  }

@GetMapping("/write")
public String write(){
return  "write ....";
}
- 对/read接口做关联流控
  [图片]
- 当对write接口的访问量足够大后，再突然访问read接口，会得到：
  [图片]

#### 5.4.2.3 直接
新增429状态码设置
@Component
public class MyBlockExceptionHandler implements BlockExceptionHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       String resourceName, BlockException e) throws Exception {
        response.setStatus(429);//too many request
        response.setContentType("application/json;charset=utf-8");

        PrintWriter writer = response.getWriter();
        R<Object> r = R.fail(500, resourceName + "被sentinel限制了，原因：" + e.getClass());
        String json = objectMapper.writeValueAsString(r);

        writer.write(json);
        writer.flush();
        writer.close();
    }

}
[图片]


### 5.4.3 流控效果
[图片]
注意：warm up、匀速排队不支持关联和直接模式，只支持直接模式。

#### 5.4.3.1  快速失败
即：直接拒绝请求。
可以使用ApiPOST工具做一键压测。
[图片]

#### 5.4.3.2 Warm up
即：预热/冷启动
[图片]
[图片]
达到峰值 3，需要经过3s的冷启动时间。（第1秒接收1个请求，第2秒接收2个请求）
好处是：请求过大的时候 ，不至于系统还没 反应过来。

#### 5.4.3.3 匀速排队
对应的是漏桶算法。
[图片]
[图片]

## 5.5 熔断规则
熔断 降级是针对发起调用的一方，比如服务G发现服务D调用不了了。就会切断调用，快速返回结果，避免服务挤压，导致雪崩效应。
### 5.5.1 断路器工作原理
[图片]
图中A和B的调用，可以理解为。有一个断路器，默认是闭合状态，能够正常调用，当B请求出问题时候，断路器打开，使得服务调用不通。那如果B服务恢复正常了，如果通知A？这里就还有一种半开状态。
[图片]
半开相当于一个主键态度。
假设设置慢调用比例设置为70%，当达到这个阈值之后，断路器就会打开，假设熔断时间为30秒，那么30秒内 请求都会拒绝，过了30秒后，放开一个请求，如果成功就关掉断路器，否则就还是打开。

### 5.5.2 慢调用比例
比如，在订单业务中设置对商品接口的熔断规则。
- RT：最大响应时间（单位：毫秒）
- 比例阈值：0.0到1.0之间的小数，这里设置0.8代表80%
- 熔断时间：（单位：秒）
- 最小请求数：也就是样本量，最少要发xx个请求，才有统计的必要
- 统计时长：（单位：毫秒）
  代表5秒内，如果有80%的请求响应时间大于1秒，就是慢调用。就代表对面不可靠，即发起熔断30秒。这30秒内的所有请求都不会发给远程服务。
  [图片]
  测试的时候，可以通过休眠来模拟慢调用。
  当触发熔断后，返回的就是feignClient的兜底回调方法。
  [图片]
  注意，远程调用最好都写一个兜底回调。

### 5.5.3 异常比例
[图片]
比如模拟异常：1/0
如果没有配置异常比例，那么每次请求都会先发给远程，发现远程错误后才再调用兜底。
[图片]
如果配置了熔断规则，那么会有一段时间不发送请求给远程，就能省下远程调用时间，节约资源。


### 5.5.4 异常数
相对于异常比例的区别，异常数不需要百分比。
[图片]
表示：假设有千万个请求，5秒内只要有10个（即使1%都不到）是异常的，就触发熔断。只要熔断了，30秒内就不会再发请求。

## 5.6 热点规则
本身是流量控制的一种。相对来说更细节性。
[图片]
对请求的参数做限制。
[图片]
相关场景：
- 秒杀业务中，设置每个用户秒杀QPS不超过1，但是对vvip用户不限制QPS；可以在用户id层面做限制。
- 666号商品是下架商品，不允许访问；可以在商品id层面做限制。
  [图片]

### 5.6.1 热点参数限流
携带此参数的参与流控，不携带不流控
[图片]
这个规则表示，对于第一个参数（假设是userId）设置每秒只能有一个请求通过，也就是任何用户都只能每秒请求一次这个接口。
避免有些请求参数不带userId，可以在注解@RequestParam中设置默认值（defaultValue）
注意，参数是从0开始计数的。
还可以配置参数例外项，设置的参数，可以不参与流控。
[图片]
假设这里6号是vip，就不会被限流。
还可以对访问商品接口设置参数外项，666号商品不允许被访问。

## 5.7 授权规则
[图片]
可以对应用级别设置授权类型为白名单或黑名单。

## 5.8 系统规则
前面的规则都是对资源的控制，系统规则是从系统角度出发。
[图片]
应用重启后规则会失效。


# 6. Gateway
   网关的三个核心概念：路由、断言、过滤器
   [图片]

## 6.1 网关功能
- 统一入口：是所有业务集群请求的入口。前端不需要记住每一个微服务的地址，只需要记住网关一个地址。
- 请求路由：交给网关的请求，会自动判断请求要交给哪个业务。
- 负载均衡：均衡集群中每一个服务的负载量。
- 流量控制：结合sentinel实现全局的限流。
- 身份认证：针对部分需要登录的业务，如果是未登录的用户，可以将请求打回。
- 协议转换：可以将json数据转为grpc数据模型。
- 系统监控：可以监控每一个请求从收到到处理完成的时间，统计慢请求，当天的访问总量等。
- 安全防护：防止跨站脚本伪造、跨站脚本攻击、sql注入等常见安全问题。

需求：
1. 客户端发送/api/order/**转到service-order
2. 客户端发送/api/product/**转到service-product
3. 以上转发有负载均衡效果

## 6.2 创建网关
步骤：
1. 新建一个module
2. 引入依赖：
<!--nacos-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<!--gateway-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<!--loadbalancer-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
3. 编写启动类：
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayMainApplication {
  public static void main(String[] args) {
    SpringApplication.run(GatewayMainApplication.class, args);
  }
}
4. 编写配置文件
  - 配置80端口就可以再请求路径中不加上端口，直接localhost就行
spring:
  application:
    name: gateway
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848

server:
port: 80
5. 启动应用：
   [图片]
6. 直接输入localhost会得到
   [图片]
7. nocos界面
   [图片]


## 6.3 路由
### 6.3.1 规则配置
lb代表loadbalance，负载均衡。
注意，一定要引入loadbalance依赖，否则接口报503服务不可达。
1. 配置文件
- application.yaml
  spring:
  profiles:
  include: route
  application:
  name: gateway
  cloud:
  nacos:
  server-addr: 127.0.0.1:8848


server:
port: 80
- aoolication-route.yaml
  spring:
  cloud:
  gateway:
  routes:
  - id: order-route
  uri: lb://service-order
  predicates:
  - Path=/api/order/**
  - id: product-route
  uri: lb://service-product
  predicates:
  - Path=/api/product/**
2. 将order和product控制器中加入请求前缀，否则会报404
   @RequestMapping("/api/order")
   @RequestMapping("/api/product")
3. 另外，远程调用中也需要补充
   /**
* 用来调用商品服务的客户端.
  */
  @FeignClient(value = "service-product", fallback = ProductFeignClientFallback.class)
  public interface ProductFeignClient {

@GetMapping("/api/product/product/{id}")
Product getProduct(@PathVariable Long id);

}
4. 请求成功
   [图片]
   [图片]
5. 此外，应用创建多个服务也能实现负载均衡
   [图片]
   [图片]

### 6.3.2 工作原理
[图片]
注意路径规则的顺序，application-route.yaml中的请求路径一旦匹配上了就会不会继续后续的匹配。

下方配置表示，除了前两个，其他请求会路由到bing页面。
spring:
cloud:
gateway:
routes:
- id: order-route
uri: lb://service-order
predicates:
- Path=/api/order/**
- id: product-route
uri: lb://service-product
predicates:
- Path=/api/product/**
- id: bing-route
uri: https://cn.bing.com
predicates:
- Path=/api/product/**
另外还可以配置order来表示优先级0、1、2...
spring:
cloud:
gateway:
routes:
- id: order-route
uri: lb://service-order
# 断言
predicates:
- Path=/api/order/**
order: 0
- id: product-route
uri: lb://service-product
predicates:
- Path=/api/product/**
order: 1
- id: bing-route
uri: https://cn.bing.com
predicates:
- Path=/api/product/**
order: 2

## 6.4 断言
### 6.4.1 长短写法
前面用的是短写法，现在介绍下长写法
[图片]
[图片]
matchTrailingSlash表示全匹配，如果设置true就不会很严格，设置false就会很严格，默认设置true。
- 短写法示例：
  spring:
  cloud:
  gateway:
  routes:
  - id: product-route
  uri: lb://service-product
  predicates:
  - Path=/api/product/**
  order: 1
- 长写法示例
  spring:
  cloud:
  gateway:
  routes:
  - id: product-route
  uri: lb://service-product
  predicates:
  - name: Path
  args:
  patterns: /api/order/**
  matchTrailingSlash: true

### 6.4.2 Query
[图片]
spring:
cloud:
gateway:
routes:
- id: bing-route
uri: https://cn.bing.com
predicates:
- name: Path
args:
pattern: /search
- name: Query
args:
param: q
regexp: haha
order: 2
这样设置表示，当请求路径localhost/search，并且参数q等于haha时，请求会跳转至https://cn.bing.com
[图片]

### 6.4.3 自定义断言工厂
需要自己写一个断言工厂，可以参考QueryRoutePredicateFactory
注意断言名字不能瞎写，如果想要配置Vip，那么断言工厂的名字前缀就得是Vip，全称：VipRoutePredicateFactory
package com.whl.cloud.gateway.predicate;

import io.netty.util.internal.StringUtil;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class VipRoutePredicateFactory extends AbstractRoutePredicateFactory<VipRoutePredicateFactory.Config> {

public VipRoutePredicateFactory() {
super(Config.class);
}

/**
* 满足什么规则.
  */
  @Override
  public Predicate<ServerWebExchange> apply(Config config) {
  return new GatewayPredicate() {
  @Override
  public boolean test(ServerWebExchange serverWebExchange) {
  //localhost/search?q=haha&user=cloud
  ServerHttpRequest request = serverWebExchange.getRequest();
  String first = request.getQueryParams().getFirst(config.param);

  return StringUtils.hasText(first) && first.equals(config.value);
  }
  };
  }

/**
* 表示短写法的顺序.
  */
  @Override
  public List<String> shortcutFieldOrder() {
  return Arrays.asList("param", "value");
  }

/**
* 可以配置的参数.
  */
  @Validated
  public static class Config {

    @NotEmpty
    private String param;

    @NotEmpty
    private String value;

    public String getParam() {
      return param;
    }

    public void setParam(String param) {
      this.param = param;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
}
}
[图片]

## 6.5 过滤器
[图片]

### 6.5.1 基本使用
#### 6.5.1.1 路径重写
[图片]
使得路径重写后和控制器里面的路径一致，无需将控制器中的接口再加上请求前缀。
1. 将order和product的控制器前缀/api/order、/api/product去掉
2. 配置路径重写过滤器
   spring:
   cloud:
   gateway:
   routes:
   - id: order-route
   uri: lb://service-order
   predicates:
   - Path=/api/order/**
   filters:
   - RewritePath=/api/order/?(?<segment>.*), /$\{segment}
   order: 0
   - id: product-route
   uri: lb://service-product
   predicates:
   - name: Path
   args:
   patterns: /api/order/**
   matchTrailingSlash: true
   order: 1

3. 请求成功
   [图片]

#### 6.5.1.2 添加响应头
- 不添加响应头
  [图片]
- 添加响应头
  spring:
  cloud:
  gateway:
  routes:
  - id: order-route
  uri: lb://service-order
  predicates:
  - Path=/api/order/**
  filters:
  - RewritePath=/api/order/?(?<segment>.*), /$\{segment}
  # 添加响应头
  - AddResponseHeader=X-Response-Abc, 123
  order: 0
  [图片]

### 6.5.2 默认filter
[图片]
配置后每一个路由规则都会使用到，每一个请求响应都会带响应头。
spring:
cloud:
gateway:
routes:
- id: order-route
uri: lb://service-order
predicates:
- Path=/api/order/**
filters:
- RewritePath=/api/order/?(?<segment>.*), /$\{segment}
#            - AddResponseHeader=X-Response-Abc, 123
          order: 0
        - id: product-route
          uri: lb://service-product
          predicates:
            - name: Path
              args:
                patterns: /api/product/**
                matchTrailingSlash: true
          filters:
            - RewritePath=/api/product/?(?<segment>.*), /$\{segment}
          #            - AddResponseHeader=X-Response-Abc, 123
          order: 1
        - id: bing-route
          uri: https://cn.bing.com
          predicates:
            - name: Path
              args:
                pattern: /search
            - name: Query
              args:
                param: q
                regexp: haha
#            短写法
#            - Vip=user,cloud
#            长写法
            - name: Vip
              args:
                param: user
                value: cloud
          order: 2
      default-filters:
        - AddResponseHeader=X-Response-Abc, 123


### 6.5.3 GlobalFilters
即对所有请求都生效的filter。
如果自定义，需要实现
[图片]
编写一个通用过滤器：
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RtGlobalFilter implements GlobalFilter, Ordered {

@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
ServerHttpRequest request = exchange.getRequest();
ServerHttpResponse response = exchange.getResponse();

    String uri = request.getURI().toString();
    long start = System.currentTimeMillis();
    log.info("请求【{}】开始时间：{}", uri, start);
    //------------------------以上前置逻辑-------------------------------

    Mono<Void> filter = chain.filter(exchange)
          .doFinally((result)-> {
            //------------------------以下后置逻辑--------------------------------------
            long end = System.currentTimeMillis();
            log.info("请求【{}】结束时间{}，耗时：{}ms", uri, end, end - start);
          });//放行

    return filter;
}

@Override
public int getOrder() {
return 0;
}
}
注意这里放行是异步放行。如果要在请求执行完成后再执行，可以放在doFinally里面。
[图片]

### 6.5.4 自定义网关过滤器工厂
需要继承AbstractNameValueGatewayFilterFactory
[图片]
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class OnceTokenGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
@Override
public GatewayFilter apply(NameValueConfig config) {
return new GatewayFilter() {

      @Override
      public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //每次响应之前，添加一个一次性令牌，支持uuid、jwt等格式

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
          ServerHttpResponse response = exchange.getResponse();
          HttpHeaders headers = response.getHeaders();
          String value = config.getValue();
          if ("uuid".equalsIgnoreCase(value)) {
            value = UUID.randomUUID().toString();
          }
          if ("jwt".equalsIgnoreCase(value)) {
            //生成jwt的逻辑，这里不写具体逻辑。。
            value = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEyMzQ1LCJ1c2VybmFtZSI6ImpvaG5fZG9lIiwicm9sZSI6ImFkbWluIiwic3ViIjoiam9obl9kb2UiLCJpc3MiOiJteWFwcCIsImlhdCI6MTcxMDAwMDAwMCwiZXhwIjoxNzEwMDg2NDAwfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
          }
          headers.add(config.getName(), value);

        }));
      }
    };
}
}
spring:
cloud:
gateway:
routes:
- id: order-route
uri: lb://service-order
predicates:
- Path=/api/order/**
filters:
- RewritePath=/api/order/?(?<segment>.*), /$\{segment}
# 响应token
- OnceToken=X-Response-Token, uuid

[图片]

## 6.6 扩展
### 6.6.1 全局跨域
可以在网关上设置统一跨域。
[图片]
- 全局跨域【一般配置允许全局跨域】
  [图片]
- 某一种路由规则下跨域
  [图片]
  这里举例全局跨域配置
  spring:
  cloud:
  gateway:
  globalcors:
  cors-configurations:
  '[/**]':
  allowed-origin-patterns: '*'
  allowed-headers: '*'
  allowed-methods: '*'

[图片]
F12出现允许跨域项。

注意，微服务之间的调用是不经过网关的。只需要在在注册中心找到服务，就可以调用。

# 7. Seata
   分布式事务。
   [图片]
   假设一个购买业务涉及到三个微服务，每个微服务涉及三个独立的数据库，如果最后一步执行失败了，前面两个也应该失败。需要共同控制多个事务的提交回滚。

## 7.1 基础
### 7.1.1 环境搭建
[图片]
步骤：
1. 创建四个module，拷贝代码
   seata-account
   seata-business
   seata-order
   seata-storage
2. 运行sql文件
   CREATE DATABASE IF NOT EXISTS `storage_db`;
   USE  `storage_db`;
   DROP TABLE IF EXISTS `storage_tbl`;
   CREATE TABLE `storage_tbl` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `commodity_code` varchar(255) DEFAULT NULL,
   `count` int(11) DEFAULT 0,
   PRIMARY KEY (`id`),
   UNIQUE KEY (`commodity_code`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
   INSERT INTO storage_tbl (commodity_code, count) VALUES ('P0001', 100);
   INSERT INTO storage_tbl (commodity_code, count) VALUES ('B1234', 10);

-- 注意此处0.3.0+ 增加唯一索引 ux_undo_log
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`branch_id` bigint(20) NOT NULL,
`xid` varchar(100) NOT NULL,
`context` varchar(128) NOT NULL,
`rollback_info` longblob NOT NULL,
`log_status` int(11) NOT NULL,
`log_created` datetime NOT NULL,
`log_modified` datetime NOT NULL,
`ext` varchar(100) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE DATABASE IF NOT EXISTS `order_db`;
USE  `order_db`;
DROP TABLE IF EXISTS `order_tbl`;
CREATE TABLE `order_tbl` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`user_id` varchar(255) DEFAULT NULL,
`commodity_code` varchar(255) DEFAULT NULL,
`count` int(11) DEFAULT 0,
`money` int(11) DEFAULT 0,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 注意此处0.3.0+ 增加唯一索引 ux_undo_log
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`branch_id` bigint(20) NOT NULL,
`xid` varchar(100) NOT NULL,
`context` varchar(128) NOT NULL,
`rollback_info` longblob NOT NULL,
`log_status` int(11) NOT NULL,
`log_created` datetime NOT NULL,
`log_modified` datetime NOT NULL,
`ext` varchar(100) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE DATABASE IF NOT EXISTS `account_db`;
USE  `account_db`;
DROP TABLE IF EXISTS `account_tbl`;
CREATE TABLE `account_tbl` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`user_id` varchar(255) DEFAULT NULL,
`money` int(11) DEFAULT 0,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO account_tbl (user_id, money) VALUES ('1', 10000);
-- 注意此处0.3.0+ 增加唯一索引 ux_undo_log
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`branch_id` bigint(20) NOT NULL,
`xid` varchar(100) NOT NULL,
`context` varchar(128) NOT NULL,
`rollback_info` longblob NOT NULL,
`log_status` int(11) NOT NULL,
`log_created` datetime NOT NULL,
`log_modified` datetime NOT NULL,
`ext` varchar(100) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
3. 下载seata包
- 地址：https://seata.apache.org/zh-cn/download/seata-server/