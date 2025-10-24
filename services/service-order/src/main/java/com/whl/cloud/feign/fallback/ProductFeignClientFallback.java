package com.whl.cloud.feign.fallback;

import com.whl.cloud.feign.ProductFeignClient;
import com.whl.cloud.product.bean.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
