package com.whl.cloud.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.whl.cloud.feign.ProductFeignClient;
import com.whl.cloud.order.bean.Order;
import com.whl.cloud.product.bean.Product;
import com.whl.cloud.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

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
