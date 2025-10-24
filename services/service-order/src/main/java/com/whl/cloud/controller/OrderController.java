package com.whl.cloud.controller;

import com.whl.cloud.order.bean.Order;
import com.whl.cloud.properties.OrderProperties;
import com.whl.cloud.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("/api/order")
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

  @GetMapping("/read")
  public String read() {
    return "read ..";
  }

  @GetMapping("/write")
  public String write() {
    return "write ..";
  }

}
