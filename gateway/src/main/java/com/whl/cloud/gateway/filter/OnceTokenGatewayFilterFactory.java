package com.whl.cloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class OnceTokenGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
  @Override
  public GatewayFilter apply(NameValueConfig config) {
    return new GatewayFilter() {

      @Override
      public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //每次响应之前，添加一个一次性令牌，支持uuid、jwt等格式

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
          ServerHttpResponse response = exchange.getResponse();
          HttpHeaders headers = response.getHeaders();
          String value = config.getValue();
          if ("uuid".equalsIgnoreCase(value)) {
            value = UUID.randomUUID().toString();
          }
          if ("jwt".equalsIgnoreCase(value)) {
            //生成jwt的逻辑，这里不写具体逻辑。。
            value = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEyMzQ1LCJ1c2VybmFtZSI6ImpvaG5fZG9lIiwicm9sZSI6ImFkbWluIiwic3ViIjoiam9obl9kb2UiLCJpc3MiOiJteWFwcCIsImlhdCI6MTcxMDAwMDAwMCwiZXhwIjoxNzEwMDg2NDAwfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
          }
          headers.add(config.getName(), value);

        }));
      }
    };
  }
}
