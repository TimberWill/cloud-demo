package com.whl.cloud.gateway.predicate;

import io.netty.util.internal.StringUtil;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class VipRoutePredicateFactory extends AbstractRoutePredicateFactory<VipRoutePredicateFactory.Config> {

  public VipRoutePredicateFactory() {
    super(Config.class);
  }

  /**
   * 满足什么规则.
   */
  @Override
  public Predicate<ServerWebExchange> apply(Config config) {
    return new GatewayPredicate() {
      @Override
      public boolean test(ServerWebExchange serverWebExchange) {
        //localhost/search?q=haha&user=cloud
        ServerHttpRequest request = serverWebExchange.getRequest();
        String first = request.getQueryParams().getFirst(config.param);

        return StringUtils.hasText(first) && first.equals(config.value);
      }
    };
  }

  /**
   * 表示短写法的顺序.
   */
  @Override
  public List<String> shortcutFieldOrder() {
    return Arrays.asList("param", "value");
  }

  /**
   * 可以配置的参数.
   */
  @Validated
  public static class Config {

    @NotEmpty
    private String param;

    @NotEmpty
    private String value;

    public String getParam() {
      return param;
    }

    public void setParam(String param) {
      this.param = param;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
