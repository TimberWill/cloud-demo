package com.whl.cloud;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import java.util.List;

@SpringBootTest
public class DiscoveryTest {

  @Autowired
  DiscoveryClient discoveryClient;

  @Autowired
  NacosServiceDiscovery nacosServiceDiscovery;

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
