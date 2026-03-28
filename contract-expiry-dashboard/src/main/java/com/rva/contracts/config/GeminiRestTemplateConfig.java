package com.rva.contracts.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GeminiRestTemplateConfig {

  @Bean
  @Qualifier("geminiRestTemplate")
  public RestTemplate geminiRestTemplate(RestTemplateBuilder builder, GeminiProperties props) {
    return builder
        .setConnectTimeout(props.getConnectTimeout())
        .setReadTimeout(props.getReadTimeout())
        .build();
  }
}
