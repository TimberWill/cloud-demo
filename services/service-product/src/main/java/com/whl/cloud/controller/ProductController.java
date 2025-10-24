package com.whl.cloud.controller;

import com.whl.cloud.product.bean.Product;
import com.whl.cloud.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("/api/product")
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
