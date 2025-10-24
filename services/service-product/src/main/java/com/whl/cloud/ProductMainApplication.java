package com.whl.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductMainApplication {
  public static void main(String[] args) {
    System.out.println("Hello, product main application!");
    SpringApplication.run(ProductMainApplication.class, args);
  }
}