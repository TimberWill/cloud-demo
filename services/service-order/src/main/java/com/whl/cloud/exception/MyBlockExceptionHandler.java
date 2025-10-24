package com.whl.cloud.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.cloud.common.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
public class MyBlockExceptionHandler implements BlockExceptionHandler {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
                     String resourceName, BlockException e) throws Exception {
    response.setContentType("application/json;charset=utf-8");

    PrintWriter writer = response.getWriter();
    R<Object> r = R.fail(500, resourceName + "被sentinel限制了，原因：" + e.getClass());
    String json = objectMapper.writeValueAsString(r);

    writer.write(json);
    writer.flush();
    writer.close();
  }

}
