package com.whl.cloud;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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