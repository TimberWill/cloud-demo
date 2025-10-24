package com.whl.cloud.service.impl;

import com.whl.cloud.product.bean.Product;
import com.whl.cloud.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {
  @Override
  public Product getProduct(Long id) {
    Product product = new Product();
    product.setId(id);
    product.setProductName("香蕉");
    product.setNum(1);
    product.setPrice(BigDecimal.valueOf(5));
    return product;
  }
}
