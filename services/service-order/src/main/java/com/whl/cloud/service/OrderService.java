package com.whl.cloud.service;

import com.whl.cloud.order.bean.Order;

public interface OrderService {
  Order createOrder(Long productId, Long userId);
}
