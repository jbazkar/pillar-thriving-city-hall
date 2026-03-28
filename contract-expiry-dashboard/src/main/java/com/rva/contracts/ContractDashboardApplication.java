package com.rva.contracts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ContractDashboardApplication {

  public static void main(String[] args) {
    boolean bootstrap = Arrays.asList(args).contains("bootstrap");
    SpringApplicationBuilder builder = new SpringApplicationBuilder(ContractDashboardApplication.class);
    if (bootstrap) {
      builder.properties(Map.of("app.bootstrap", "true", "spring.main.web-application-type", "none"));
      try (ConfigurableApplicationContext ctx = builder.run(args)) {
        int code = SpringApplication.exit(ctx, () -> 0);
        System.exit(code);
      }
    } else {
      builder.run(args);
    }
  }
}
