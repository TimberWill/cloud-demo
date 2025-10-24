package com.whl.cloud.feign;

import com.whl.cloud.feign.fallback.ProductFeignClientFallback;
import com.whl.cloud.product.bean.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用来调用商品服务的客户端.
 */
@FeignClient(value = "service-product", fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {

  @GetMapping("/api/product/product/{id}")
  Product getProduct(@PathVariable Long id);

}
