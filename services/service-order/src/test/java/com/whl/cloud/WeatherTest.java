package com.whl.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.cloud.feign.WeatherFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class WeatherTest {

  @Autowired
  WeatherFeignClient weatherFeignClient;

  @Test
  void getWeather() {
    String weather = weatherFeignClient.getWeather("APPCODE 93b7e19861a24c519a7548b17dc16d75",
      "50b53ff8dd7d9fa320d3d3ca32cf8ed1",
      "2182");

    System.out.println(weather);

  }

  @Test
  void getWeather2() throws JsonProcessingException {
    List<Long> paramsList = new ArrayList<>();
    for (long i = 2; i <= 20001; i++) {
      paramsList.add(i);
    }

    ObjectMapper mapper = new ObjectMapper();
    String jsonBody = mapper.writeValueAsString(paramsList);
    System.out.println(jsonBody);
  }


}
